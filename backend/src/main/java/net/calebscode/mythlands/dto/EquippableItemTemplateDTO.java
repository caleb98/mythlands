package net.calebscode.mythlands.dto;

import java.util.List;
import java.util.stream.Collectors;

import net.calebscode.mythlands.core.item.EquippableItemSlot;
import net.calebscode.mythlands.core.item.EquippableItemTemplate;
import net.calebscode.mythlands.core.item.ItemAffixDTO;

public class EquippableItemTemplateDTO extends ItemTemplateDTO {

	public final EquippableItemSlot slot;
	public final List<ItemAffixDTO> affixes;
	
	public EquippableItemTemplateDTO(EquippableItemTemplate template) {
		super(template);
		slot = template.getSlot();
		affixes = template.getAffixes().stream()
					.map(ItemAffixDTO::new)
					.collect(Collectors.toList());
	}
	
}
