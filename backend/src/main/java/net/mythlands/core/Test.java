package net.mythlands.core;

import java.util.HashMap;
import java.util.Map;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import net.mythlands.core.item.EquippableItemSlot;
import net.mythlands.core.item.ItemRarity;
import net.mythlands.dto.EquippableItemInstanceDTO;
import net.mythlands.exception.MythlandsServiceException;
import net.mythlands.service.MythlandsGameService;

@Component
public class Test implements CommandLineRunner {

	@Autowired private MythlandsGameService gameService;
	
	@Transactional
	public void run(String... args) {
		try {
			gameService.createStatusEffectTemplate(
					"TestEffect", 
					"Test Effect", 
					"ApplyStatMod", 
					"RemoveStatMod", 
					"A test effect.", 
					"", 
					true, 
					5000
			);
		} catch (MythlandsServiceException e) {
			System.err.println(e.getMessage());
		}
		
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
					5000,
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
					5000,
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
			gameService.createItemAffix(
					"StrengthAffix", "ApplyStatMod", "RemoveStatMod",
					"+{additional} Strength"
			);
		} catch (MythlandsServiceException e) {
			System.err.println(e.getMessage());
		}
		
		try {
			gameService.createItemAffix("StaminaAffix", "ApplyStatMod", "RemoveStatMod",
					"+{additional} Stamina"
			);
		} catch (MythlandsServiceException e) {
			System.err.println(e.getMessage());
		}
		
		try {
			
			if(gameService.getInventory(1).size() == 0) {
//				var strData = new HashMap<String, String>();
//				strData.put("stat", "STRENGTH");
//				strData.put("additional", "5");
//				
//				var stmData = new HashMap<String, String>();
//				stmData.put("stat", "STAMINA");
//				stmData.put("additional", "5");
//				
//				var affixData = new HashMap<String, Map<String, String>>();
//				affixData.put("StrengthAffix", strData);
//				affixData.put("StaminaAffix", stmData);
//				
//				EquippableItemInstanceDTO dto = gameService.createEquippableItemInstance(
//						"LeatherArmor", 
//						affixData
//				);
				
				var dto = gameService.createConsumableItemInstance("LesserHealingPotion", 20);
				
				gameService.addInventoryItem(1, dto.id);
			}
		} catch (MythlandsServiceException e) {
			e.printStackTrace();
		}
	}
	
}
