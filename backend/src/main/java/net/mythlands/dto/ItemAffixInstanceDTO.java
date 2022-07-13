package net.mythlands.dto;

import java.util.HashMap;
import java.util.Map;

import net.mythlands.core.item.ItemAffixInstance;

public class ItemAffixInstanceDTO {
	
	public final int id;
	public final ItemAffixTemplateDTO template;	
	public final Map<String, String> data;

	public ItemAffixInstanceDTO(ItemAffixInstance affix) {
		id = affix.getId();
		template = new ItemAffixTemplateDTO(affix.getTemplate());
		
		data = new HashMap<>();
		for(String key : affix.getData().keySet()) {
			data.put(key, affix.getData().get(key));
		}
	}
	
	public String getDescription() {
		String desc = template.description;
		for(String key : data.keySet()) {
			desc = desc.replace("{" + key + "}", data.get(key));
		}
		return desc;
	}
	
}
