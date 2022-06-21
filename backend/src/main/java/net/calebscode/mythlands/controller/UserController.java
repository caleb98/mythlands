package net.calebscode.mythlands.controller;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import net.calebscode.mythlands.dto.MythlandsUserDTO;
import net.calebscode.mythlands.exception.UserNotFoundException;
import net.calebscode.mythlands.exception.UserRegistrationException;
import net.calebscode.mythlands.messages.out.ServerMessage;
import net.calebscode.mythlands.service.MythlandsUserService;

@Controller
public class UserController {
	
	@Autowired private MythlandsUserService userService;
	
	@GetMapping(path="/user/info")
	public @ResponseBody ServerMessage userData(Authentication auth) {
		if(auth == null) {
			return new ServerMessage("Not logged in.", true);
		}
		
		try {
			MythlandsUserDTO info = userService.getUserInfo(auth.getName());
			return new ServerMessage("Success!", info);
		} catch (UserNotFoundException e) {
			return new ServerMessage(e.getMessage(), true);
		}
	}
	
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
		} catch (UserRegistrationException e) {
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
	
}
