package net.calebscode.mythlands.messages.out;

import java.util.List;

import net.calebscode.mythlands.dto.MythlandsCharacterDTO;

public class CharacterListMessage {

	public final List<MythlandsCharacterDTO> characters;
	public final int activeCharacterId;
	
	public CharacterListMessage(List<MythlandsCharacterDTO> characters, int activeCharacterId) {
		this.characters = characters;
		this.activeCharacterId = activeCharacterId;
	}
	
}
