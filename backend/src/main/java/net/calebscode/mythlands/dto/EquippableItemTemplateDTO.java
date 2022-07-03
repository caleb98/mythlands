package net.calebscode.mythlands.dto;

import net.calebscode.mythlands.core.item.EquippableItemSlot;
import net.calebscode.mythlands.core.item.EquippableItemTemplate;

public class EquippableItemTemplateDTO extends ItemTemplateDTO {

	public final EquippableItemSlot slot;
	
	public EquippableItemTemplateDTO(EquippableItemTemplate template) {
		super(template);
		slot = template.getSlot();
	}
	
}
