package net.calebscode.mythlands.dto;

import net.calebscode.mythlands.core.item.ConsumableItemTemplate;

public class ConsumableItemTemplateDTO extends ItemTemplateDTO {

	public final CombatActionDTO onConsume;
	
	public ConsumableItemTemplateDTO(ConsumableItemTemplate template) {
		super(template);
		onConsume = new CombatActionDTO(template.getOnConsume());
	}
	
}
