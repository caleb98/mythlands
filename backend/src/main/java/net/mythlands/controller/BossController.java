package net.mythlands.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import net.mythlands.dto.BossDTO;
import net.mythlands.exception.MythlandsServiceException;
import net.mythlands.messages.in.AttackMessage;
import net.mythlands.messages.out.ErrorMessage;
import net.mythlands.service.MythlandsGameService;

@Controller
public class BossController {
	
	@Autowired private MythlandsGameService gameService;
	@Autowired private SimpMessagingTemplate messenger;

	@MessageMapping("/attack")
	public void attack(AttackMessage attack, Principal principal) {
		try {
			gameService.attackBoss(principal.getName(), attack.bossId);
		} catch (MythlandsServiceException e) {
			messenger.convertAndSendToUser(principal.getName(), "/local/error", 
					new ErrorMessage(e.getMessage()));
		}
	}	
	
	@GetMapping("/game/bossinfo")
	public @ResponseBody BossDTO getBossInfo() {
		return gameService.getActiveBoss();
	}
	
}
