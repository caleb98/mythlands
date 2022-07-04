package net.mythlands.dto;

import java.util.HashMap;
import java.util.Map;

import net.mythlands.core.item.ItemAffixInstance;

public class ItemAffixInstanceDTO {
	
	public final int id;
	public final String description;
	public final Map<String, String> data;

	public ItemAffixInstanceDTO(ItemAffixInstance affix) {
		id = affix.getId();
		description = affix.getDescription();
		
		data = new HashMap<>();
		for(String key : affix.getData().keySet()) {
			data.put(key, affix.getData().get(key));
		}
	}
	
}
