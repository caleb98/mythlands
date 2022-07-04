package net.mythlands.dto;

import net.mythlands.core.item.ConsumableItemTemplate;
import net.mythlands.core.item.EquippableItemTemplate;
import net.mythlands.core.item.ItemInstance;
import net.mythlands.core.item.ItemTemplate;

public abstract class ItemInstanceDTO {

	public final String id;
	public final String type;
	public final ItemTemplateDTO template;
	public final int count;
	
	public ItemInstanceDTO(ItemInstance instance) {
		id = instance.getId().toString();
		count = instance.getCount();
		
		// Create the correct ItemTemplateDTO subclass depending on the type of
		// the ItemTemplate
		ItemTemplate instanceTemplate = instance.getTemplate();
		if(instanceTemplate instanceof ConsumableItemTemplate) {
			template = new ConsumableItemTemplateDTO((ConsumableItemTemplate) instanceTemplate);
			type = "Consumable";
		}
		else if(instanceTemplate instanceof EquippableItemTemplate) {
			template = new EquippableItemTemplateDTO((EquippableItemTemplate) instanceTemplate);
			type = "Equippable";
		}
		else {
			template = new ItemTemplateDTO(instanceTemplate);
			type = null;
			System.out.printf(
					"Warning! ItemInstanceDTO created with default item template type: %s (%s)\n",
					template.name, id
			);
		}
	}
	
}
