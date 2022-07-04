package net.mythlands.core.item;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;

@Entity
@DiscriminatorValue("Equippable")
public class EquippableItemInstance extends ItemInstance {

	@ManyToMany(cascade = CascadeType.ALL)
	private List<ItemAffixInstance> affixes;
	
	protected EquippableItemInstance() {}
	
	public EquippableItemInstance(EquippableItemTemplate template, ItemAffixInstance... affixes) {
		super(template, 1);
		
		this.affixes = new ArrayList<>();
		for(var affix : affixes) {
			this.affixes.add(affix);
		}
	}
	
	public List<ItemAffixInstance> getAffixes() {
		return affixes;
	}
	
	public EquippableItemTemplate getEquippableItemTemplate() {
		return (EquippableItemTemplate) getTemplate();
	}
	
}
