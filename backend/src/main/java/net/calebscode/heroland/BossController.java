package net.calebscode.heroland;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Random;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.calebscode.heroland.character.HerolandCharacter;
import net.calebscode.heroland.character.HerolandCharacterRepository;
import net.calebscode.heroland.messages.in.AttackMessage;
import net.calebscode.heroland.messages.out.BossDiedMessage;
import net.calebscode.heroland.messages.out.BossStatusMessage;
import net.calebscode.heroland.messages.out.CharacterUpdateMessage;
import net.calebscode.heroland.messages.out.CooldownMessage;
import net.calebscode.heroland.user.HerolandUser;
import net.calebscode.heroland.user.UserRepository;

@Controller
public class BossController {
	
	@Autowired private UserRepository userRepository;
	@Autowired private HerolandCharacterRepository characterRepository;
	@Autowired private SimpMessagingTemplate messenger;
	@Autowired private Gson gson;
	
	private Random random = new Random();
	private int bossCounter = 1;
	private Boss boss = new Boss("Boss 1", 15);
	
	private HashMap<Integer, ContributionInfo> contribs = new HashMap<>(); 
	
	private static Object bossLock = new Object();

	@MessageMapping("/attack")
	@Transactional
	@SendToUser("/local/character")
	public void attack(AttackMessage attack, Principal principal) {
		// Get the attacking user's hero
		HerolandUser user = userRepository.findByUsername(principal.getName());
		HerolandCharacter hero = user.getActiveCharacter();
		if(hero == null) {
			//TODO: error handle
			return;
		}
		
		// Make sure that the character is living
		if(hero.isDeceased()) {
			return;
		}
		
		// Check that the attack cooldown is finished for that user
		long currentTime = System.currentTimeMillis();
		if(currentTime < hero.getAttackReady()) {
			//TODO: error handle
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
			if(!contribs.containsKey(hero.getId())) {
				contribs.put(hero.getId(), new ContributionInfo());
			}
			var info = contribs.get(hero.getId());
			info.addTotalDamage(1);
			info.incrementAttacks();
			
			// Check for boss kill
			if(boss.getCurrentHealth() <= 0) {
				info.setDealtKillingBlow(true);
				messenger.convertAndSend("/global/boss.died", new BossDiedMessage(boss.getName(), hero.getFullName()));
				boss = new Boss("Boss " + ++bossCounter, 12 + random.nextInt(6));
				
				// Process contributions and update relevant characters
				processContributorAwards();
			}
		}
		
		
		// Deal damage to the player
		// TODO: change how this works
		JsonObject receivedDamageUpdate = new JsonObject();
		hero.setCurrentHealth(hero.getCurrentHealth() - 1);
		if(hero.getCurrentHealth() <= 0) {
			hero.setDeceased(true);
			receivedDamageUpdate.addProperty("isDeceased", true);
		}
		receivedDamageUpdate.addProperty("currentHealth", hero.getCurrentHealth());
		
		// Send updated boss info
		messenger.convertAndSend("/global/boss.status", new BossStatusMessage(boss));
		
		// Send attack result to user
		long attackCooldown = currentTime + 2000;
		hero.setAttackReady(attackCooldown);
		
		// Process xp gain
		JsonObject updates = addCharacterXp(hero, 1);
		JsonArray updatesArr = new JsonArray();
		updatesArr.add(updates);
		updatesArr.add(receivedDamageUpdate);
		messenger.convertAndSendToUser(principal.getName(), "/local/character", gson.toJson(new CharacterUpdateMessage(updatesArr)));
		messenger.convertAndSendToUser(principal.getName(), "/local/cooldown", new CooldownMessage(2.0));
	}
	
	private void processContributorAwards() {
		// Process each contribution
		for(Entry<Integer, ContributionInfo> entry : contribs.entrySet()) {
			var contributor = characterRepository.getById(entry.getKey());
			var contrib = entry.getValue();
			
			// Make sure the contrubting hero is not dead
			if(contributor.isDeceased()) {
				continue;
			}
			
			int xpGained = 5 + (contrib.dealtKillingBlow() ? 5 : 0);
			
			// Add their xp and push the updates
			JsonObject updates = addCharacterXp(contributor, xpGained);
			JsonArray updatesArr = new JsonArray();
			updatesArr.add(updates);
		
			messenger.convertAndSendToUser(
				contributor.getOwner().getUsername(), 
				"/local/character", 
				gson.toJson(new CharacterUpdateMessage(updatesArr))
			);
		}
		
		// Clear the map for the next boss
		contribs.clear();
	}
	
	/**
	 * Adds xp to a character
	 * @param character
	 * @param xp
	 * @return a {@link JsonObject} representing the changed propertied of the character
	 */
	private JsonObject addCharacterXp(HerolandCharacter character, int xp) {
		int oldLevel = character.getLevel();
		
		int newXp = character.getXp() + xp;
		int newLevel = character.getLevel();
		int newSkillPoints = character.getSkillPoints();
		int xpRequired = 10 + (10 * (newLevel - 1));
		while(newXp >= xpRequired) {
			newXp -= xpRequired;
			newLevel++;
			newSkillPoints++;
			xpRequired = 10 + (10 * (newLevel - 1));
		}
		character.setXp(newXp);
		character.setLevel(newLevel);
		character.setSkillPoints(newSkillPoints);
		
		JsonObject updates = new JsonObject();
		updates.addProperty("xp", newXp);
		if(oldLevel != newLevel) {
			updates.addProperty("level", newLevel);
			updates.addProperty("skillPoints", newSkillPoints);
		}
		return updates;
	}
	
	@GetMapping("/game/bossinfo")
	public @ResponseBody BossStatusMessage getBossInfo() {
		return new BossStatusMessage(boss);
	}
	
}
