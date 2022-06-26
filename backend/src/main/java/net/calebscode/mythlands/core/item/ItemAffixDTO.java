package net.calebscode.mythlands.core.item;

import net.calebscode.mythlands.dto.CombatActionDTO;

public class ItemAffixDTO {

	public final CombatActionDTO onEquip;
	public final CombatActionDTO onDequip;
	
	public ItemAffixDTO(ItemAffix affix) {
		onEquip = new CombatActionDTO(affix.getOnEquip());
		onDequip = new CombatActionDTO(affix.getOnDequip());
	}
	
}
