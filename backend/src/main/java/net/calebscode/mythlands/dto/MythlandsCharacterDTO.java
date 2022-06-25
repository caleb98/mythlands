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
	public final int inventoryCapacity;
	
	public final int skillPoints;
	public final long attackReady;
	
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
	
	public MythlandsCharacterDTO(MythlandsCharacter hc) {
		id = hc.getId();
		
		firstName = hc.getFirstName();
		lastName = hc.getLastName();
		ownerName = hc.getOwner().getUsername();
		level = hc.getLevel();
		xp = hc.getXp();
		isDeceased = hc.isDeceased();
		inventoryCapacity = hc.getInventoryCapacity();
		
		skillPoints = hc.getSkillPoints();
		attackReady = hc.getAttackReady();
		
		maxHealth = hc.getMaxHealth();
		currentHealth = hc.getCurrentHealth();
		maxMana = hc.getMaxMana();
		currentMana = hc.getCurrentMana();
		
		stamina = hc.getStamina().getValue();
		spirit = hc.getSpirit().getValue();
		strength = hc.getStrength().getValue();
		dexterity = hc.getDexterity().getValue();
		attunement = hc.getAttunement().getValue();
		toughness = hc.getToughness().getValue();
		avoidance = hc.getAvoidance().getValue();
		resistance = hc.getResistance().getValue();
		
		goldGain = hc.getGoldGain().getValue();
		xpGain = hc.getXpGain().getValue();
		attackCooldown = hc.getAttackCooldown().getValue();
	}
	
}
