package net.calebscode.heroland.response.dto;

import net.calebscode.heroland.user.HerolandUser;

public class UserInfo {

	public final String username;
	public final String email;
	
	public UserInfo(HerolandUser user) {
		username = user.getUsername();
		email = user.getEmail();
	}
	
}
