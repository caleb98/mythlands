package net.calebscode.mythlands.core.item;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import net.calebscode.mythlands.core.action.CombatAction;

@Entity
@DiscriminatorValue("Consumable")
public class ConsumableItemTemplate extends ItemTemplate {

	@ManyToOne
	private CombatAction onConsume;
	
	protected ConsumableItemTemplate() {}
	
	public ConsumableItemTemplate(String name, String icon, ItemRarity rarity, int stackSize, CombatAction onConsume) {
		super(name, icon, rarity, stackSize);
		this.onConsume = onConsume;
	}
	
	public CombatAction getOnConsume() {
		return onConsume;
	}
	
	public void setOnConsume(CombatAction onConsume) {
		this.onConsume = onConsume;
	}
	
}
