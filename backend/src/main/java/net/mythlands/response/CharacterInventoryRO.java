package net.mythlands.response;

import java.util.HashMap;
import java.util.Map;

import net.mythlands.dto.ConsumableItemInstanceDTO;
import net.mythlands.dto.EquippableItemInstanceDTO;
import net.mythlands.dto.MythlandsCharacterDTO;

public class CharacterInventoryRO {

	public final int capacity;
	public final Map<Integer, ItemRO> items;
	
	public CharacterInventoryRO(MythlandsCharacterDTO character) {
		capacity = character.inventoryCapacity;
		
		items = new HashMap<>();
		for(int slot : character.inventory.keySet()) {
			var item = character.inventory.get(slot);
			items.put(slot, ItemRO.getItemRO(item));
		}
	}
	
}
