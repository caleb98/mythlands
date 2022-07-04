package net.mythlands.dto;

import net.mythlands.core.MythlandsUser;

public class MythlandsUserDTO {

	public final int id;
	public final String username;
	public final String email;
	
	public MythlandsUserDTO(MythlandsUser user) {
		id = user.getId();
		username = user.getUsername();
		email = user.getEmail();
	}
	
}
