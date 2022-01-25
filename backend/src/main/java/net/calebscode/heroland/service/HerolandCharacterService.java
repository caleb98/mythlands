package net.calebscode.heroland.service;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.JsonObject;

import net.calebscode.heroland.entity.HerolandCharacter;
import net.calebscode.heroland.exception.CharacterNotFoundException;
import net.calebscode.heroland.messages.in.SpendSkillPointMessage;
import net.calebscode.heroland.repository.HerolandCharacterRepository;

@Service
public class HerolandCharacterService {

	@Autowired private HerolandCharacterRepository characterRepository;
	
	@Transactional
	public void setAttackCooldown(int heroId, long ready) throws CharacterNotFoundException {
		HerolandCharacter hero = getCharacter(heroId);
		hero.setAttackReady(ready);
	}
	
	@Transactional
	public JsonObject dealDamage(int heroId, double amount) throws CharacterNotFoundException {
		HerolandCharacter hero = getCharacter(heroId);
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
		HerolandCharacter hero = getCharacter(heroId);
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
		HerolandCharacter hero = getCharacter(heroId);
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
		HerolandCharacter hero = getCharacter(heroId);
		double prevMana = hero.getCurrentMana();
		hero.modifyCurrentMana(amount);
		JsonObject update = new JsonObject();
		if(prevMana != hero.getCurrentMana()) {
			update.addProperty("currentMana", hero.getCurrentMana());
		}
		return update;
	}
	
	@Transactional
	public JsonObject useSkillPoint(int heroId, SpendSkillPointMessage spend) throws CharacterNotFoundException {
		HerolandCharacter hero = getCharacter(heroId);
		if(hero.getSkillPoints() < 1) {
			throw new IllegalArgumentException("No skill points available to spend.");
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
		
		return updates;
	}
	
	@Transactional
	public JsonObject grantXp(int heroId, int xp) 
			throws CharacterNotFoundException {
		
		HerolandCharacter hero = getCharacter(heroId);
		
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
		}
		
		return updates;
	}
	
	public boolean isDeceased(int heroId) throws CharacterNotFoundException {
		return getCharacter(heroId).isDeceased();
	}
	
	private HerolandCharacter getCharacter(int id) throws CharacterNotFoundException {
		HerolandCharacter hero = characterRepository.findById(id);
		if(hero == null) {
			throw new CharacterNotFoundException("No character with id " + id + " found.");
		}
		return hero;
	}
	
}
