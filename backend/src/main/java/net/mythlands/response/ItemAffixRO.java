package net.mythlands.response;

import net.mythlands.dto.ItemAffixInstanceDTO;

public class ItemAffixRO {

	public final String description;
	
	public ItemAffixRO(ItemAffixInstanceDTO affix) {
		description = affix.getDescription();
	}
	
}
