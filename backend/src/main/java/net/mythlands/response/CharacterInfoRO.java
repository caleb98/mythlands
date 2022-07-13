package net.mythlands.response;

import net.mythlands.dto.MythlandsCharacterDTO;

public class CharacterInfoRO {
	
	public final String firstName;
	public final String lastName;
	public final boolean isDeceased;
	public final String ownerName;
	
	public CharacterInfoRO(MythlandsCharacterDTO character) {
		firstName = character.firstName;
		lastName = character.lastName;
		isDeceased = character.isDeceased;
		ownerName = character.owner.username;
	}
	
}
