package net.mythlands.dto;

import java.util.ArrayList;

import net.mythlands.core.item.EquippableItemInstance;

public class EquippableItemInstanceDTO extends ItemInstanceDTO {

	public final ArrayList<ItemAffixInstanceDTO> affixes;
	
	public EquippableItemInstanceDTO(EquippableItemInstance instance) {
		super(instance);
		affixes = new ArrayList<>();
		for(var affix : instance.getAffixes()) {
			affixes.add(new ItemAffixInstanceDTO(affix));
		}
	}
	
	public EquippableItemTemplateDTO getEquippableTemplate() {
		return (EquippableItemTemplateDTO) template;
	}

}
