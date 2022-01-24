package net.calebscode.heroland.core;

import java.util.HashMap;
import java.util.concurrent.Future;

import javax.transaction.Transactional;

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

import net.calebscode.heroland.exception.UserNotFoundException;
import net.calebscode.heroland.messages.out.CharacterUpdateMessage;
import net.calebscode.heroland.response.dto.HerolandCharacterDTO;
import net.calebscode.heroland.response.dto.UserInfo;
import net.calebscode.heroland.service.HerolandCharacterService;
import net.calebscode.heroland.service.HerolandUserService;

@Component
public class CharacterSessionManager {
	
	public static final int HERO_UPDATE_INTERVAL = 5000;

	@Autowired private HerolandUserService userService;
	@Autowired private HerolandCharacterService characterService;
	@Autowired private SimpMessagingTemplate messenger;
	@Autowired private Gson gson;
	
	@Autowired
	@Qualifier("herolandTaskExecutor")
	private ThreadPoolTaskExecutor executor;
	private HashMap<String, Future<?>> futures = new HashMap<>();
	
	@EventListener
	public void onApplicationEvent(SessionConnectedEvent event) {		
		if(event.getUser() != null) {
			UserInfo info;
			try {
				info = userService.getUserInfo(event.getUser().getName());
			} catch (UserNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
			final String username = info.username;
			if(!futures.containsKey(username)) {
				
				System.out.printf("%s connected. Starting update thread.\n", username);
				Future<?> updateFuture = executor.submit(new HeroUpdateRunnable(username));
				futures.put(username, updateFuture);
				
			}
		}
	}
	
	//@EventListener
	public void onApplicationEvent(SessionDisconnectEvent event) {
		if(event.getUser() != null) {
			UserInfo info;
			try {
				info = userService.getUserInfo(event.getUser().getName());
			} catch (UserNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
			final String username = info.username;
			Future<?> updateTask = futures.get(username);
			if(updateTask != null) {
				updateTask.cancel(true);
				futures.remove(username);
			}
		}
	}

	private class HeroUpdateRunnable implements Runnable {

		String username;
		int updateTimer = 0;
		
		HeroUpdateRunnable(String username) {
			this.username = username;
		}
		
		@Override
		public void run() {			
			long prevTime = System.currentTimeMillis();
			long currentTime, delta;
			
			while(true) {
				currentTime = System.currentTimeMillis();
				delta = currentTime - prevTime;
				
				updateTimer += delta;
				
				while(updateTimer > HERO_UPDATE_INTERVAL) {
					updateHero();
				}
				
				prevTime = currentTime;
				
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					System.out.printf("%s disconnected. Stopping update thread.\n", username);
					return;
				}
			}
		}
		
		void updateHero() {
			try {
				updateTimer -= HERO_UPDATE_INTERVAL;
				HerolandCharacterDTO heroDto = userService.getActiveCharacter(username);
				
				if(heroDto.isDeceased) {
					return;
				}
				
				System.out.printf("%s regenning from %.2f\n", heroDto.firstName, heroDto.currentHealth);
				double regenAmount = 1 + (heroDto.level - 1) * 0.5;
				JsonObject hpRegen = characterService.gainHealth(heroDto.id, regenAmount);
				JsonObject manaRegen = characterService.gainMana(heroDto.id, regenAmount);
				heroDto = userService.getActiveCharacter(username);
				System.out.printf("%s regenned to %.2f (+%.2f)\n", heroDto.firstName, heroDto.currentHealth, regenAmount);
				
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
