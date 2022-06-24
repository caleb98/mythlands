package net.calebscode.mythlands.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;

import net.calebscode.mythlands.dto.MythlandsCharacterDTO;
import net.calebscode.mythlands.messages.out.HallOfFameMessage;
import net.calebscode.mythlands.messages.out.ServerMessage;
import net.calebscode.mythlands.service.MythlandsGameService;
import net.calebscode.mythlands.service.MythlandsUserService;

@Controller
public class ApiController {

	@Autowired MythlandsGameService gameService;
	@Autowired MythlandsUserService userService;
	@Autowired Gson gson;
	
	@GetMapping("/api/halloffame")
	public @ResponseBody ServerMessage getHallOfFame(
			@RequestParam(defaultValue = "10") int pageSize, 
			@RequestParam(defaultValue = "0") int pageNum) 
	{
		if(pageSize < 1) {
			pageSize = 1;
		}
		else if(pageNum < 0) {
			return new ServerMessage("Error, invalid page number.", true);
		}
		List<MythlandsCharacterDTO> characters = gameService.getHallOfFameCharacters(pageSize, pageNum);
		return new ServerMessage("Success!", new HallOfFameMessage(characters, pageSize, pageNum));
	}
	
}
