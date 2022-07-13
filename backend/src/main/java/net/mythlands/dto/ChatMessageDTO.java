package net.mythlands.dto;

import java.time.Instant;

import net.mythlands.core.chat.ChatMessage;

public class ChatMessageDTO {

	public final int id;
	public final Instant timestamp;
	public final MythlandsUserDTO user;
	public final ChatGroupDTO group;
	public final String message;
	
	public ChatMessageDTO(ChatMessage chatMessage) {
		id = chatMessage.getId();
		timestamp = Instant.from(chatMessage.getTimestamp());
		user = new MythlandsUserDTO(chatMessage.getUser());
		group = new ChatGroupDTO(chatMessage.getGroup());
		message = chatMessage.getMessage();
	}
	
}