package net.calebscode.heroland.core;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class HerolandUserDetails implements UserDetails {

	private static final long serialVersionUID = 6391129448758873453L;
	
	private HerolandUser user;
	
	public HerolandUserDetails(HerolandUser user) {
		this.user = user;
	}
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		ArrayList<GrantedAuthority> authorities = new ArrayList<>();
		for(String authority : user.getAuthorities()) {
			authorities.add(()->{
				return authority;
			});
		}
		return authorities;
	}
	
	@Override
	public String getPassword() {
		return user.getPasswordHash();
	}
	
	@Override
	public String getUsername() {
		return user.getUsername();
	}
	
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}
	
	@Override
	public boolean isAccountNonLocked() {
		return true;
	}
	
	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}
	
	@Override
	public boolean isEnabled() {
		return true;
	}
	
}
