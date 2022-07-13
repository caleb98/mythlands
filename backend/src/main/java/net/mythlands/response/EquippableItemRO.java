package net.mythlands.response;

import java.util.List;

import net.mythlands.core.item.EquippableItemSlot;
import net.mythlands.dto.EquippableItemInstanceDTO;

public class EquippableItemRO extends ItemRO {

	public final EquippableItemSlot slot;
	public final List<ItemAffixRO> affixes;
	
	public EquippableItemRO(EquippableItemInstanceDTO item) {
		super(item);
		slot = item.getEquippableTemplate().slot;
		affixes = item.affixes.stream()
					.map(ItemAffixRO::new)
					.toList();
	}
	
}
