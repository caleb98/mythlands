package net.calebscode.mythlands.dto;

import java.util.UUID;

import net.calebscode.mythlands.core.item.ItemRarity;
import net.calebscode.mythlands.core.item.MythlandsItem;

public class MythlandsItemDTO {

	public final UUID id;
	public final String name;
	public final ItemRarity rarity;
	public final int characterOwnerId;
	
	public MythlandsItemDTO(MythlandsItem item) {
		id = item.getId();
		name = item.getName();
		rarity = item.getRarity();
		characterOwnerId = item.getCharacterOwner() == null ? 
				-1 : item.getCharacterOwner().getId();
	}
	
}
