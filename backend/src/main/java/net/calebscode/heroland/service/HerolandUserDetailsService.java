package net.calebscode.heroland.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import net.calebscode.heroland.core.HerolandUserDetails;
import net.calebscode.heroland.entity.HerolandUser;
import net.calebscode.heroland.repository.HerolandUserRepository;

public class HerolandUserDetailsService implements UserDetailsService {

	@Autowired
	private HerolandUserRepository userRepository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		HerolandUser user = userRepository.findByUsername(username);
		if(user == null) {
			throw new UsernameNotFoundException("User not found");
		}
		return new HerolandUserDetails(user);
	}
	
}
