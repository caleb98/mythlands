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
import net.calebscode.mythlands.messages.in.ChatReportMessage;
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
		try {
			
			String cleanedMessage = message.message.trim();
			if(cleanedMessage.isBlank()) {
				return;
			}
			
			MythlandsUserDTO user = userService.getUserInfo(principal.getName());
			if(!chatService.hasChatPermissions(user.id, message.groupId)) {
				// TODO: send error message to user
				return;
			}
			
			ChatMessageDTO messageDto = chatService.logMessage(user.id, message.groupId, cleanedMessage);
			Set<MythlandsUserDTO> sendToUsers = chatService.getGroupUsers(messageDto.groupId);
			
			for(MythlandsUserDTO sendTo : sendToUsers) {
				messenger.convertAndSendToUser(
					sendTo.username, 
					"/local/chat",
					new EchoChatMessage(messageDto)
				);
			}
			
		} catch (UserNotFoundException | ChatGroupNotFoundException e) {
			e.printStackTrace();
			return;
		}
	}
	
	@MessageMapping("/chat.report")
	public void handleChatReport(ChatReportMessage report, Principal principal) {
		try {
			MythlandsUserDTO user = userService.getUserInfo(principal.getName());
			chatService.addChatReport(report.messageId, user.id);
			// TODO: send confirmation for report
		} catch (UserNotFoundException e) {
			// TODO send report fail?
			e.printStackTrace();
		}
	}
	
}
