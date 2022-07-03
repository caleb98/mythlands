package net.calebscode.mythlands.core.item;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("Consumable")
public class ConsumableItemInstance extends ItemInstance {
	
	protected ConsumableItemInstance() {}
	
	public ConsumableItemInstance(ConsumableItemTemplate template) {
		super(template);
	}
	
	public ConsumableItemInstance(ConsumableItemTemplate template, int count) {
		super(template, count);
	}
	
	public ConsumableItemTemplate getConsumableItemTemplate() {
		return (ConsumableItemTemplate) getTemplate();
	}

}
