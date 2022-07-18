package net.mythlands.response;

import net.mythlands.dto.MythlandsUserDTO;

public class UserRO {

	public final String username;
	public final String email;
	public final int activeCharacterId;
	
	public UserRO(MythlandsUserDTO user) {
		username = user.username;
		email = user.email;
		activeCharacterId = user.activeCharacter == null ? -1 : user.activeCharacter.id;
	}
	
}
