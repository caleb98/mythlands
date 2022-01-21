package net.calebscode.heroland.character;

import java.security.Principal;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import net.calebscode.heroland.dto.CharacterListing;
import net.calebscode.heroland.response.ServerResponse;
import net.calebscode.heroland.user.HerolandUser;
import net.calebscode.heroland.user.UserRepository;

@Controller
public class HerolandCharacterController {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private HerolandCharacterRepository characterRepository;
	
	@PostMapping("/character/create")
	public @ResponseBody ServerResponse createCharacter(
			@RequestParam String firstName,
			@RequestParam String lastName,
			Principal principal)  
	{
		HerolandUser user = userRepository.findByUsername(principal.getName());
		boolean hasAlive = user.getCharacters().stream().anyMatch(c -> !c.isDeceased());
		
		if(hasAlive) {
			return new ServerResponse("You already have a living character. You may only have one character at a time.", true);
		}
		
		HerolandCharacter newChar = new HerolandCharacter();
		newChar.setFirstName(firstName);
		newChar.setLastName(lastName);
		newChar.setOwner(user);
		newChar = characterRepository.save(newChar);
		user.getCharacters().add(newChar);
		//userRepository.save(user);
		
		return new ServerResponse("Character created!");
	}
	
	@GetMapping("/character/list")
	public @ResponseBody ServerResponse listCharacters(Principal principal) {
		String username = principal.getName();
		HerolandUser user = userRepository.findByUsername(username);
		return new ServerResponse(
				"Success!", 
				user.getCharacters().stream().map((c) -> new CharacterListing(c)).collect(Collectors.toList())
		);
	}
	
}
