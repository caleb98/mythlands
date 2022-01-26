package net.calebscode.mythlands.dto;

import net.calebscode.mythlands.entity.MythlandsUser;

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
