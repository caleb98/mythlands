package net.calebscode.mythlands.core;

import java.util.HashMap;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import net.calebscode.mythlands.core.item.EquippableItemSlot;
import net.calebscode.mythlands.core.item.ItemRarity;
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
			System.err.println(e.getMessage());
		}
		
		try {
			var data = new HashMap<String, String>();
			data.put("amount", "5");
			gameService.createCombatAction("ManaPotion_Lesser", data, "RestorePlayerMana");
		} catch (MythlandsServiceException e) {
			System.err.println(e.getMessage());
		}
		
		try {
			gameService.createConsumableItemTemplate(
					"LesserHealingPotion",
					"Lesser Healing Potion",
					"/item/consumable/lesser_healing_potion.png",
					"A simple potion that restores 5 health when used.",
					ItemRarity.COMMON, 20, 
					"HealingPotion_Lesser"
			);
		} catch (MythlandsServiceException e) {
			System.err.println(e.getMessage());
		}
		
		try {
			gameService.createConsumableItemTemplate(
					"LesserManaPotion",
					"Lesser Mana Potion",
					"/item/consumable/lesser_mana_potion.png",
					"A simple potion that restores 5 mana when used.",
					ItemRarity.COMMON, 20, 
					"ManaPotion_Lesser"
			);
		} catch (MythlandsServiceException e) {
			System.err.println(e.getMessage());
		}
		
		try {
			gameService.createEquippableItemTemplate(
					"Battleaxe", 
					"Battleaxe", 
					"/item/weapon/battle_axe_1.png", 
					"A fierce battle axe.",
					ItemRarity.RARE,
					EquippableItemSlot.WEAPON
			);
		} catch (MythlandsServiceException e) {
			System.err.println(e.getMessage());
		}
		
		try {
			gameService.createEquippableItemTemplate(
					"WimsyWard", 
					"Von Hugo's Wimsy Ward", 
					"/item/trinket/celtic_yellow.png", 
					"The runes on this necklace seem to shift organically.",
					ItemRarity.RARE,
					EquippableItemSlot.TRINKET
			);
		} catch (MythlandsServiceException e) {
			System.err.println(e.getMessage());
		}
		
		try {
			gameService.createEquippableItemTemplate(
					"LeatherArmor", 
					"Leather Armor", 
					"/item/armor/leather_armor_2.png", 
					"Plain leather armor.",
					ItemRarity.RARE,
					EquippableItemSlot.ARMOR
			);
		} catch (MythlandsServiceException e) {
			System.err.println(e.getMessage());
		}
		
		try {
			MythlandsCharacter hero = characterRepository.getById(1);
			ItemInstanceDTO healingPotion = gameService.createItemInstance("LesserHealingPotion", 7);
			gameService.addInventoryItem(hero.getId(), healingPotion.id);

			ItemInstanceDTO manaPotion = gameService.createItemInstance("LesserManaPotion", 7);
			gameService.addInventoryItem(hero.getId(), manaPotion.id);
			
			ItemInstanceDTO axe = gameService.createItemInstance("Battleaxe");
			ItemInstanceDTO arm = gameService.createItemInstance("LeatherArmor");
			ItemInstanceDTO tkt = gameService.createItemInstance("WimsyWard");
			
			gameService.addInventoryItem(hero.getId(), axe.id);
			gameService.addInventoryItem(hero.getId(), arm.id);
			gameService.addInventoryItem(hero.getId(), tkt.id);
		} catch (MythlandsServiceException e) {
			e.printStackTrace();
		}
	}
	
}
