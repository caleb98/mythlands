package net.mythlands.dto;

import net.mythlands.core.item.ConsumableItemTemplate;

public class ConsumableItemTemplateDTO extends ItemTemplateDTO {

	public final CombatActionDTO onConsume;
	public final long cooldownTime;
	
	public ConsumableItemTemplateDTO(ConsumableItemTemplate template) {
		super(template);
		onConsume = new CombatActionDTO(template.getOnConsume());
		cooldownTime = template.getCooldownTime();
	}
	
}
