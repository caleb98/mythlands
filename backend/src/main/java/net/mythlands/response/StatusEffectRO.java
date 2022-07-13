package net.mythlands.response;

import net.mythlands.dto.StatusEffectInstanceDTO;

public class StatusEffectRO {

	public final String name;
	public final String description;
	public final String icon;
	public final boolean isBuff;
	public final long duration;
	
	public final long startTime;
	public final long finishTime;
	
	public StatusEffectRO(StatusEffectInstanceDTO effect) {
		name = effect.template.name;
		description = effect.getDescription();
		icon = effect.template.icon;
		isBuff = effect.template.isBuff;
		duration = effect.template.duration;
		
		startTime = effect.startTime;
		finishTime = effect.finishTime;
	}
	
}
