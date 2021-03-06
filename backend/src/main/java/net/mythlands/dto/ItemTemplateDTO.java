package net.mythlands.dto;

import net.mythlands.core.item.ItemRarity;
import net.mythlands.core.item.ItemTemplate;

public class ItemTemplateDTO {

	public final String id;
	public final String name;
	public final String icon;
	public final String description;
	public final int stackSize;
	public final ItemRarity rarity;
	
	public ItemTemplateDTO(ItemTemplate template) {
		id = template.getId();
		name = template.getName();
		icon = template.getIcon();
		description = template.getDescription();
		stackSize = template.getStackSize();
		rarity = template.getRarity();
	}
	
}
