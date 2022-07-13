package net.mythlands.dto;

import net.mythlands.core.chat.ChatGroup;

public class ChatGroupDTO {

	public final int id;
	public final String groupName;
	
	public ChatGroupDTO(ChatGroup group) {
		id = group.getId();
		groupName = group.getGroupName();
	}
	
}
