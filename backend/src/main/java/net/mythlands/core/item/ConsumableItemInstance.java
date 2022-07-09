package net.mythlands.core.item;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("Consumable")
public class ConsumableItemInstance extends ItemInstance {
	
	@Column	private long cooldownStart;
	@Column private long cooldownFinish;
	
	protected ConsumableItemInstance() {}
	
	public ConsumableItemInstance(ConsumableItemTemplate template) {
		super(template);
	}
	
	public ConsumableItemInstance(ConsumableItemTemplate template, int count) {
		super(template, count);
		cooldownStart = 0;
		cooldownFinish = 0;
	}
	
	public ConsumableItemTemplate getConsumableItemTemplate() {
		return (ConsumableItemTemplate) getTemplate();
	}
	
	public long getCooldownStart() {
		return cooldownStart;
	}
	
	public void setCooldownStart(long cooldownStart) {
		this.cooldownStart = cooldownStart;
	}

	public long getCooldownFinish() {
		return cooldownFinish;
	}
	
	public void setCooldownFinish(long cooldownFinish) {
		this.cooldownFinish = cooldownFinish;
	}
	
	public boolean isOnCooldown() {
		return System.currentTimeMillis() < cooldownFinish;
	}
	
	public void triggerCooldown() {
		cooldownStart = System.currentTimeMillis();
		cooldownFinish = cooldownStart + getConsumableItemTemplate().getCooldownTime();
	}
	
}
