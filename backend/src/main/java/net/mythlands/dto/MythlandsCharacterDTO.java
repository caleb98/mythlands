package net.mythlands.dto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.mythlands.core.MythlandsCharacter;
import net.mythlands.core.item.ConsumableItemInstance;
import net.mythlands.core.item.EquippableItemInstance;

public class MythlandsCharacterDTO {
	
	public final int id;
	
	// Character Info
	public final String firstName;
	public final String lastName;
	public final boolean isDeceased;
	public final MythlandsUserDTO owner;	
	
	// Character Inventory
	public final Map<Integer, ItemInstanceDTO> inventory;
	public final int inventoryCapacity;
	
	// Character Equipment
	public final EquippableItemInstanceDTO weaponItem;
	public final EquippableItemInstanceDTO armorItem;
	public final EquippableItemInstanceDTO trinketItem;
	
	// Character Effects
	public final List<StatusEffectInstanceDTO> effects;
	
	// Stats
	public final long attackReady;
	public final int skillPoints;
	
	public final int level;
	public final int xp;
	
	public final double maxHealth;
	public final double currentHealth;
	public final double maxMana;
	public final double currentMana;
	
	public final double stamina;
	public final double spirit;
	public final double strength;
	public final double dexterity;
	public final double attunement;
	public final double toughness;
	public final double avoidance;
	public final double resistance;
	
	public final double goldGain;
	public final double xpGain;
	public final double attackCooldown;
	
	public MythlandsCharacterDTO(MythlandsCharacter hc) {
		id = hc.getId();
		
		firstName = hc.getFirstName();
		lastName = hc.getLastName();
		level = hc.getLevel();
		xp = hc.getXp();
		isDeceased = hc.isDeceased();
		
		attackReady = hc.getAttackReady();
		skillPoints = hc.getSkillPoints();
		
		owner = new MythlandsUserDTO(hc.getOwner());
		
		inventory = new HashMap<>();
		for(int slot : hc.getInventory().keySet()) {
			var item = hc.getInventory().get(slot);
			if(item instanceof EquippableItemInstance) {
				var equippable = (EquippableItemInstance) item;
				inventory.put(slot, new EquippableItemInstanceDTO(equippable));
			}
			else if(item instanceof ConsumableItemInstance) {
				var consumable = (ConsumableItemInstance) item;
				inventory.put(slot, new ConsumableItemInstanceDTO(consumable));
			}
			else {
				throw new RuntimeException("Invalid item class: " + item.getClass().getSimpleName());
			}
		}
		inventoryCapacity = hc.getInventoryCapacity();
		
		weaponItem = hc.hasWeaponItem() ? new EquippableItemInstanceDTO(hc.getWeaponItem()) : null;
		armorItem = hc.hasArmorItem() ? new EquippableItemInstanceDTO(hc.getArmorItem()) : null;
		trinketItem = hc.hasTrinketItem() ? new EquippableItemInstanceDTO(hc.getTrinketItem()) : null;
		
		effects = hc.getStatusEffects().stream()
					.map(StatusEffectInstanceDTO::new)
					.toList();
		
		maxHealth = hc.getMaxHealth();
		currentHealth = hc.getCurrentHealth();
		maxMana = hc.getMaxMana();
		currentMana = hc.getCurrentMana();
		
		stamina = hc.getStamina().getValue();
		spirit = hc.getSpirit().getValue();
		strength = hc.getStrength().getValue();
		dexterity = hc.getDexterity().getValue();
		attunement = hc.getAttunement().getValue();
		toughness = hc.getToughness().getValue();
		avoidance = hc.getAvoidance().getValue();
		resistance = hc.getResistance().getValue();
		
		goldGain = hc.getGoldGain().getValue();
		xpGain = hc.getXpGain().getValue();
		attackCooldown = hc.getAttackCooldown().getValue();
	}
	
}
