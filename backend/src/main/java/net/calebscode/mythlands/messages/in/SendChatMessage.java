package net.calebscode.mythlands.messages.in;

public class SendChatMessage {

	public final int groupId;
	public final String message;
	
	public SendChatMessage() {
		groupId = -1;
		message = null;
	}
	
}
