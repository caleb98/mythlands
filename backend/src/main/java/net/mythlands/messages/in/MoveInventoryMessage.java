package net.mythlands.messages.in;

public class MoveInventoryMessage {

	public final int fromSlot;
	public final int toSlot;
	
	public MoveInventoryMessage() {
		fromSlot = -1;
		toSlot = -1;
	}
	
}
