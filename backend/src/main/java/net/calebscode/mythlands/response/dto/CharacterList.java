package net.calebscode.mythlands.response.dto;

import java.util.List;

public class CharacterList {

	public final List<MythlandsCharacterDTO> characters;
	public final int activeCharacterId;
	
	public CharacterList(List<MythlandsCharacterDTO> characters, int activeCharacterId) {
		this.characters = characters;
		this.activeCharacterId = activeCharacterId;
	}
	
}
