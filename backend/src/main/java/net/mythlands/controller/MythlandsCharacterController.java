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

import com.google.gson.Gson;

import net.mythlands.dto.ItemInstanceDTO;
import net.mythlands.exception.MythlandsServiceException;
import net.mythlands.messages.in.EquipMessage;
import net.mythlands.messages.in.MoveInventoryMessage;
import net.mythlands.messages.in.SpendSkillPointMessage;
import net.mythlands.messages.in.UseInventoryMessage;
import net.mythlands.messages.out.CharacterListMessage;
import net.mythlands.messages.out.ErrorMessage;
import net.mythlands.messages.out.ServerMessage;
import net.mythlands.service.MythlandsGameService;
import net.mythlands.service.MythlandsUserService;

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
			gameService.useSkillPoint(principal.getName(), spend.skill);
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
			var changes = gameService.useInventoryItem(principal.getName(), active.id, use.useSlot);
			if(changes.size() > 0) {
				messenger.convertAndSendToUser(principal.getName(), "/local/inventory", 
						gson.toJson(changes));
			}
		} catch (MythlandsServiceException e) {
			messenger.convertAndSendToUser(principal.getName(), "/local/error", new ErrorMessage(e.getMessage()));
		}
	}
	
	@MessageMapping("/character.equip")
	public void equipItem(EquipMessage equip, Principal principal) {
		try {
			var changes = gameService.equipItem(principal.getName(), equip.equipSlot, equip.invSlot);
			if(changes.size() > 0) {
				messenger.convertAndSendToUser(principal.getName(), "/local/inventory", 
						gson.toJson(changes));	
			}
		} catch (MythlandsServiceException e) {
			messenger.convertAndSendToUser(principal.getName(), "/local/error", new ErrorMessage(e.getMessage()));
		}
	}
	
}
