package net.mythlands.response;

import java.util.HashMap;
import java.util.Map;

import net.mythlands.dto.ConsumableItemInstanceDTO;
import net.mythlands.dto.EquippableItemInstanceDTO;
import net.mythlands.dto.MythlandsCharacterDTO;

public class CharacterInventoryRO {

	public final Map<Integer, ItemRO> inventory;
	
	public CharacterInventoryRO(MythlandsCharacterDTO character) {
		inventory = new HashMap<>();
		for(int slot : character.inventory.keySet()) {
			var item = character.inventory.get(slot);
			
			if(item instanceof EquippableItemInstanceDTO) {
				var equippable = (EquippableItemInstanceDTO) item;
				inventory.put(slot, new EquippableItemRO(equippable));
			}
			else if(item instanceof ConsumableItemInstanceDTO) {
				var consumable = (ConsumableItemInstanceDTO) item;
				inventory.put(slot, new ConsumableItemRO(consumable));
			}
			else {
				throw new RuntimeException("Illegal class type: " + item.getClass().getSimpleName());
			}
		}
	}
	
}
