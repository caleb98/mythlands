package net.mythlands.messages.out;

import com.google.gson.JsonObject;

import net.mythlands.dto.MythlandsCharacterDTO;

/**
 * A message used by the frontend to update a character's stat values. *
 */
public class CharacterStatsMessage {

	public final long timestamp;
	public final JsonObject stats;
	
	public CharacterStatsMessage(MythlandsCharacterDTO character) {
		timestamp = System.currentTimeMillis();
		stats = new JsonObject();
		
		stats.addProperty("level", character.level);
		stats.addProperty("xp", character.xp);
		stats.addProperty("skillpoints", character.skillPoints);
		
		stats.addProperty("maxHealth", character.maxHealth);
		stats.addProperty("currentHealth", character.currentHealth);
		stats.addProperty("maxMana", character.maxMana);
		stats.addProperty("currentMana", character.currentMana);
		
		stats.addProperty("stamina", character.stamina);
		stats.addProperty("spirit", character.spirit);
		stats.addProperty("strength", character.strength);
		stats.addProperty("dexterity", character.dexterity);
		stats.addProperty("attunement", character.attunement);
		stats.addProperty("toughness", character.toughness);
		stats.addProperty("avoidance", character.avoidance);
		stats.addProperty("resistance", character.resistance);

		stats.addProperty("goldGain", character.goldGain);
		stats.addProperty("xpGain", character.xpGain);
		stats.addProperty("attackCooldown", character.attackCooldown);
	}
	
}
