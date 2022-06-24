package net.calebscode.mythlands.dto;

import java.time.Instant;

import net.calebscode.mythlands.entity.ChatMessage;

public class ChatMessageDTO {

	public final int id;
	public final Instant timestamp;
	public final int userId;
	public final String username;
	public final int groupId;
	public final String groupName;
	public final String message;
	
	public ChatMessageDTO(ChatMessage chatMessage) {
		id = chatMessage.getId();
		timestamp = Instant.from(chatMessage.getTimestamp());
		userId = chatMessage.getUser().getId();
		username = chatMessage.getUser().getUsername();
		groupId = chatMessage.getGroup().getId();
		groupName = chatMessage.getGroup().getGroupName();
		message = chatMessage.getMessage();
	}
	
}