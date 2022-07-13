package net.mythlands.response;

import net.mythlands.core.item.ItemRarity;
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
	
}
