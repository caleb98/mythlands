package net.mythlands.response;

import java.util.List;

import net.mythlands.dto.MythlandsCharacterDTO;

public class CharacterEffectsRO {

	public final long timestamp;
	public final List<StatusEffectRO> effects;
	
	public CharacterEffectsRO(MythlandsCharacterDTO character) {
		timestamp = System.currentTimeMillis();
		effects = character.effects.stream()
					.map(StatusEffectRO::new)
					.toList()
					;
	}
	
}
