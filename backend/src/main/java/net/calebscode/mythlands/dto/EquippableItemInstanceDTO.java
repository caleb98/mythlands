package net.calebscode.mythlands.dto;

import net.calebscode.mythlands.core.item.EquippableItemInstance;

public class EquippableItemInstanceDTO extends ItemInstanceDTO {

	//public final ArrayList<ItemAffixDTO> affixes;
	
	public EquippableItemInstanceDTO(EquippableItemInstance instance) {
		super(instance);
//		affixes = new ArrayList<>();
//		for(var affix : instance.getAffixes()) {
//			affixes.add(new ItemAffixDTO(affix));
//		}
	}

}
