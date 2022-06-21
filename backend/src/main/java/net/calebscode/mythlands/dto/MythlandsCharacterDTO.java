package net.calebscode.mythlands.dto;

import net.calebscode.mythlands.entity.MythlandsCharacter;

public class MythlandsCharacterDTO {
	
	public final int id;
	
	public final String firstName;
	public final String lastName;
	public final String ownerName;
	public final int level;
	public final int xp;
	public final boolean isDeceased;
	
	public final int maxHealth;
	public final double currentHealth;
	public final int maxMana;
	public final double currentMana;
	
	public final int stamina;
	public final int spirit;
	public final int strength;
	public final int dexterity;
	public final int attunement;
	public final int toughness;
	public final int avoidance;
	public final int resistance;
	
	public final int skillPoints;
	public final long attackReady;
	
	public MythlandsCharacterDTO(MythlandsCharacter hc) {
		id = hc.getId();
		
		firstName = hc.getFirstName();
		lastName = hc.getLastName();
		ownerName = hc.getOwner().getUsername();
		level = hc.getLevel();
		xp = hc.getXp();
		isDeceased = hc.isDeceased();
		
		maxHealth = hc.getMaxHealth();
		currentHealth = hc.getCurrentHealth();
		maxMana = hc.getMaxMana();
		currentMana = hc.getCurrentMana();
		
		stamina = hc.getStamina();
		spirit = hc.getSpirit();
		strength = hc.getStrength();
		dexterity = hc.getDexterity();
		attunement = hc.getAttunement();
		toughness = hc.getToughness();
		avoidance = hc.getAvoidance();
		resistance = hc.getResistance();
		
		skillPoints = hc.getSkillPoints();
		attackReady = hc.getAttackReady();
	}
	
}
