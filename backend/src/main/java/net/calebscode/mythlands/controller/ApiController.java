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
import net.calebscode.mythlands.service.MythlandsCharacterService;
import net.calebscode.mythlands.service.MythlandsUserService;

@Controller
public class ApiController {

	@Autowired MythlandsCharacterService characterService;
	@Autowired MythlandsUserService userService;
	@Autowired Gson gson;
	
	@GetMapping("/api/halloffame")
	public @ResponseBody HallOfFameMessage getHallOfFame(
			@RequestParam(defaultValue = "10") int pageSize, 
			@RequestParam(defaultValue = "0") int pageNum) 
	{
		List<MythlandsCharacterDTO> characters = characterService.getHallOfFameCharacters(pageSize, pageNum);
		return new HallOfFameMessage(characters, pageSize, pageNum);
	}
	
}
