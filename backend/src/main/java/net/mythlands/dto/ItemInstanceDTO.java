package net.mythlands.dto;

import java.util.UUID;

import net.mythlands.core.item.ConsumableItemTemplate;
import net.mythlands.core.item.EquippableItemTemplate;
import net.mythlands.core.item.ItemInstance;
import net.mythlands.core.item.ItemTemplate;

public abstract class ItemInstanceDTO {

	public final UUID id;
	public final ItemTemplateDTO template;
	public final int count;
	
	public ItemInstanceDTO(ItemInstance instance) {
		id = instance.getId();
		count = instance.getCount();
		
		// Create the correct ItemTemplateDTO subclass depending on the type of
		// the ItemTemplate
		ItemTemplate instanceTemplate = instance.getTemplate();
		if(instanceTemplate instanceof ConsumableItemTemplate) {
			template = new ConsumableItemTemplateDTO((ConsumableItemTemplate) instanceTemplate);
		}
		else if(instanceTemplate instanceof EquippableItemTemplate) {
			template = new EquippableItemTemplateDTO((EquippableItemTemplate) instanceTemplate);
		}
		else {
			throw new IllegalArgumentException(
					"Item instance of class " + instance.getClass().getSimpleName() + " is invalid.");
		}
	}
	
}
