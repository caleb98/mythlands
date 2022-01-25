package net.calebscode.mythlands.response.dto;

import net.calebscode.mythlands.entity.MythlandsUser;

public class UserInfo {

	public final String username;
	public final String email;
	
	public UserInfo(MythlandsUser user) {
		username = user.getUsername();
		email = user.getEmail();
	}
	
}
