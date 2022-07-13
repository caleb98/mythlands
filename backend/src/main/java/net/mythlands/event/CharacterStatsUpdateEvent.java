package net.mythlands.event;

import net.mythlands.dto.MythlandsCharacterDTO;

public class CharacterStatsUpdateEvent {

	public final MythlandsCharacterDTO character;
	
	public CharacterStatsUpdateEvent(MythlandsCharacterDTO character) {
		this.character = character;
	}
	
}
