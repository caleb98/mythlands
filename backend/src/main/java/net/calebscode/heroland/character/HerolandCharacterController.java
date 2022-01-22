package net.calebscode.heroland.character;

import java.security.Principal;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.calebscode.heroland.dto.CharacterListing;
import net.calebscode.heroland.messages.in.SpendSkillPointMessage;
import net.calebscode.heroland.messages.out.CharacterUpdateMessage;
import net.calebscode.heroland.response.ServerResponse;
import net.calebscode.heroland.user.HerolandUser;
import net.calebscode.heroland.user.UserRepository;

@Controller
public class HerolandCharacterController {

	@Autowired private UserRepository userRepository;
	@Autowired private HerolandCharacterRepository characterRepository;
	@Autowired private SimpMessagingTemplate messenger;
	@Autowired private Gson gson;
	
	@PostMapping("/character/create")
	public @ResponseBody ServerResponse createCharacter(
			@RequestParam String firstName,
			@RequestParam String lastName,
			Principal principal)  
	{
		HerolandUser user = userRepository.findByUsername(principal.getName());
		boolean hasAlive = user.getCharacters().stream().anyMatch(c -> !c.isDeceased());
		
		if(hasAlive) {
			return new ServerResponse("You already have a living character. You may only have one character at a time.", true);
		}
		
		HerolandCharacter newChar = new HerolandCharacter();
		newChar.setFirstName(firstName);
		newChar.setLastName(lastName);
		newChar.setOwner(user);
		newChar = characterRepository.save(newChar);
		user.getCharacters().add(newChar);
		//userRepository.save(user);
		
		return new ServerResponse("Character created!");
	}
	
	@GetMapping("/character/list")
	public @ResponseBody ServerResponse listCharacters(Principal principal) {
		String username = principal.getName();
		HerolandUser user = userRepository.findByUsername(username);
		return new ServerResponse(
				"Success!", 
				user.getCharacters().stream().map((c) -> new CharacterListing(c)).collect(Collectors.toList())
		);
	}
	
	@MessageMapping("/character.skillup")
	@Transactional
	public void spendSkillPoint(SpendSkillPointMessage spend, Principal principal) {
		HerolandUser user = userRepository.findByUsername(principal.getName());
		HerolandCharacter hero = user.getActiveCharacter();
		
		if(hero == null) {
			//TODO: handle
			return;
		}
		
		if(hero.getSkillPoints() == 0) {
			return;
		}
		
		JsonObject updates = new JsonObject();
		hero.setSkillPoints(hero.getSkillPoints() - 1);
		updates.addProperty("skillPoints", hero.getSkillPoints());
		
		switch(spend.skill) {
		
		case ATTUNEMENT:
			hero.modifyAttunement(1);
			updates.addProperty("attunement", hero.getAttunement());
			break;
			
		case AVOIDANCE:
			hero.modifyAvoidance(1);
			updates.addProperty("avoidance", hero.getAvoidance());
			break;
			
		case DEXTERITY:
			hero.modifyDexterity(1);
			updates.addProperty("dexterity", hero.getDexterity());
			break;
			
		case RESISTANCE:
			hero.modifyResistance(1);
			updates.addProperty("resistance", hero.getResistance());
			break;
			
		case STRENGTH:
			hero.modifyStrength(1);
			updates.addProperty("strength", hero.getStrength());
			break;
			
		case TOUGHNESS:
			hero.modifyToughness(1);
			updates.addProperty("toughness", hero.getToughness());
			break;
			
		default:
			//TODO: handle
			break;
		
		}
		
		// Send updates back to user
		JsonArray updatesArr = new JsonArray();
		updatesArr.add(updates);
		messenger.convertAndSendToUser(principal.getName(), "/local/character", gson.toJson(new CharacterUpdateMessage(updatesArr)));	
	}
	
}
