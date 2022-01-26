package net.calebscode.mythlands.controller;

import java.security.Principal;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import net.calebscode.mythlands.dto.ChatMessageDTO;
import net.calebscode.mythlands.dto.MythlandsUserDTO;
import net.calebscode.mythlands.exception.ChatGroupNotFoundException;
import net.calebscode.mythlands.exception.UserNotFoundException;
import net.calebscode.mythlands.messages.in.SendChatMessage;
import net.calebscode.mythlands.messages.out.EchoChatMessage;
import net.calebscode.mythlands.service.MythlandsChatService;
import net.calebscode.mythlands.service.MythlandsUserService;

@Controller
public class ChatController {
	
	@Autowired private MythlandsChatService chatService;
	@Autowired private MythlandsUserService userService;
	
	@Autowired private SimpMessagingTemplate messenger;

	@MessageMapping("/chat")
	public void handleChatMessage(SendChatMessage message, Principal principal) {
		String cleanedMessage = message.message.trim();
		if(cleanedMessage.isBlank()) {
			return;
		}
		
		MythlandsUserDTO user;
		try {
			user = userService.getUserInfo(principal.getName());
		} catch (UserNotFoundException e) {
			e.printStackTrace();
			return;
		}
		
		try {
			if(!chatService.hasChatPermissions(user.id, message.groupId)) {
				// TODO: send error message to user
				return;
			}
		} catch (ChatGroupNotFoundException e) {
			e.printStackTrace();
			return;
		}
		
		ChatMessageDTO messageDto;
		try {
			messageDto = chatService.logMessage(user.id, message.groupId, cleanedMessage);
		} catch (UserNotFoundException | ChatGroupNotFoundException e) {
			// TODO: send error message to user
			e.printStackTrace();
			return;
		}
		
		Set<MythlandsUserDTO> sendToUsers;
		try {
			sendToUsers = chatService.getGroupUsers(messageDto.groupId);
		} catch (ChatGroupNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		
		for(MythlandsUserDTO sendTo : sendToUsers) {
			messenger.convertAndSendToUser(
				sendTo.username, 
				"/local/chat",
				new EchoChatMessage(messageDto)
			);
		}
	}
	
}
