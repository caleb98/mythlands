package net.mythlands.controller;

import java.security.Principal;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import net.mythlands.dto.ChatMessageDTO;
import net.mythlands.dto.MythlandsUserDTO;
import net.mythlands.exception.MythlandsServiceException;
import net.mythlands.messages.in.ChatReportMessage;
import net.mythlands.messages.in.SendChatMessage;
import net.mythlands.messages.out.EchoChatMessage;
import net.mythlands.service.MythlandsChatService;
import net.mythlands.service.MythlandsUserService;

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
			Set<MythlandsUserDTO> sendToUsers = chatService.getGroupUsers(messageDto.group.id);
			
			for(MythlandsUserDTO sendTo : sendToUsers) {
				messenger.convertAndSendToUser(
					sendTo.username, 
					"/local/chat",
					new EchoChatMessage(messageDto)
				);
			}
			
		} catch (MythlandsServiceException e) {
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
		} catch (MythlandsServiceException e) {
			// TODO send report fail?
			e.printStackTrace();
		}
	}
	
}
