package net.mythlands.core.item;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import net.mythlands.core.action.CombatAction;

@Entity
@DiscriminatorValue("Consumable")
public class ConsumableItemTemplate extends ItemTemplate {

	@ManyToOne
	private CombatAction onConsume;
	
	@Column
	private long cooldownTime;
	
	protected ConsumableItemTemplate() {}
	
	public ConsumableItemTemplate(String id, String name, String icon, String desc, ItemRarity rarity, int stackSize, long cooldownTime, CombatAction onConsume) {
		super(id, name, icon, desc, rarity, stackSize);
		this.onConsume = onConsume;
		this.cooldownTime = cooldownTime;
	}
	
	public CombatAction getOnConsume() {
		return onConsume;
	}
	
	public void setOnConsume(CombatAction onConsume) {
		this.onConsume = onConsume;
	}
	
	public long getCooldownTime() {
		return cooldownTime;
	}
	
	public void setCooldownTime(long cooldownTime) {
		this.cooldownTime = cooldownTime;
	}
	
}
