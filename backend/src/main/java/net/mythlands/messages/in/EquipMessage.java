package net.mythlands.messages.in;

import net.mythlands.core.item.EquippableItemSlot;

public class EquipMessage {

	public final EquippableItemSlot equipSlot;
	public final int invSlot;
	
	public EquipMessage() {
		equipSlot = null;
		invSlot = -1;
	}
	
}
