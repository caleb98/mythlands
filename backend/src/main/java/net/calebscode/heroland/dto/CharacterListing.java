package net.calebscode.heroland.dto;

import net.calebscode.heroland.character.HerolandCharacter;

public class CharacterListing {
	
	public final String firstName;
	public final String lastName;
	public final String ownerName;
	public final int level;
	public final int xp;
	public final boolean isDeceased;
	
	public final int maxHealth;
	public final int currentHealth;
	public final int maxMana;
	public final int currentMana;
	
	public final int strength;
	public final int dexterity;
	public final int attunement;
	public final int toughness;
	public final int avoidance;
	public final int resistance;
	
	public CharacterListing(HerolandCharacter hc) {
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
		
		strength = hc.getStrength();
		dexterity = hc.getDexterity();
		attunement = hc.getAttunement();
		toughness = hc.getToughness();
		avoidance = hc.getAvoidance();
		resistance = hc.getResistance();		
	}
	
}
