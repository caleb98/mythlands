package net.mythlands.response;

import net.mythlands.dto.MythlandsCharacterDTO;

public class CharacterRO {

	public final CharacterInfoRO info;
	public final CharacterEquipmentRO equipment;
	public final CharacterStatsRO stats;
	public final CharacterEffectsRO effects;
	public final CharacterInventoryRO inventory;
	
	public CharacterRO(MythlandsCharacterDTO character) {
		info = new CharacterInfoRO(character);
		equipment = new CharacterEquipmentRO(character);
		stats = new CharacterStatsRO(character);
		effects = new CharacterEffectsRO(character);
		inventory = new CharacterInventoryRO(character);
	}
	
}
