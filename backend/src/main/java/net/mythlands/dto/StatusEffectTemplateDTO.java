package net.mythlands.dto;

import net.mythlands.core.effect.StatusEffectTemplate;

public class StatusEffectTemplateDTO {

	public final String id;
	
	public final String name;
	public final String onApplyFunction;
	public final String onRemoveFunction;
	public final String description;
	public final String icon;
	public final boolean isBuff;
	public final long duration;
	
	public StatusEffectTemplateDTO(StatusEffectTemplate template) {
		id = template.getId();
		
		name = template.getName();
		onApplyFunction = template.getOnApplyFunction();
		onRemoveFunction = template.getOnRemoveFunction();
		description = template.getDescription();
		icon = template.getIcon();
		isBuff = template.isBuff();
		duration = template.getDuration();
	}
	
}
