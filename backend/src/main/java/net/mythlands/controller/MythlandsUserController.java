package net.mythlands.controller;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import net.mythlands.exception.MythlandsServiceException;
import net.mythlands.messages.out.ServerMessage;
import net.mythlands.response.CharacterRO;
import net.mythlands.response.TimestampedRO;
import net.mythlands.response.UserRO;
import net.mythlands.service.MythlandsUserService;

@Controller
public class MythlandsUserController {
	
	@Autowired private MythlandsUserService userService;
	
	@PostMapping("/user/register")
	public @ResponseBody ServerMessage addNewUser(
			@RequestParam String username, 
			@RequestParam(required = false) String email,
			@RequestParam String password,
			@RequestParam String passwordConfirm,
			HttpServletRequest request) 
	{
		
		try {
			userService.createNewUser(username, null, password, passwordConfirm);
		} catch (MythlandsServiceException e) {
			return new ServerMessage(e.getMessage(), true);
		}
		
		// Log the new user in
		try {
			request.login(username, password);
		} catch (ServletException e) {
			return new ServerMessage("Your account has been created, but you could not be logged in automatically. "
					+ "Please try refreshing and logging in manually.");
		}
		
		// Success!
		return new ServerMessage("Success!");
	}
	
	@GetMapping("/user")
	public @ResponseBody ServerMessage user(Authentication auth) {
		if(auth == null) {
			return new ServerMessage("Not logged in.", true);
		}
		
		try {
			UserRO info = new UserRO(userService.getUserInfo(auth.getName()));
			return new ServerMessage("Success!", info);
		} catch (MythlandsServiceException e) {
			return new ServerMessage(e.getMessage(), true);
		}
	}
	
	@GetMapping("/user/character")
	public @ResponseBody ServerMessage userActiveCharacter(Authentication auth) {
		if(auth == null) {
			return new ServerMessage("Not logged in.", true);
		}
		
		try {
			var character = new CharacterRO(userService.getActiveCharacter(auth.getName()));
			var data = new TimestampedRO<CharacterRO>(character);
			return new ServerMessage("Success!", data);
		} catch (MythlandsServiceException e) {
			return new ServerMessage(e.getMessage(), true);
		}
	}
	
	@GetMapping("/user/characters")
	public @ResponseBody ServerMessage userCharacters(Authentication auth) {
		if(auth == null) {
			return new ServerMessage("Not logged in.", true);
		}
		
		try {
			var characters = userService.getAllCharacters(auth.getName()).stream()
					.map(CharacterRO::new)
					.toList();
			return new ServerMessage("Success!", characters);
		} catch (MythlandsServiceException e) {
			return new ServerMessage(e.getMessage(), true);
		}
	}
	
}
