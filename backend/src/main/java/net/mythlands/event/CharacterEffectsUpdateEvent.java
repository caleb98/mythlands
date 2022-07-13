package net.mythlands.event;

import net.mythlands.dto.MythlandsCharacterDTO;

public class CharacterEffectsUpdateEvent {

	public final MythlandsCharacterDTO character;
	
	public CharacterEffectsUpdateEvent(MythlandsCharacterDTO character) {
		this.character = character;
	}
	
}
