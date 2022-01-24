package net.calebscode.heroland.controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import net.calebscode.heroland.core.Boss;
import net.calebscode.heroland.core.ContributionInfo;
import net.calebscode.heroland.exception.CharacterNotFoundException;
import net.calebscode.heroland.exception.NoActiveCharacterException;
import net.calebscode.heroland.exception.UserNotFoundException;
import net.calebscode.heroland.messages.in.AttackMessage;
import net.calebscode.heroland.messages.out.BossDiedMessage;
import net.calebscode.heroland.messages.out.BossStatusMessage;
import net.calebscode.heroland.messages.out.CharacterUpdateMessage;
import net.calebscode.heroland.messages.out.CooldownMessage;
import net.calebscode.heroland.messages.out.ErrorMessage;
import net.calebscode.heroland.response.dto.HerolandCharacterDTO;
import net.calebscode.heroland.service.HerolandCharacterService;
import net.calebscode.heroland.service.HerolandUserService;

@Controller
public class BossController {
	
	@Autowired private HerolandCharacterService characterService;
	@Autowired private HerolandUserService userService;
	@Autowired private SimpMessagingTemplate messenger;
	@Autowired private Gson gson;
	
	private Random random = new Random();
	private int bossCounter = 1;
	private Boss boss = new Boss("Boss 1", 15);
	
	private HashMap<Integer, ContributionInfo> contribs = new HashMap<>(); 
	
	private static Object bossLock = new Object();

	@MessageMapping("/attack")
	public void attack(AttackMessage attack, Principal principal) {		
		// Get the attacking user's hero
		HerolandCharacterDTO heroDto;
		try {
			heroDto = userService.getActiveCharacter(principal.getName());
		} catch (UserNotFoundException | NoActiveCharacterException e) {
			messenger.convertAndSendToUser(principal.getName(), "/local/error", 
					new ErrorMessage(e.getMessage()));
			return;
		}
		
		// Make sure that the character is living
		try {
			if(characterService.isDeceased(heroDto.id)) {
				return;
			}
		} catch (CharacterNotFoundException e) {
			// TODO: log this error
		}
		
		// Check that the attack cooldown is finished for that user
		long currentTime = System.currentTimeMillis();
		if(currentTime < heroDto.attackReady) {
			messenger.convertAndSendToUser(principal.getName(), "/local/error", 
					new ErrorMessage("Your attack is still on cooldown."));
			return;
		}
		
		// Do boss calcs
		synchronized(bossLock) {			
			// Check boss alive
			if(boss.getCurrentHealth() <= 0) {
				return;
			}
			
			// Check that the boss the user wanted to attack
			// is still the active boss.
			if(boss.getId() != attack.bossId) {
				return;
			}
			
			// Do the attack
			boss.damage(1); //TODO: damage calculation
			if(!contribs.containsKey(heroDto.id)) {
				contribs.put(heroDto.id, new ContributionInfo());
			}
			var info = contribs.get(heroDto.id);
			info.addTotalDamage(1);
			info.incrementAttacks();
			
			// Check for boss kill
			if(boss.getCurrentHealth() <= 0) {
				info.setDealtKillingBlow(true);
				messenger.convertAndSend("/global/boss.died", 
						new BossDiedMessage(boss.getName(), heroDto.firstName + " " + heroDto.lastName));
				boss = new Boss("Boss " + ++bossCounter, 12 + random.nextInt(6));
				
				// Process contributions and update relevant characters
				processContributorAwards();
			}
		}
		
		// Send updated boss info
		messenger.convertAndSend("/global/boss.status", new BossStatusMessage(boss));

		// Do attack post-processing
		try {
			// TODO: implement damage calculation
			JsonObject receivedDamageUpdate = characterService.dealDamage(heroDto.id, 1);
			JsonObject xpUpdate = characterService.grantXp(heroDto.id, 1);
			messenger.convertAndSendToUser(principal.getName(), "/local/character", 
					gson.toJson(new CharacterUpdateMessage(receivedDamageUpdate, xpUpdate)));
			
			// Attack cooldown
			characterService.setAttackCooldown(heroDto.id, currentTime + 2000);
			messenger.convertAndSendToUser(principal.getName(), "/local/cooldown", new CooldownMessage(2.0));
		} catch (CharacterNotFoundException e) {
			// TODO: handle/log
		}
	}
	
	private void processContributorAwards() {
		// Process each contribution
		for(Entry<Integer, ContributionInfo> entry : contribs.entrySet()) {
			int heroId = entry.getKey();
			var contrib = entry.getValue();
			
			// Make sure the contributing hero is not dead
			try {
				if(characterService.isDeceased(heroId)) {
					continue;
				}
			} catch (CharacterNotFoundException e) {
				continue;
			}
			
			// Add their xp
			int xpGained = 5 + (contrib.dealtKillingBlow() ? 5 : 0);
			try {
				characterService.grantXp(heroId, xpGained);
			} catch (CharacterNotFoundException e) {
				// TODO: Log this error or handle it more gracefully (shouldn't happen in production, though)
			}
		}
		
		// Clear the map for the next boss
		contribs.clear();
	}
	
	@GetMapping("/game/bossinfo")
	public @ResponseBody BossStatusMessage getBossInfo() {
		return new BossStatusMessage(boss);
	}
	
}
