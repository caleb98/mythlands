package net.mythlands.response;

import net.mythlands.dto.ConsumableItemInstanceDTO;

public class ConsumableItemRO extends ItemRO {

	public final long cooldownStart;
	public final long cooldownFinish;
	public final long cooldownTime;
	
	public ConsumableItemRO(ConsumableItemInstanceDTO item) {
		super(item);
		
		cooldownStart = item.cooldownStart;
		cooldownFinish = item.cooldownFinish;
		cooldownTime = item.getConsumableTemplate().cooldownTime;
	}
	
}
