package net.mythlands.core;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import net.mythlands.dto.MythlandsCharacterDTO;
import net.mythlands.dto.MythlandsUserDTO;
import net.mythlands.event.CharacterStatsUpdateEvent;
import net.mythlands.exception.MythlandsServiceException;
import net.mythlands.response.CharacterEffectsRO;
import net.mythlands.response.CharacterInfoRO;
import net.mythlands.response.CharacterStatsRO;
import net.mythlands.response.TimestampedRO;
import net.mythlands.service.MythlandsGameService;
import net.mythlands.service.MythlandsUserService;

@Component
public class CharacterSessionManager {
	
	public static final int SERVER_TICK_RATE = 20;
	public static final int SERVER_TICK_DURATION = 1000 / SERVER_TICK_RATE;
	public static final int HERO_UPDATE_INTERVAL = 5000;

	@Autowired private MythlandsUserService userService;
	@Autowired private MythlandsGameService gameService;
	@Autowired private ApplicationEventPublisher eventPublisher;
	@Autowired private SimpMessagingTemplate messenger;
	
	private Object activeUsersLock = new Object();
	private Thread heroUpdateThread;
	private HashMap<String, Integer> users = new HashMap<>();
	
	@EventListener
	public void onApplicationEvent(SessionConnectedEvent event) {		
		if(event.getUser() != null) {
			
			// Get the current user's information
			MythlandsUserDTO info;
			try {
				info = userService.getUserInfo(event.getUser().getName());
			} catch (MythlandsServiceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
			
			// Insert the user into the active user's map
			final String username = info.username;
			synchronized(activeUsersLock) {
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
			
			// Send the user's character information (if they have an active character)
//			try {
//				MythlandsCharacterDTO character = userService.getActiveCharacter(username);
//				var characterInfo = new TimestampedRO<CharacterInfoRO>(new CharacterInfoRO(character));
//				var characterStats = new TimestampedRO<CharacterStatsRO>(new CharacterStatsRO(character));
//				var characterEffects = new TimestampedRO<CharacterEffectsRO>(new CharacterEffectsRO(character));
//				
//				messenger.convertAndSendToUser(username, "/local/character.info", characterInfo);
//				messenger.convertAndSendToUser(username, "/local/character.stats", characterStats);
//				messenger.convertAndSendToUser(username, "/local/character.effects", characterEffects);
//			} catch (MythlandsServiceException e) {
//				e.printStackTrace();
//			}
			
		}
	}
	
	@EventListener
	public void onApplicationEvent(SessionDisconnectEvent event) {
		if(event.getUser() != null) {
			
			// Get the current user's information
			MythlandsUserDTO info;
			try {
				info = userService.getUserInfo(event.getUser().getName());
			} catch (MythlandsServiceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
			
			// Remove them from the active user's list
			final String username = info.username;
			synchronized(activeUsersLock) {
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
			long start, delta;
			
			// Main loop
			while(true) {
				
				// Get current iteration time and calculate delta
				start = System.currentTimeMillis();
				delta = start - prevTime;
				
				// Increase the timer for ticks
				tickTimer += delta;
				
				// While the server tick timer is greater than the server
				// tick duration, we need to do an update.
				while(tickTimer > SERVER_TICK_DURATION) {
					
					synchronized(activeUsersLock) {
						
						// Loop through all active characters
						users.keySet().parallelStream().forEach((username) -> {							
							// Get the character's timer and increment it by one server tick duration.
							int userTimer = users.get(username);
							userTimer += SERVER_TICK_DURATION;				
							
							// Update the user's character
							try {
								var characterDto = userService.getActiveCharacter(username);
								gameService.updateStatusEffects(username);
								
								// If the timer exceeds the regen interval, update that hero and
								// adjust the timer accordingly.
								if(userTimer > HERO_UPDATE_INTERVAL) {
									doHeroRegen(characterDto);
									userTimer -= HERO_UPDATE_INTERVAL;
								}
							} catch (MythlandsServiceException e) {
								// This can be safely ignored.
							}						
							
							// Update the user's update timer
							users.put(username, userTimer);
						});
						
					}
					
					// We've executed a server tick, so remove one tick duration
					// from the tick timer.
					tickTimer -= SERVER_TICK_DURATION;
				}
				
				// Set the previous time for delta calculation in next loop
				prevTime = start;
				
				// Sleep until next tick is necessary
				try {
					Thread.sleep(Math.max(0, SERVER_TICK_DURATION - delta));
				} catch (InterruptedException e) {
					System.out.printf("All characters disconnected. Stopping update thread.\n");
					return;
				}
			}
		}
		
		void doHeroRegen(MythlandsCharacterDTO character) {
			try {					
				// Make sure the hero is alive before doing an update
				if(character.isDeceased) {
					return;
				}
				
				// Regen health and mana
				// TODO: proper regen mechanics
				double hpAmount = character.maxHealth * 0.1;
				double manaAmount = character.maxMana * 0.1;
				boolean update = gameService.modifyHealth(character.id, hpAmount);
				update |= gameService.modifyMana(character.id, manaAmount);
				
				// If the hp/mana regen changed anything, send the updates
				if(update) {
					character = userService.getActiveCharacter(character.owner);
					eventPublisher.publishEvent(new CharacterStatsUpdateEvent(character));
				}
				
			} catch (MythlandsServiceException e) {
				e.printStackTrace();
				System.err.println(e.getMessage());
			}
		}
		
	}
	
}
