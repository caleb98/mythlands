package net.mythlands.dto;

import net.mythlands.core.item.ItemAffixTemplate;

public class ItemAffixTemplateDTO {

	public final String id;
	public final String onEquipFunction;
	public final String onDequipFunction;
	public final String description;
	
	public ItemAffixTemplateDTO(ItemAffixTemplate affix) {
		id = affix.getId();
		onEquipFunction = affix.getOnEquip();
		onDequipFunction = affix.getOnDequip();
		description = affix.getDescription();
	}
	
}
