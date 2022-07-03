package net.calebscode.mythlands.dto;

import net.calebscode.mythlands.core.item.ItemAffixTemplate;

public class ItemAffixDTO {

	public final String onEquipFunction;
	public final String onDequipFunction;
	
	public ItemAffixDTO(ItemAffixTemplate affix) {
		onEquipFunction = affix.getOnEquip();
		onDequipFunction = affix.getOnDequip();
	}
	
}
