package net.calebscode.mythlands.service;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.google.gson.JsonObject;

import net.calebscode.mythlands.entity.MythlandsCharacter;
import net.calebscode.mythlands.entity.MythlandsUser;
import net.calebscode.mythlands.exception.CharacterCreationException;
import net.calebscode.mythlands.exception.NoActiveCharacterException;
import net.calebscode.mythlands.exception.UserNotFoundException;
import net.calebscode.mythlands.exception.UserRegistrationException;
import net.calebscode.mythlands.messages.in.SpendSkillPointMessage;
import net.calebscode.mythlands.repository.MythlandsCharacterRepository;
import net.calebscode.mythlands.repository.MythlandsUserRepository;
import net.calebscode.mythlands.response.dto.CharacterList;
import net.calebscode.mythlands.response.dto.MythlandsCharacterDTO;
import net.calebscode.mythlands.response.dto.UserInfo;

@Service
public class MythlandsUserService {

	public static final Pattern VALID_EMAIL_ADDRESS_REGEX = 
		    Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
	public static final Pattern VALID_USERNAME_REGEX = 
			Pattern.compile("^[A-Za-z0-9]*$");
	
	@Autowired private MythlandsUserRepository userRepository;
	@Autowired private MythlandsCharacterRepository characterRepository;
	@Autowired private BCryptPasswordEncoder passwordEncoder;
	
	@Transactional
	public void createNewUser(String username, String email, String password, String passwordConfirm) 
			throws UserRegistrationException {
		
		// Check email/username availability
		if(email != null && userRepository.findByEmail(email) != null) {
			throw new UserRegistrationException("A user with that email already exists.");
		}
		else if(userRepository.findByUsername(username) != null) {
			throw new UserRegistrationException("A user with that username already exists.");
		}
		
		// Check username length
		if(username.length() < 8 || username.length() > 25) {
			throw new UserRegistrationException("Username must be 8 to 25 characters long.");
		}
		
		// Check username characters
		Matcher usernameMatcher = VALID_USERNAME_REGEX.matcher(username);
		if(!usernameMatcher.matches()) {
			throw new UserRegistrationException("Username contains an invalid character. Only letters and numbers may be used.");
		}
		
		// Validate email
		if(email != null) {
			Matcher emailMatcher = VALID_EMAIL_ADDRESS_REGEX.matcher(email);
			if(!emailMatcher.matches()) {
				throw new UserRegistrationException("Invalid email.");
			}
		}
		
		// Validate password
		if(!password.equals(passwordConfirm)) {
			throw new UserRegistrationException("Your passwords do not match.");
		}
		
		if(password.length() < 10 || password.length() > 75) {
			throw new UserRegistrationException("Password must be 10 to 75 characters in length.");
		}
		
		// Hash the password
		String encodedPassword = passwordEncoder.encode(password);
		
		// Create the user
		MythlandsUser user = new MythlandsUser();
		user.setUsername(username);
		user.setEmail(email);
		user.setPasswordHash(encodedPassword);
		user.getAuthorities().add("ROLE_PLAYER");
		userRepository.save(user);
		
	}
	
	@Transactional
	public void createNewCharacter(String username, String firstName, String lastName) 
			throws CharacterCreationException, UserNotFoundException {
		MythlandsUser user = getUser(username);
		
		boolean hasAlive = user.getCharacters().stream().anyMatch(c -> !c.isDeceased());
		if(hasAlive) {
			throw new CharacterCreationException("You already have a living character. You may only have one character at a time.");
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
	public JsonObject useSkillPoint(String username, SpendSkillPointMessage spend) 
			throws UserNotFoundException, NoActiveCharacterException, IllegalArgumentException {
		MythlandsUser user = getUser(username);
		
		MythlandsCharacter hero = user.getActiveCharacter();
		if(hero == null) {
			throw new NoActiveCharacterException("No active characters available to spend skill point.");
		}
		
		if(hero.getSkillPoints() < 1) {
			throw new IllegalArgumentException("No skill points available to spend.");
		}
		
		JsonObject updates = new JsonObject();
		hero.setSkillPoints(hero.getSkillPoints() - 1);
		updates.addProperty("skillPoints", hero.getSkillPoints());
		
		switch(spend.skill) {
		
		case ATTUNEMENT:
			hero.modifyAttunement(1);
			updates.addProperty("attunement", hero.getAttunement());
			break;
			
		case AVOIDANCE:
			hero.modifyAvoidance(1);
			updates.addProperty("avoidance", hero.getAvoidance());
			break;
			
		case DEXTERITY:
			hero.modifyDexterity(1);
			updates.addProperty("dexterity", hero.getDexterity());
			break;
			
		case RESISTANCE:
			hero.modifyResistance(1);
			updates.addProperty("resistance", hero.getResistance());
			break;
			
		case STRENGTH:
			hero.modifyStrength(1);
			updates.addProperty("strength", hero.getStrength());
			break;
			
		case TOUGHNESS:
			hero.modifyToughness(1);
			updates.addProperty("toughness", hero.getToughness());
			break;
			
		default:
			//TODO: handle
			break;
		
		}
		
		return updates;
	}
	
	public UserInfo getUserInfo(String username) throws UserNotFoundException {
		return new UserInfo(getUser(username));
	}
	
	public MythlandsCharacterDTO getActiveCharacter(String username) throws UserNotFoundException, NoActiveCharacterException {
		MythlandsUser user = getUser(username);
		MythlandsCharacter hero = user.getActiveCharacter();
		if(hero == null) {
			throw new NoActiveCharacterException("You have no active character.");
		}
		return new MythlandsCharacterDTO(hero);
	}
	
	public CharacterList getCharacterList(String username) throws UserNotFoundException {
		MythlandsUser user = getUser(username);
		
		List<MythlandsCharacterDTO> characters = user.getCharacters().stream()
				.map((c) -> new MythlandsCharacterDTO(c)).collect(Collectors.toList());
		MythlandsCharacter active = user.getActiveCharacter();
		int activeCharacterId = active == null ? -1 : active.getId();
		
		return new CharacterList(characters, activeCharacterId);
	}
	
	private MythlandsUser getUser(String username) throws UserNotFoundException {
		MythlandsUser user = userRepository.findByUsername(username);
		if(user == null) {
			throw new UserNotFoundException("No user with username " + username + " found.");
		}
		return user;
	}
	
}
