package net.calebscode.mythlands.controller;

import java.security.Principal;

import javax.transaction.Transactional;

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

import net.calebscode.mythlands.exception.CharacterCreationException;
import net.calebscode.mythlands.exception.NoActiveCharacterException;
import net.calebscode.mythlands.exception.UserNotFoundException;
import net.calebscode.mythlands.messages.in.SpendSkillPointMessage;
import net.calebscode.mythlands.messages.out.CharacterUpdateMessage;
import net.calebscode.mythlands.messages.out.ErrorMessage;
import net.calebscode.mythlands.response.ServerResponse;
import net.calebscode.mythlands.response.dto.CharacterList;
import net.calebscode.mythlands.service.MythlandsUserService;

@Controller
public class MythlandsCharacterController {

	@Autowired private MythlandsUserService userService;
	@Autowired private SimpMessagingTemplate messenger;
	@Autowired private Gson gson;
	
	@PostMapping("/character/create")
	public @ResponseBody ServerResponse createCharacter(
			@RequestParam String firstName,
			@RequestParam String lastName,
			Principal principal)  
	{
		
		try {
			userService.createNewCharacter(principal.getName(), firstName, lastName);
		} catch (CharacterCreationException | UserNotFoundException e) {
			return new ServerResponse(e.getMessage(), true);
		}
		
		return new ServerResponse("Character created!");
	}
	
	@GetMapping("/character/list")
	public @ResponseBody ServerResponse listCharacters(Principal principal) {
		try {
			CharacterList list = userService.getCharacterList(principal.getName());
			return new ServerResponse("Success!", list);
		} catch (UserNotFoundException e) {
			return new ServerResponse(e.getMessage(), true);
		}
	}
	
	@MessageMapping("/character.skillup")
	@Transactional
	public void spendSkillPoint(SpendSkillPointMessage spend, Principal principal) {
		try {
			JsonObject updates = userService.useSkillPoint(principal.getName(), spend);
			messenger.convertAndSendToUser(principal.getName(), "/local/character",
					gson.toJson(new CharacterUpdateMessage(updates)));
		} catch (IllegalArgumentException | UserNotFoundException | NoActiveCharacterException e) {
			messenger.convertAndSendToUser(principal.getName(), "/local/error", new ErrorMessage(e.getMessage()));
		}
	}
	
}