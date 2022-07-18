package net.mythlands.response;

import com.google.gson.Gson;

import net.mythlands.dto.MythlandsCharacterDTO;

public class CharacterStatsRO {

	public final long attackReady;
	public final int skillPoints;
	
	public final int level;
	public final int xp;
	
	public final double maxHealth;
	public final double currentHealth;
	public final double maxMana;
	public final double currentMana;
	
	public final double stamina;
	public final double spirit;
	public final double strength;
	public final double dexterity;
	public final double attunement;
	public final double toughness;
	public final double avoidance;
	public final double resistance;
	
	public final double goldGain;
	public final double xpGain;
	public final double attackCooldown;
	
	public CharacterStatsRO(MythlandsCharacterDTO character) {
		attackReady = character.attackReady;
		skillPoints = character.skillPoints;
		
		level = character.level;
		xp = character.level;
		
		maxHealth = character.maxHealth;
		currentHealth = character.currentHealth;
		maxMana = character.maxMana;
		currentMana = character.currentMana;
		
		stamina = character.stamina;
		spirit = character.spirit;
		strength = character.strength;
		dexterity = character.dexterity;
		attunement = character.attunement;
		toughness = character.toughness;
		avoidance = character.avoidance;
		resistance = character.resistance;
		
		goldGain = character.goldGain;
		xpGain = character.xpGain;
		attackCooldown = character.attackCooldown;
	}
	
}
