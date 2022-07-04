package net.mythlands.core.item;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import net.mythlands.core.action.CombatAction;

@Entity
@DiscriminatorValue("Consumable")
public class ConsumableItemTemplate extends ItemTemplate {

	@ManyToOne
	private CombatAction onConsume;
	
	protected ConsumableItemTemplate() {}
	
	public ConsumableItemTemplate(String id, String name, String icon, String desc, ItemRarity rarity, int stackSize, CombatAction onConsume) {
		super(id, name, icon, desc, rarity, stackSize);
		this.onConsume = onConsume;
	}
	
	public CombatAction getOnConsume() {
		return onConsume;
	}
	
	public void setOnConsume(CombatAction onConsume) {
		this.onConsume = onConsume;
	}
	
}
