package net.calebscode.mythlands.core.item;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToMany;

@Entity
@DiscriminatorValue("Equippable")
public class EquippableItemTemplate extends ItemTemplate {

	@Enumerated(EnumType.STRING)
	private EquippableItemSlot slot;
	
	@ManyToMany
	private List<ItemAffix> affixes = new ArrayList<>();
	
	protected EquippableItemTemplate() {}
	
	public EquippableItemTemplate(String id, String name, String icon, String desc, ItemRarity rarity, EquippableItemSlot slot, ItemAffix... affixes) {
		super(id, name, icon, desc, rarity, 1);
		this.slot = slot;
		for(ItemAffix affix : affixes) {
			this.affixes.add(affix);
		}
	}
		
	public EquippableItemSlot getSlot() {
		return slot;
	}
	
	public List<ItemAffix> getAffixes() {
		return affixes;
	}
	
}
