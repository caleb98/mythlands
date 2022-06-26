package net.calebscode.mythlands.messages.in;

import net.calebscode.mythlands.core.item.EquippableItemSlot;

public class EquipMessage {

	public final EquippableItemSlot equipSlot;
	public final int invSlot;
	
	public EquipMessage() {
		equipSlot = null;
		invSlot = -1;
	}
	
}
