package net.calebscode.heroland.response.dto;

import java.util.List;

public class CharacterList {

	public final List<CharacterListEntry> characters;
	public final int activeCharacterId;
	
	public CharacterList(List<CharacterListEntry> characters, int activeCharacterId) {
		this.characters = characters;
		this.activeCharacterId = activeCharacterId;
	}
	
}
