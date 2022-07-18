package net.mythlands.event;

import net.mythlands.dto.MythlandsCharacterDTO;

public class CharacterEquipmentUpdateEvent {

	public MythlandsCharacterDTO character;
	
	public CharacterEquipmentUpdateEvent(MythlandsCharacterDTO character) {
		this.character = character;
	}
	
}
