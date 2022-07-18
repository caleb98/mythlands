package net.mythlands.controller;

import java.security.Principal;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import net.mythlands.dto.ItemInstanceDTO;
import net.mythlands.exception.MythlandsServiceException;
import net.mythlands.messages.in.EquipMessage;
import net.mythlands.messages.in.MoveInventoryMessage;
import net.mythlands.messages.in.SpendSkillPointMessage;
import net.mythlands.messages.in.UseInventoryMessage;
import net.mythlands.messages.out.ErrorMessage;
import net.mythlands.messages.out.ServerMessage;
import net.mythlands.response.CharacterInventoryRO;
import net.mythlands.response.CharacterRO;
import net.mythlands.service.MythlandsGameService;
import net.mythlands.service.MythlandsUserService;

@Controller
public class MythlandsCharacterController {

	@Autowired private MythlandsUserService userService;
	@Autowired private MythlandsGameService gameService;
	@Autowired private SimpMessagingTemplate messenger;
	
	@PostMapping("/character/create")
	public @ResponseBody ServerMessage createCharacter(
			@RequestParam String firstName,
			@RequestParam String lastName,
			Principal principal)  
	{
		
		try {
			userService.createNewCharacter(principal.getName(), firstName, lastName);
		} catch (MythlandsServiceException e) {
			return new ServerMessage(e.getMessage(), true);
		}
		
		return new ServerMessage("Character created!");
	}
	
	@GetMapping("/character")
	public @ResponseBody ServerMessage getActiveCharacterData(Principal principal) {
		if(principal == null) {
			return new ServerMessage("You are not logged in.", true);
		}
		
		try {
			CharacterRO character = new CharacterRO(userService.getActiveCharacter(principal.getName()));
			return new ServerMessage("Success!", character);
		} catch (MythlandsServiceException e) {
			return new ServerMessage(e.getMessage(), true);
		}
	}
	
	@GetMapping("/character/inventory")
	public @ResponseBody ServerMessage getInventory(Principal principal) {
		try {
			var inventory = new CharacterInventoryRO(userService.getActiveCharacter(principal.getName()));
			return new ServerMessage("Success", inventory);
		} catch (MythlandsServiceException e) {
			return new ServerMessage(e.getMessage(), true);
		}
	}
	
	@MessageMapping("/character.skillup")
	public void spendSkillPoint(SpendSkillPointMessage spend, Principal principal) {
		try {
			gameService.useSkillPoint(principal.getName(), spend.skill);
		} catch (MythlandsServiceException e) {
			messenger.convertAndSendToUser(principal.getName(), "/local/error", new ErrorMessage(e.getMessage()));
		}
	}
	
	@MessageMapping("/character.moveinventory")
	public void moveInventorySlots(MoveInventoryMessage swap, Principal principal) {
		try {
			gameService.moveInventoryItem(principal.getName(), swap.fromSlot, swap.toSlot);
		} catch (MythlandsServiceException e) {
			messenger.convertAndSendToUser(principal.getName(), "/local/error", new ErrorMessage(e.getMessage()));
		}
	}
	
	@MessageMapping("/character.useinventory")
	public void useInventorySlot(UseInventoryMessage use, Principal principal) {
		try {
			gameService.useInventoryItem(principal.getName(), use.useSlot);
		} catch (MythlandsServiceException e) {
			messenger.convertAndSendToUser(principal.getName(), "/local/error", new ErrorMessage(e.getMessage()));
		}
	}
	
	@MessageMapping("/character.equip")
	public void equipItem(EquipMessage equip, Principal principal) {
		try {
			gameService.equipItem(principal.getName(), equip.equipSlot, equip.invSlot);
		} catch (MythlandsServiceException e) {
			messenger.convertAndSendToUser(principal.getName(), "/local/error", new ErrorMessage(e.getMessage()));
		}
	}
	
}
