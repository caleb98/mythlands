package net.mythlands.response;

import net.mythlands.dto.MythlandsCharacterDTO;

public class CharacterEquipmentRO {

	public final EquippableItemRO weaponItem;
	public final EquippableItemRO armorItem;
	public final EquippableItemRO trinketItem;
	
	public CharacterEquipmentRO(MythlandsCharacterDTO character) {
		weaponItem = character.weaponItem == null ? null : new EquippableItemRO(character.weaponItem);
		armorItem = character.armorItem == null ? null : new EquippableItemRO(character.armorItem);
		trinketItem = character.trinketItem == null ? null : new EquippableItemRO(character.trinketItem);
	}
	
}
