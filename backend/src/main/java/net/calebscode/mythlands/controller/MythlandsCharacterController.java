package net.calebscode.mythlands.controller;

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

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import net.calebscode.mythlands.dto.ItemInstanceDTO;
import net.calebscode.mythlands.exception.MythlandsServiceException;
import net.calebscode.mythlands.messages.in.MoveInventoryMessage;
import net.calebscode.mythlands.messages.in.SpendSkillPointMessage;
import net.calebscode.mythlands.messages.in.UseInventoryMessage;
import net.calebscode.mythlands.messages.out.CharacterListMessage;
import net.calebscode.mythlands.messages.out.CharacterUpdateMessage;
import net.calebscode.mythlands.messages.out.ErrorMessage;
import net.calebscode.mythlands.messages.out.ServerMessage;
import net.calebscode.mythlands.service.MythlandsGameService;
import net.calebscode.mythlands.service.MythlandsUserService;

@Controller
public class MythlandsCharacterController {

	@Autowired private MythlandsUserService userService;
	@Autowired private MythlandsGameService gameService;
	@Autowired private SimpMessagingTemplate messenger;
	@Autowired private Gson gson;
	
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
	
	@GetMapping("/character/list")
	public @ResponseBody ServerMessage listCharacters(Principal principal) {
		try {
			CharacterListMessage list = userService.getCharacterList(principal.getName());
			return new ServerMessage("Success!", list);
		} catch (MythlandsServiceException e) {
			return new ServerMessage(e.getMessage(), true);
		}
	}
	
	@GetMapping("/character/inventory")
	public @ResponseBody ServerMessage getInventory(Principal principal) {
		try {
			Map<Integer, ItemInstanceDTO> inventory = gameService.getInventory(
					userService.getActiveCharacter(principal.getName()).id
			);
			return new ServerMessage("Success", inventory);
		} catch (MythlandsServiceException e) {
			return new ServerMessage(e.getMessage(), true);
		}
	}
	
	@MessageMapping("/character.skillup")
	public void spendSkillPoint(SpendSkillPointMessage spend, Principal principal) {
		try {
			var active = userService.getActiveCharacter(principal.getName());
			JsonObject updates = gameService.useSkillPoint(active.id, spend);
			messenger.convertAndSendToUser(principal.getName(), "/local/character",
					gson.toJson(new CharacterUpdateMessage(updates)));
		} catch (MythlandsServiceException e) {
			messenger.convertAndSendToUser(principal.getName(), "/local/error", new ErrorMessage(e.getMessage()));
		}
	}
	
	@MessageMapping("/character.moveinventory")
	public void moveInventorySlots(MoveInventoryMessage swap, Principal principal) {
		try {
			var active = userService.getActiveCharacter(principal.getName());
			var changes = gameService.moveInventoryItem(active.id, swap.fromSlot, swap.toSlot);
			if(changes.size() > 0) {
				messenger.convertAndSendToUser(principal.getName(), "/local/inventory", 
						gson.toJson(changes));
			}
		} catch (MythlandsServiceException e) {
			messenger.convertAndSendToUser(principal.getName(), "/local/error", new ErrorMessage(e.getMessage()));
		}
	}
	
	@MessageMapping("/character.useinventory")
	public void useInventorySlot(UseInventoryMessage use, Principal principal) {
		try {
			var active = userService.getActiveCharacter(principal.getName());
			var changes = gameService.useInventoryItem(active.id, use.useSlot);
			if(changes.size() > 0) {
				messenger.convertAndSendToUser(principal.getName(), "/local/inventory", 
						gson.toJson(changes));
			}
		} catch (MythlandsServiceException e) {
			messenger.convertAndSendToUser(principal.getName(), "/local/error", new ErrorMessage(e.getMessage()));
		}
	}
	
}
