package net.mythlands.dto;

import net.mythlands.core.item.EquippableItemSlot;
import net.mythlands.core.item.EquippableItemTemplate;

public class EquippableItemTemplateDTO extends ItemTemplateDTO {

	public final EquippableItemSlot slot;
	
	public EquippableItemTemplateDTO(EquippableItemTemplate template) {
		super(template);
		slot = template.getSlot();
	}
	
}
