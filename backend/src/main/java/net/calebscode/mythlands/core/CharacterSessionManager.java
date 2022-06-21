package net.calebscode.mythlands.core;

import java.util.HashMap;
import java.util.concurrent.Future;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import net.calebscode.mythlands.dto.MythlandsCharacterDTO;
import net.calebscode.mythlands.dto.MythlandsUserDTO;
import net.calebscode.mythlands.exception.NoActiveCharacterException;
import net.calebscode.mythlands.exception.UserNotFoundException;
import net.calebscode.mythlands.messages.out.CharacterUpdateMessage;
import net.calebscode.mythlands.service.MythlandsCharacterService;
import net.calebscode.mythlands.service.MythlandsUserService;

@Component
public class CharacterSessionManager {
	
	public static final int SERVER_TICK_RATE = 20;
	public static final int SERVER_TICK_DURATION = 1000 / SERVER_TICK_RATE;
	public static final int HERO_UPDATE_INTERVAL = 5000;

	@Autowired private MythlandsUserService userService;
	@Autowired private MythlandsCharacterService characterService;
	@Autowired private SimpMessagingTemplate messenger;
	@Autowired private Gson gson;
	
	private Object activeHeroLock = new Object();
	private Thread heroUpdateThread;
	private HashMap<String, Integer> users = new HashMap<>();
	
	@EventListener
	public void onApplicationEvent(SessionConnectedEvent event) {		
		if(event.getUser() != null) {
			MythlandsUserDTO info;
			try {
				info = userService.getUserInfo(event.getUser().getName());
			} catch (UserNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
			final String username = info.username;
			synchronized(activeHeroLock) {
				if(!users.containsKey(username)) {
					
					System.out.printf("%s connected.\n", username);
					users.put(username, 0);
					
					// If the users map size is now one, we need to start the update thread.
					if(users.size() == 1) {
						System.out.println("First player connected. Starting update thread.");
						heroUpdateThread = new Thread(new HeroUpdateRunnable(), "HeroUpdateThread");
						heroUpdateThread.start();
					}
					
				}		
			}
		}
	}
	
	@EventListener
	public void onApplicationEvent(SessionDisconnectEvent event) {
		if(event.getUser() != null) {
			MythlandsUserDTO info;
			try {
				info = userService.getUserInfo(event.getUser().getName());
			} catch (UserNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
			final String username = info.username;
			synchronized(activeHeroLock) {
				users.remove(username);
				System.out.printf("%s disconnected.\n", username);
				if(users.size() == 0) {
					
					heroUpdateThread.interrupt();
					try {
						heroUpdateThread.join();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
				}
			}
		}
	}

	private class HeroUpdateRunnable implements Runnable {

		int tickTimer = 0;
		
		@Override
		public void run() {			
			long prevTime = System.currentTimeMillis();
			long start, finish, delta;
			
			while(true) {
				start = System.currentTimeMillis();
				delta = start - prevTime;
				
				tickTimer += delta;
				
				// While the server tick timer is greater than the server
				// tick duration, we need to do an update.
				while(tickTimer > SERVER_TICK_DURATION) {
					
					synchronized(activeHeroLock) {
						// Loop through all active characters.
						for(String username : users.keySet()) {
							
							// Get the character's timer and increment it by one server tick duration.
							int userTimer = users.get(username);
							userTimer += SERVER_TICK_DURATION;
							
							// If the timer exceeds the update interval, update that hero and
							// adjust the timer accordingly.
							if(userTimer > HERO_UPDATE_INTERVAL) {
								updateHero(username);
								userTimer -= HERO_UPDATE_INTERVAL;
							}
							
							users.put(username, userTimer);
							
						}
					}
					
					// We've executed a server tick, so remove one tick duration
					// from the tick timer.
					tickTimer -= SERVER_TICK_DURATION;
				}
				
				prevTime = start;
				
				try {
					Thread.sleep(Math.max(0, SERVER_TICK_DURATION - delta));
				} catch (InterruptedException e) {
					System.out.printf("All characters disconnected. Stopping update thread.\n");
					return;
				}
			}
		}
		
		void updateHero(String username) {
			try {
				MythlandsCharacterDTO heroDto;
				try {
					heroDto = userService.getActiveCharacter(username);
				} catch (NoActiveCharacterException e) {
					return;
				}
					
				if(heroDto.isDeceased) {
					return;
				}
				
				double regenAmount = heroDto.maxHealth * 0.05;
				JsonObject hpRegen = characterService.gainHealth(heroDto.id, regenAmount);
				JsonObject manaRegen = characterService.gainMana(heroDto.id, regenAmount);
				heroDto = userService.getActiveCharacter(username);
				
				// If the hp/mana regen changed anything, send the updates
				if(hpRegen.entrySet().size() > 0 || manaRegen.entrySet().size() > 0) {
					messenger.convertAndSendToUser(username, "/local/character", 
							gson.toJson(new CharacterUpdateMessage(hpRegen, manaRegen)));
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println(e.getMessage());
			}
		}
		
	}
	
}
