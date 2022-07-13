package net.mythlands.messages.out;

import java.time.Instant;

import net.mythlands.dto.ChatMessageDTO;

public class EchoChatMessage {

	public final int id;
	public final Instant timestamp;
	public final String username;
	public final String message;
	
	public EchoChatMessage(ChatMessageDTO message) {
		id = message.id;
		timestamp = message.timestamp;
		username = message.user.username;
		this.message = message.message;
	}
	
}
