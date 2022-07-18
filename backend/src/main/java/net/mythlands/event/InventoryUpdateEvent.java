package net.mythlands.event;

import java.util.Map;

import net.mythlands.dto.ItemInstanceDTO;
import net.mythlands.dto.MythlandsCharacterDTO;

public class InventoryUpdateEvent {

	public final MythlandsCharacterDTO character;
	public final Map<Integer, ItemInstanceDTO> updates;
	
	public InventoryUpdateEvent(MythlandsCharacterDTO character, Map<Integer, ItemInstanceDTO> updates) {
		this.character = character;
		this.updates = updates;
	}
	
}
