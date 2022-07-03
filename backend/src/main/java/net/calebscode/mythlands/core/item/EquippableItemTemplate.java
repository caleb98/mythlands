package net.calebscode.mythlands.core.item;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Entity
@DiscriminatorValue("Equippable")
public class EquippableItemTemplate extends ItemTemplate {

	@Enumerated(EnumType.STRING)
	private EquippableItemSlot slot;
	
	protected EquippableItemTemplate() {}
	
	public EquippableItemTemplate(String id, String name, String icon, String desc, ItemRarity rarity, EquippableItemSlot slot) {
		super(id, name, icon, desc, rarity, 1);
		this.slot = slot;
	}
		
	public EquippableItemSlot getSlot() {
		return slot;
	}
	
}
