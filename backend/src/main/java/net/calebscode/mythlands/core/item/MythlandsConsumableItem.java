package net.calebscode.mythlands.core.item;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import net.calebscode.mythlands.core.Boss;
import net.calebscode.mythlands.core.action.CombatAction;

@Entity
@DiscriminatorValue("Consumable")
public class MythlandsConsumableItem extends MythlandsItem {

	@ManyToOne
	private CombatAction onConsume;
	
	private MythlandsConsumableItem() {
		super(null, null);
	}
	
	public MythlandsConsumableItem(String name, ItemRarity rarity) {
		super(name, rarity);
	}
	
	public void consume(int heroId, Boss boss) {
		onConsume.execute(heroId, boss);
	}
	
}
