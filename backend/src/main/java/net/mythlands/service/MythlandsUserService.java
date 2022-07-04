package net.mythlands.service;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import net.mythlands.core.MythlandsCharacter;
import net.mythlands.core.MythlandsUser;
import net.mythlands.core.MythlandsUserDetails;
import net.mythlands.dto.MythlandsCharacterDTO;
import net.mythlands.dto.MythlandsUserDTO;
import net.mythlands.exception.MythlandsServiceException;
import net.mythlands.messages.out.CharacterListMessage;
import net.mythlands.repository.MythlandsCharacterRepository;
import net.mythlands.repository.MythlandsUserRepository;

@Service
public class MythlandsUserService implements UserDetailsService {

	public static final Pattern VALID_EMAIL_ADDRESS_REGEX = 
		    Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
	public static final Pattern VALID_USERNAME_REGEX = 
			Pattern.compile("^[A-Za-z0-9]*$");
	
	@Autowired private MythlandsChatService chatService;
	@Autowired private MythlandsUserRepository userRepository;
	@Autowired private MythlandsCharacterRepository characterRepository;
	@Autowired private BCryptPasswordEncoder passwordEncoder;
	
	@Transactional
	public void createNewUser(String username, String email, String password, String passwordConfirm) 
			throws MythlandsServiceException {
		
		// Check email/username availability
		if(email != null && userRepository.findByEmail(email) != null) {
			throw new MythlandsServiceException("A user with that email already exists.");
		}
		else if(userRepository.findByUsername(username) != null) {
			throw new MythlandsServiceException("A user with that username already exists.");
		}
		
		// Check username length
		if(username.length() < 8 || username.length() > 25) {
			throw new MythlandsServiceException("Username must be 8 to 25 characters long.");
		}
		
		// Check username characters
		Matcher usernameMatcher = VALID_USERNAME_REGEX.matcher(username);
		if(!usernameMatcher.matches()) {
			throw new MythlandsServiceException("Username contains an invalid character. Only letters and numbers may be used.");
		}
		
		// Validate email
		if(email != null) {
			Matcher emailMatcher = VALID_EMAIL_ADDRESS_REGEX.matcher(email);
			if(!emailMatcher.matches()) {
				throw new MythlandsServiceException("Invalid email.");
			}
		}
		
		// Validate password
		if(!password.equals(passwordConfirm)) {
			throw new MythlandsServiceException("Your passwords do not match.");
		}
		
		if(password.length() < 10 || password.length() > 75) {
			throw new MythlandsServiceException("Password must be 10 to 75 characters in length.");
		}
		
		// Hash the password
		String encodedPassword = passwordEncoder.encode(password);
		
		// Create the user
		MythlandsUser user = new MythlandsUser();
		user.setUsername(username);
		user.setEmail(email);
		user.setPasswordHash(encodedPassword);
		user.getAuthorities().add("ROLE_PLAYER");
		user = userRepository.save(user);
		
		// Register the user in the global chat channel
		try {
			chatService.addUserToGroup(user.getId(), 1);
		} catch (MythlandsServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Transactional
	public void createNewCharacter(String username, String firstName, String lastName) 
			throws MythlandsServiceException {
		MythlandsUser user = getUser(username);
		
		boolean hasAlive = user.getCharacters().stream().anyMatch(c -> !c.isDeceased());
		if(hasAlive) {
			throw new MythlandsServiceException("You already have a living character. You may only have one character at a time.");
		}
		
		MythlandsCharacter newChar = new MythlandsCharacter();
		newChar.setFirstName(firstName);
		newChar.setLastName(lastName);
		newChar.setOwner(user);
		newChar = characterRepository.save(newChar);
		user.getCharacters().add(newChar);
		user.setActiveCharacter(newChar);
		userRepository.save(user);
	}
	
	@Transactional
	public MythlandsUserDTO getUserInfo(String username) throws MythlandsServiceException {
		return new MythlandsUserDTO(getUser(username));
	}
	
	@Transactional
	public MythlandsCharacterDTO getActiveCharacter(String username) throws MythlandsServiceException {
		MythlandsUser user = getUser(username);
		MythlandsCharacter hero = user.getActiveCharacter();
		if(hero == null) {
			throw new MythlandsServiceException("You have no active character.");
		}
		return new MythlandsCharacterDTO(hero);
	}
	
	@Transactional
	public CharacterListMessage getCharacterList(String username) throws MythlandsServiceException {
		MythlandsUser user = getUser(username);
		
		List<MythlandsCharacterDTO> characters = user.getCharacters().stream()
				.map((c) -> new MythlandsCharacterDTO(c)).collect(Collectors.toList());
		MythlandsCharacter active = user.getActiveCharacter();
		int activeCharacterId = active == null ? -1 : active.getId();
		
		return new CharacterListMessage(characters, activeCharacterId);
	}
	
	private MythlandsUser getUser(String username) throws MythlandsServiceException {
		MythlandsUser user = userRepository.findByUsername(username);
		if(user == null) {
			throw new MythlandsServiceException("No user with username " + username + " found.");
		}
		return user;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		MythlandsUser user = userRepository.findByUsername(username);
		if(user == null) {
			throw new UsernameNotFoundException("User not found");
		}
		return new MythlandsUserDetails(user);
	}
	
}
