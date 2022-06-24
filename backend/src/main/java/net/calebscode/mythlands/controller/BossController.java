package net.calebscode.mythlands.controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Random;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import net.calebscode.mythlands.core.Boss;
import net.calebscode.mythlands.core.ContributionInfo;
import net.calebscode.mythlands.core.NameGenerator;
import net.calebscode.mythlands.dto.MythlandsCharacterDTO;
import net.calebscode.mythlands.exception.MythlandsServiceException;
import net.calebscode.mythlands.messages.in.AttackMessage;
import net.calebscode.mythlands.messages.out.BossDiedMessage;
import net.calebscode.mythlands.messages.out.BossStatusMessage;
import net.calebscode.mythlands.messages.out.CharacterUpdateMessage;
import net.calebscode.mythlands.messages.out.CooldownMessage;
import net.calebscode.mythlands.messages.out.ErrorMessage;
import net.calebscode.mythlands.service.MythlandsGameService;
import net.calebscode.mythlands.service.MythlandsUserService;

@Controller
public class BossController {
	
	@Autowired private MythlandsGameService gameService;
	@Autowired private MythlandsUserService userService;
	@Autowired private SimpMessagingTemplate messenger;
	@Autowired private Gson gson;
	@Autowired private NameGenerator bossNameGenerator;
	
	private Random random = new Random();
	private Boss boss;
	private int bossCount = 1;
	
	private HashMap<Integer, ContributionInfo> contribs = new HashMap<>(); 
	
	private static Object bossLock = new Object();

	@PostConstruct
	public void init() {
		boss = new Boss("Boss " + bossCount++ + " - " + bossNameGenerator.generateName(), 15); 
	}
	
	@MessageMapping("/attack")
	public void attack(AttackMessage attack, Principal principal) {		
		// Get the attacking user's hero
		MythlandsCharacterDTO heroDto;
		try {
			heroDto = userService.getActiveCharacter(principal.getName());
		} catch (MythlandsServiceException e) {
			messenger.convertAndSendToUser(principal.getName(), "/local/error", 
					new ErrorMessage(e.getMessage()));
			return;
		}
		
		// Make sure that the character is living
		try {
			if(gameService.isDeceased(heroDto.id)) {
				return;
			}
		} catch (MythlandsServiceException e) {
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
				contribs.put(heroDto.id, new ContributionInfo(principal.getName()));
			}
			var info = contribs.get(heroDto.id);
			info.addTotalDamage(1);
			info.incrementAttacks();
			
			// Check for boss kill
			if(boss.getCurrentHealth() <= 0) {
				info.setDealtKillingBlow(true);
				messenger.convertAndSend("/global/boss.died", 
						new BossDiedMessage(boss.getName(), heroDto.firstName + " " + heroDto.lastName));
				boss = new Boss("Boss " + bossCount++ + " - " + bossNameGenerator.generateName(), 12 + random.nextInt(6));
				
				// Process contributions and update relevant characters
				processContributorAwards();
			}
		}
		
		// Send updated boss info
		messenger.convertAndSend("/global/boss.status", new BossStatusMessage(boss));

		// Do attack post-processing
		try {			
			// TODO: implement damage calculation
			JsonObject receivedDamageUpdate = gameService.dealDamage(heroDto.id, 1);
			JsonObject xpUpdate = gameService.grantXp(heroDto.id, 1);
			// TODO: xp gain modifier
			// TODO: gold?
			messenger.convertAndSendToUser(principal.getName(), "/local/character", 
					gson.toJson(new CharacterUpdateMessage(receivedDamageUpdate, xpUpdate)));
			
			// Attack cooldown
			gameService.setAttackCooldown(heroDto.id, currentTime + Math.round(heroDto.attackCooldown));
			messenger.convertAndSendToUser(principal.getName(), "/local/cooldown", new CooldownMessage(heroDto.attackCooldown / 1000));
		} catch (MythlandsServiceException e) {
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
				if(gameService.isDeceased(heroId)) {
					continue;
				}
			} catch (MythlandsServiceException e) {
				continue;
			}
			
			// Add their xp
			int xpGained = 5 + (contrib.dealtKillingBlow() ? 5 : 0);
			try {
				JsonObject updates = gameService.grantXp(heroId, xpGained);
				messenger.convertAndSendToUser(
						contrib.getUsername(),
						"/local/character", 
						gson.toJson(new CharacterUpdateMessage(updates))
				);
			} catch (MythlandsServiceException e) {
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
