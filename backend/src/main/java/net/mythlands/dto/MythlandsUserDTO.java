package net.mythlands.dto;

import java.util.ArrayList;
import java.util.List;

import net.mythlands.core.MythlandsUser;

public class MythlandsUserDTO {

	public final int id;
	public final String username;
	public final String email;
	public final List<String> authorities;
	public final List<MythlandsCharacterDTO> characters;
	public final MythlandsCharacterDTO activeCharacter;
	
	public MythlandsUserDTO(MythlandsUser user) {
		id = user.getId();
		username = user.getUsername();
		email = user.getEmail();
		
		authorities = new ArrayList<>();
		authorities.addAll(user.getAuthorities());
		
		characters = user.getCharacters().stream()
				.map(MythlandsCharacterDTO::new)
				.toList();
		
		activeCharacter = user.getActiveCharacter() == null ? null : new MythlandsCharacterDTO(user.getActiveCharacter());
	}
	
}
