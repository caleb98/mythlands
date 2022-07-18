package net.mythlands.response;

import net.mythlands.core.item.ItemRarity;
import net.mythlands.dto.ConsumableItemInstanceDTO;
import net.mythlands.dto.EquippableItemInstanceDTO;
import net.mythlands.dto.ItemInstanceDTO;

public abstract class ItemRO {

	public final String id;
	
	public final String name;
	public final String description;
	public final String icon;
	public final ItemRarity rarity;
	
	public final int count;
	public final int stackSize;
	
	public ItemRO(ItemInstanceDTO item) {
		id = item.id.toString();
		
		name = item.template.name;
		description = item.template.description;
		icon = item.template.icon;
		rarity = item.template.rarity;
		
		count = item.count;
		stackSize = item.template.stackSize;
	}
	
	public static ItemRO getItemRO(ItemInstanceDTO instance) {
		if(instance == null) {
			return null;
		}
		else if(instance instanceof EquippableItemInstanceDTO) {
			var equippable = (EquippableItemInstanceDTO) instance;
			return new EquippableItemRO(equippable);
		}
		else if(instance instanceof ConsumableItemInstanceDTO) {
			var consumable = (ConsumableItemInstanceDTO) instance;
			return new ConsumableItemRO(consumable);
		}
		else {
			throw new RuntimeException("Illegal class type: " + instance.getClass().getSimpleName());
		}
	}
	
}
