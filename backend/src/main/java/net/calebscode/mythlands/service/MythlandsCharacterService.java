package net.calebscode.mythlands.service;

import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.google.gson.JsonObject;

import net.calebscode.mythlands.dto.MythlandsCharacterDTO;
import net.calebscode.mythlands.entity.MythlandsCharacter;
import net.calebscode.mythlands.exception.CharacterNotFoundException;
import net.calebscode.mythlands.exception.InvalidCharacterException;
import net.calebscode.mythlands.messages.in.SpendSkillPointMessage;
import net.calebscode.mythlands.repository.MythlandsCharacterRepository;

@Service
public class MythlandsCharacterService {

	@Autowired private MythlandsCharacterRepository characterRepository;
	
	@Transactional
	public void setAttackCooldown(int heroId, long ready) throws CharacterNotFoundException {
		MythlandsCharacter hero = getCharacter(heroId);
		hero.setAttackReady(ready);
	}
	
	@Transactional
	public JsonObject dealDamage(int heroId, double amount) throws CharacterNotFoundException {
		MythlandsCharacter hero = getCharacter(heroId);
		hero.modifyCurrentHealth(-amount);
		double newHealth = hero.getCurrentHealth();
		
		JsonObject updates = new JsonObject();
		updates.addProperty("currentHealth", newHealth);
		if(newHealth <= 0) {
			updates.addProperty("isDeceased", true);
			hero.setDeceased(true);
		}
		
		return updates;
	}
	
	@Transactional
	public JsonObject gainHealth(int heroId, double amount) throws CharacterNotFoundException {
		MythlandsCharacter hero = getCharacter(heroId);
		double prevHealth = hero.getCurrentHealth();
		hero.modifyCurrentHealth(amount);
		JsonObject update = new JsonObject();
		if(prevHealth != hero.getCurrentHealth()) {
			update.addProperty("currentHealth", hero.getCurrentHealth());
		}
		return update;
	}
	
	@Transactional
	public JsonObject loseMana(int heroId, double amount) throws CharacterNotFoundException {
		MythlandsCharacter hero = getCharacter(heroId);
		double prevMana = hero.getCurrentMana();
		hero.modifyCurrentMana(-amount);
		JsonObject update = new JsonObject();
		if(prevMana != hero.getCurrentMana()) {
			update.addProperty("currentMana", hero.getCurrentMana());
		}
		return update;
	}
	
	@Transactional
	public JsonObject gainMana(int heroId, double amount) throws CharacterNotFoundException {
		MythlandsCharacter hero = getCharacter(heroId);
		double prevMana = hero.getCurrentMana();
		hero.modifyCurrentMana(amount);
		JsonObject update = new JsonObject();
		if(prevMana != hero.getCurrentMana()) {
			update.addProperty("currentMana", hero.getCurrentMana());
		}
		return update;
	}
	
	@Transactional
	public JsonObject useSkillPoint(int heroId, SpendSkillPointMessage spend) throws CharacterNotFoundException, InvalidCharacterException {
		MythlandsCharacter hero = characterRepository.findById(heroId);
		if(hero == null) {
			throw new CharacterNotFoundException("No character with id " + heroId + " found.");
		}
		else if(hero.isDeceased()) {
			throw new InvalidCharacterException("Hero is dead.");
		}
		else if(hero.getSkillPoints() < 1) {
			throw new IllegalArgumentException("No skill points available to spend.");
		}
		
		JsonObject updates = new JsonObject();
		hero.setSkillPoints(hero.getSkillPoints() - 1);
		updates.addProperty("skillPoints", hero.getSkillPoints());
		
		switch(spend.skill) {
		
		case ATTUNEMENT:
			hero.getAttunement().modifyBase(1);
			updates.addProperty("attunement", hero.getAttunement().getValue());
			break;
			
		case AVOIDANCE:
			hero.getAvoidance().modifyBase(1);
			updates.addProperty("avoidance", hero.getAvoidance().getValue());
			break;
			
		case DEXTERITY:
			hero.getDexterity().modifyBase(1);
			updates.addProperty("dexterity", hero.getDexterity().getValue());
			break;
			
		case RESISTANCE:
			hero.getResistance().modifyBase(1);
			updates.addProperty("resistance", hero.getResistance().getValue());
			break;
			
		case STRENGTH:
			hero.getStrength().modifyBase(1);
			updates.addProperty("strength", hero.getStrength().getValue());
			break;
			
		case TOUGHNESS:
			hero.getToughness().modifyBase(1);
			updates.addProperty("toughness", hero.getToughness().getValue());
			break;
			
		case SPIRIT:
			hero.getSpirit().modifyBase(1);
			hero.recalculateMaxMana();
			updates.addProperty("spirit", hero.getSpirit().getValue());
			updates.addProperty("currentMana", hero.getCurrentMana());
			updates.addProperty("maxMana", hero.getMaxMana());
			break;
			
		case STAMINA:
			hero.getStamina().modifyBase(1);
			hero.recalculateMaxHealth();
			updates.addProperty("stamina", hero.getStamina().getValue());
			updates.addProperty("currentHealth", hero.getCurrentHealth());
			updates.addProperty("maxHealth", hero.getMaxHealth());
			break;
			
		default:
			// TODO: error
			break;
		
		}		
		
		return updates;
	}
	
	@Transactional
	public JsonObject grantXp(int heroId, int xp) 
			throws CharacterNotFoundException {
		
		MythlandsCharacter hero = getCharacter(heroId);
		
		int oldLevel = hero.getLevel();
		
		int newXp = hero.getXp() + xp;
		int newLevel = hero.getLevel();
		int newSkillPoints = hero.getSkillPoints();
		int xpRequired = 10 + (10 * (newLevel - 1));
		while(newXp >= xpRequired) {
			newXp -= xpRequired;
			newLevel++;
			newSkillPoints++;
			xpRequired = 10 + (10 * (newLevel - 1));
		}
		hero.setXp(newXp);
		hero.setLevel(newLevel);
		hero.setSkillPoints(newSkillPoints);
		
		JsonObject updates = new JsonObject();
		updates.addProperty("xp", newXp);
		if(oldLevel != newLevel) {
			updates.addProperty("level", newLevel);
			updates.addProperty("skillPoints", newSkillPoints);
			
			// If they leveled up, we also need to recalculate health and mana.
			hero.recalculateMaxHealth();
			hero.recalculateMaxMana();
			
			updates.addProperty("currentHealth", hero.getCurrentHealth());
			updates.addProperty("maxHealth", hero.getMaxHealth());
			updates.addProperty("currentMana", hero.getCurrentMana());
			updates.addProperty("maxMana", hero.getMaxMana());
		}
		
		return updates;
	}
	
	public boolean isDeceased(int heroId) throws CharacterNotFoundException {
		return getCharacter(heroId).isDeceased();
	}
	
	public List<MythlandsCharacterDTO> getHallOfFameCharacters(int pageSize, int pageNum) {
		List<MythlandsCharacter> characters = characterRepository.findByOrderByLevelDescXpDesc(
			Pageable.ofSize(pageSize).withPage(pageNum)
		);
		return characters.stream()
			.map((character) -> { return new MythlandsCharacterDTO(character); })
			.collect(Collectors.toList());
		
	}
	
	private MythlandsCharacter getCharacter(int id) throws CharacterNotFoundException {
		MythlandsCharacter hero = characterRepository.findById(id);
		if(hero == null) {
			throw new CharacterNotFoundException("No character with id " + id + " found.");
		}
		return hero;
	}
	
}
