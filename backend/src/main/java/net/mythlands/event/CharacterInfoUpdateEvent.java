package net.mythlands.event;

import net.mythlands.dto.MythlandsCharacterDTO;

public class CharacterInfoUpdateEvent {

	public final MythlandsCharacterDTO character;
	
	public CharacterInfoUpdateEvent(MythlandsCharacterDTO character) {
		this.character = character;
	}
	
}
