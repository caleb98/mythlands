package net.mythlands.response;

import java.util.List;

import net.mythlands.dto.MythlandsCharacterDTO;

public class CharacterEffectsRO {

	public final List<StatusEffectRO> effects;
	
	public CharacterEffectsRO(MythlandsCharacterDTO character) {
		effects = character.effects.stream()
					.map(StatusEffectRO::new)
					.toList()
					;
	}
	
}
