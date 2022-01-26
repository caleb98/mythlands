package net.calebscode.mythlands.messages.out;

import java.time.Instant;

import net.calebscode.mythlands.dto.ChatMessageDTO;

public class EchoChatMessage {

	public final int id;
	public final Instant timestamp;
	public final String username;
	public final String message;
	
	public EchoChatMessage(ChatMessageDTO message) {
		id = message.id;
		timestamp = message.timestamp;
		username = message.username;
		this.message = message.message;
	}
	
}