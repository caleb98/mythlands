package net.calebscode.mythlands.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import net.calebscode.mythlands.core.MythlandsUserDetails;
import net.calebscode.mythlands.entity.MythlandsUser;
import net.calebscode.mythlands.repository.MythlandsUserRepository;

@Service
public class MythlandsUserDetailsService implements UserDetailsService {

	@Autowired
	private MythlandsUserRepository userRepository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		MythlandsUser user = userRepository.findByUsername(username);
		if(user == null) {
			throw new UsernameNotFoundException("User not found");
		}
		return new MythlandsUserDetails(user);
	}
	
}
