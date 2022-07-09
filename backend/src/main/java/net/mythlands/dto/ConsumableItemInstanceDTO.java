package net.mythlands.dto;

import net.mythlands.core.item.ConsumableItemInstance;

public class ConsumableItemInstanceDTO extends ItemInstanceDTO {

	public final long cooldownStart;
	public final long cooldownFinish;
	
	public ConsumableItemInstanceDTO(ConsumableItemInstance instance) {
		super(instance);
		cooldownStart = instance.getCooldownStart();
		cooldownFinish = instance.getCooldownFinish();
	}

}
