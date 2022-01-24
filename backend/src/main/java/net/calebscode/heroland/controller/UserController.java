package net.calebscode.heroland.controller;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import net.calebscode.heroland.exception.UserNotFoundException;
import net.calebscode.heroland.exception.UserRegistrationException;
import net.calebscode.heroland.response.ServerResponse;
import net.calebscode.heroland.response.dto.UserInfo;
import net.calebscode.heroland.service.HerolandUserService;

@Controller
public class UserController {
	
	@Autowired private HerolandUserService userService;
	
	@GetMapping(path="/user/info")
	public @ResponseBody ServerResponse userData(Authentication auth) {
		if(auth == null) {
			return new ServerResponse("Not logged in.", true);
		}
		
		try {
			UserInfo info = userService.getUserInfo(auth.getName());
			return new ServerResponse("Success!", info);
		} catch (UserNotFoundException e) {
			return new ServerResponse(e.getMessage(), true);
		}
	}
	
	@PostMapping("/user/register")
	public @ResponseBody ServerResponse addNewUser(
			@RequestParam String username, 
			@RequestParam(required = false) String email,
			@RequestParam String password,
			@RequestParam String passwordConfirm,
			HttpServletRequest request) 
	{
		
		try {
			userService.createNewUser(username, null, password, passwordConfirm);
		} catch (UserRegistrationException e) {
			return new ServerResponse(e.getMessage(), true);
		}
		
		// Log the new user in
		try {
			request.login(username, password);
		} catch (ServletException e) {
			return new ServerResponse("Your account has been created, but you could not be logged in automatically. "
					+ "Please try refreshing and logging in manually.");
		}
		
		// Success!
		return new ServerResponse("Success!");
	}
	
}
