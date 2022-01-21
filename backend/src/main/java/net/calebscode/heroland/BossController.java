package net.calebscode.heroland;

import java.security.Principal;
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
import net.calebscode.heroland.messages.BossDiedMessage;
import net.calebscode.heroland.messages.BossStatusMessage;
import net.calebscode.heroland.messages.CharacterUpdateMessage;
import net.calebscode.heroland.messages.CooldownMessage;
import net.calebscode.heroland.user.HerolandUser;
import net.calebscode.heroland.user.UserRepository;

@Controller
public class BossController {
	
	@Autowired private UserRepository userRepository;
	@Autowired private SimpMessagingTemplate messenger;
	@Autowired private Gson gson;
	
	private Random random = new Random();
	private int bossNum = 1;
	private String bossName = "Boss 1";
	private int maxHealth = 15;
	private int currentHealth = 15;
	
	private static Object bossLock = new Object();

	@MessageMapping("/attack")
	@Transactional
	@SendToUser("/local/character")
	public void attack(Principal principal) {
		//TODO: check that they are attacking the same boss that they meant to
		
		// Get the attacking user's hero
		HerolandUser user = userRepository.findByUsername(principal.getName());
		HerolandCharacter hero = user.getCharacters().stream().filter(c -> !c.isDeceased()).findFirst().orElseGet(() -> null);
		if(hero == null) {
			//TODO: error handle
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
			// Do the attack
			currentHealth -= 1;
			
			// Check for boss kill
			if(currentHealth <= 0) {
				messenger.convertAndSend("/global/boss-died", new BossDiedMessage(bossName, hero.getFullName()));
				bossName = "Boss " + ++bossNum;
				currentHealth = maxHealth = 12 + random.nextInt(6);
			}
		}
		
		// Send updated boss info
		messenger.convertAndSend("/global/boss-status", new BossStatusMessage(bossName, currentHealth, maxHealth));
		
		// Send attack result to user
		long attackCooldown = currentTime + 2000;
		hero.setAttackReady(attackCooldown);
		
		JsonObject updates = new JsonObject();
		
		//TODO: add skill point
		int newXp = hero.getXp() + 1;
		if(newXp >= 9 + hero.getLevel()) {
			newXp = 0;
			int newLevel = hero.getLevel() + 1;
			updates.addProperty("level", newLevel);
			hero.setLevel(newLevel);
		}
		updates.addProperty("xp", newXp);
		hero.setXp(newXp);
		
		JsonArray updatesArr = new JsonArray();
		updatesArr.add(updates);
		messenger.convertAndSendToUser(principal.getName(), "/local/character", gson.toJson(new CharacterUpdateMessage(updatesArr)));
		messenger.convertAndSendToUser(principal.getName(), "/local/cooldown", new CooldownMessage(2.0));
		return;
	}
	
	@GetMapping("/game/bossinfo")
	public @ResponseBody BossStatusMessage getBossInfo() {
		return new BossStatusMessage(bossName, currentHealth, maxHealth);
	}
	
}
