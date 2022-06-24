package net.calebscode.mythlands.dto;

import java.util.HashMap;
import java.util.Map;

import net.calebscode.mythlands.core.item.ConsumableItemTemplate;
import net.calebscode.mythlands.core.item.ItemInstance;
import net.calebscode.mythlands.core.item.ItemTemplate;

public class ItemInstanceDTO {

	public final String id;
	public final ItemTemplateDTO template;
	public final int count;
	public final Map<String, String> itemData;
	
	public ItemInstanceDTO(ItemInstance instance) {
		id = instance.getId().toString();
		count = instance.getCount();
		
		itemData = new HashMap<>();
		instance.getData().forEach(itemData::put);
		
		// Create the correct ItemTemplateDTO subclass depending on the type of
		// the ItemTemplate
		ItemTemplate instanceTemplate = instance.getTemplate();
		if(instanceTemplate instanceof ConsumableItemTemplate) {
			template = new ConsumableItemTemplateDTO((ConsumableItemTemplate) instanceTemplate);
		}
		else {
			template = new ItemTemplateDTO(instanceTemplate);
			System.out.printf(
					"Warning! ItemInstanceDTO created with default item template type: %s (%s)\n",
					template.name, id
			);
		}
	}
	
}
