package net.calebscode.mythlands.core;

import java.util.HashMap;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import net.calebscode.mythlands.core.item.ItemRarity;
import net.calebscode.mythlands.dto.ConsumableItemTemplateDTO;
import net.calebscode.mythlands.dto.ItemInstanceDTO;
import net.calebscode.mythlands.entity.MythlandsCharacter;
import net.calebscode.mythlands.exception.MythlandsServiceException;
import net.calebscode.mythlands.repository.MythlandsCharacterRepository;
import net.calebscode.mythlands.service.MythlandsGameService;

@Component
public class Test implements CommandLineRunner {

	@Autowired private MythlandsGameService gameService;
	
	@Autowired private MythlandsCharacterRepository characterRepository;
	
	@Transactional
	public void run(String... args) {
		try {
			var data = new HashMap<String, String>();
			data.put("amount", "5");
			gameService.createCombatAction("HealingPotion_Lesser", data, "HealPlayer");
		} catch (MythlandsServiceException e) {
			//e.printStackTrace();
		}
		
		try {
			gameService.createConsumableItemTemplate("Lesser Healing Potion", null, ItemRarity.COMMON, 20, "HealingPotion_Lesser");
		} catch (MythlandsServiceException e) {
			//e.printStackTrace();
		}
		
		try {
			MythlandsCharacter hero = characterRepository.getById(1);
			ItemInstanceDTO item = gameService.createItemInstance(1, 7);
			gameService.addInventoryItem(hero.getId(), item.id);
		} catch (MythlandsServiceException e) {
			e.printStackTrace();
		}
	}
	
}