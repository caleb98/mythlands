package net.mythlands.dto;

import java.util.HashMap;
import java.util.Map;

import net.mythlands.core.effect.StatusEffectInstance;

public class StatusEffectInstanceDTO {

	public final int id;
	public final StatusEffectTemplateDTO template;
	public final Map<String, String> data;
	public final MythlandsCharacterDTO character;
	
	public final long startTime;
	public final long finishTime;
	
	public StatusEffectInstanceDTO(StatusEffectInstance instance) {
		id = instance.getId();
		template = new StatusEffectTemplateDTO(instance.getTemplate());
		
		data = new HashMap<>();
		for(String key : instance.getData().keySet()) {
			data.put(key, instance.getData().get(key));
		}
		
		character = new MythlandsCharacterDTO(instance.getCharacter());
		
		startTime = instance.getStartTime();
		finishTime = instance.getFinishTime();
	}

	public String getDescription() {
		String desc = template.description;
		for(String key : data.keySet()) {
			desc = desc.replace("{" + key + "}", data.get(key));
		}
		return desc;
	}
	
}
