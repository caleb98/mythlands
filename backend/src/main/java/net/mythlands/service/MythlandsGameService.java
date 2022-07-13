package net.mythlands.service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;

import net.mythlands.core.Boss;
import net.mythlands.core.ContributionInfo;
import net.mythlands.core.MythlandsCharacter;
import net.mythlands.core.MythlandsUser;
import net.mythlands.core.NameGenerator;
import net.mythlands.core.StatType;
import net.mythlands.core.action.CombatAction;
import net.mythlands.core.action.CombatActionFunction;
import net.mythlands.core.action.CombatContext;
import net.mythlands.core.effect.StatusEffectInstance;
import net.mythlands.core.effect.StatusEffectTemplate;
import net.mythlands.core.item.ConsumableItemInstance;
import net.mythlands.core.item.ConsumableItemTemplate;
import net.mythlands.core.item.EquippableItemInstance;
import net.mythlands.core.item.EquippableItemSlot;
import net.mythlands.core.item.EquippableItemTemplate;
import net.mythlands.core.item.ItemAffixInstance;
import net.mythlands.core.item.ItemAffixTemplate;
import net.mythlands.core.item.ItemInstance;
import net.mythlands.core.item.ItemRarity;
import net.mythlands.core.item.ItemTemplate;
import net.mythlands.dto.BossDTO;
import net.mythlands.dto.CombatActionDTO;
import net.mythlands.dto.ConsumableItemInstanceDTO;
import net.mythlands.dto.ConsumableItemTemplateDTO;
import net.mythlands.dto.EquippableItemInstanceDTO;
import net.mythlands.dto.EquippableItemTemplateDTO;
import net.mythlands.dto.ItemAffixTemplateDTO;
import net.mythlands.dto.ItemInstanceDTO;
import net.mythlands.dto.MythlandsCharacterDTO;
import net.mythlands.dto.StatusEffectInstanceDTO;
import net.mythlands.dto.StatusEffectTemplateDTO;
import net.mythlands.event.BossDiedEvent;
import net.mythlands.event.BossUpdateEvent;
import net.mythlands.event.CharacterEffectsUpdateEvent;
import net.mythlands.event.CharacterStatsUpdateEvent;
import net.mythlands.event.CooldownUpdateEvent;
import net.mythlands.exception.MythlandsServiceException;
import net.mythlands.repository.MythlandsAffixRepository;
import net.mythlands.repository.MythlandsCharacterRepository;
import net.mythlands.repository.MythlandsCombatActionRepository;
import net.mythlands.repository.MythlandsItemRepository;
import net.mythlands.repository.MythlandsItemTemplateRepository;
import net.mythlands.repository.MythlandsStatusEffectRepository;
import net.mythlands.repository.MythlandsStatusEffectTemplateRepository;
import net.mythlands.repository.MythlandsUserRepository;

@Service
public class MythlandsGameService {

	@Autowired private MythlandsItemRepository itemRepository;
	@Autowired private MythlandsItemTemplateRepository<ItemTemplate> templateRepository;
	@Autowired private MythlandsCombatActionRepository actionRepository;	
	@Autowired private MythlandsCharacterRepository characterRepository;	
	@Autowired private MythlandsAffixRepository affixRepository;
	@Autowired private MythlandsUserRepository userRepository;
	@Autowired private MythlandsStatusEffectRepository statusRepository;
	@Autowired private MythlandsStatusEffectTemplateRepository statusTemplateRepository;
	
	private HashMap<String, CombatActionFunction> functionMap = new HashMap<>();
	
	@Autowired private ApplicationEventPublisher eventPublisher;
	@Autowired private Gson gson;
	private Logger logger = LoggerFactory.getLogger(MythlandsGameService.class);
	
	@Autowired private NameGenerator bossNameGenerator;
	private Random random = new Random();
	private Boss boss;
	private int bossCount = 1;
	private HashMap<Integer, ContributionInfo> contribs = new HashMap<>();
	private static Object bossLock = new Object();
	
	@PostConstruct
	public void init() {
		boss = new Boss("Boss " + bossCount++ + " - " + bossNameGenerator.generateName(), 15); 
	}
	
	/********************************************************/
	/*                  Character Methods                   */
	/********************************************************/
	
	/**
	 * Attacks the currently active boss with a given user's active hero.
	 * @param username
	 * @param bossId
	 * @throws MythlandsServiceException
	 */
	@Transactional
	public void attackBoss(String username, int bossId) throws MythlandsServiceException {
		MythlandsCharacter character = getUserCharacter(username);
		attackBoss(character, bossId);
	}
	
	/**
	 * Attacks the currently active boss with a given hero.
	 * @param heroId
	 * @param bossId
	 * @throws MythlandsServiceException
	 */
	@Transactional
	public void attackBoss(int heroId, int bossId) throws MythlandsServiceException {
		MythlandsCharacter character = getCharacter(heroId);
		attackBoss(character, bossId);
	}

	private void attackBoss(MythlandsCharacter hero, int bossId) throws MythlandsServiceException {
		// Make sure character is alive
		if(hero.isDeceased()) {
			throw new MythlandsServiceException("Hero is dead.");
		}
		
		// Check attack cooldown
		long currentTime = System.currentTimeMillis();
		if(currentTime < hero.getAttackReady()) {
			throw new MythlandsServiceException("Attack is still on cooldown.");
		}

		long now = System.currentTimeMillis();
//		var data = new HashMap<String, String>();
//		data.put("stat", "STRENGTH");
//		data.put("additional", "5");
//		addStatusEffectInstance(
//				"TestEffect", 
//				hero, 
//				data, 
//				now
//		);
		
		// Do boss calculations
		synchronized(bossLock) {
			
			// Check boss still alive
			if(boss.getCurrentHealth() <= 0) {
				throw new MythlandsServiceException("That boss is dead.");
			}
			
			// Check the boss the user wanted to attack
			// is still the active boss.
			if(boss.getId() != bossId) {
				throw new MythlandsServiceException("The boss you attempted to"
						+ " attack is not the currently active boss.");
			}
			
			// Do the attack
			boss.damage(1);
			if(!contribs.containsKey(hero.getId())) {
				contribs.put(hero.getId(), new ContributionInfo());
			}
			var info = contribs.get(hero.getId());
			info.addTotalDamage(1);
			info.incrementAttacks();
			
			// Check for boss kill
			if(boss.getCurrentHealth() <= 0) {
				info.setDealtKillingBlow(true);
				eventPublisher.publishEvent(new BossDiedEvent(boss, new MythlandsCharacterDTO(hero)));
				boss = new Boss(
						String.format("Boss %d - %s", bossCount++, bossNameGenerator.generateName()),
						12 + random.nextInt(8)
				);
				
				processContributorAwards();
			}
			
		}
		
		// Boss info has been updated
		eventPublisher.publishEvent(new BossUpdateEvent(boss));
		
		// Do attack post processing
		modifyHealth(hero, -1);
		grantXp(hero, 1);
		// TODO: xp gain modifier
		// TODO: gold?
		
		MythlandsCharacterDTO heroDto = new MythlandsCharacterDTO(hero);
		eventPublisher.publishEvent(new CharacterStatsUpdateEvent(heroDto));
		
		// Set attack cooldown
		int cooldownTime = (int) Math.round(hero.getAttackCooldown().getValue());
		setAttackCooldown(hero, currentTime + cooldownTime);
		// TODO: incorporate this in stats?
		eventPublisher.publishEvent(new CooldownUpdateEvent(hero.getOwner().getUsername(), cooldownTime / 1000.0));
	}
	
	@Transactional
	private void processContributorAwards() {
		// Process each contribution
		for(Entry<Integer, ContributionInfo> entry : contribs.entrySet()) {
			
			// Retrieve the hero
			MythlandsCharacter hero;
			try { 
				hero = getCharacter(entry.getKey());
			} catch (MythlandsServiceException e) {
				logger.warn("Unable to retrieve character while processing "
						+ "contribution rewards: {}", e.getMessage());
				continue;
			}
			
			// Retrieve their contributions
			var contrib = entry.getValue();
			
			// Make sure the contributing hero is not dead
			if(hero.isDeceased()) {
				continue;
			}
			
			// Add their xp
			int xpGained = 5 + (contrib.dealtKillingBlow() ? 5 : 0);
			try {
				grantXp(hero.getId(), xpGained);
				eventPublisher.publishEvent(new CharacterStatsUpdateEvent(new MythlandsCharacterDTO(hero)));
			} catch (MythlandsServiceException e) {
				logger.warn("Unable to grant xp to contributor {}: {}",
						hero.getFullName(), e.getMessage());
			}
		}
		
		// Clear the map for the next boss
		contribs.clear();
	}
	
	@Transactional
	public BossDTO getActiveBoss() {
		return new BossDTO(boss);
	}

	/**
	 * Sets the attack cooldown for a user's active character.
	 * @param username
	 * @param ready
	 * @throws MythlandsServiceException
	 */
	@Transactional
	public void setAttackCooldown(String username, long ready) throws MythlandsServiceException {
		MythlandsCharacter character = getUserCharacter(username);
		setAttackCooldown(character, ready);
	}

	/**
	 * Sets the attack cooldown for a character.
	 * @param heroId
	 * @param ready
	 * @throws MythlandsServiceException
	 */
	@Transactional
	public void setAttackCooldown(int heroId, long ready) throws MythlandsServiceException {
		MythlandsCharacter character = getCharacter(heroId);
		setAttackCooldown(character, ready);
	}
	
	private void setAttackCooldown(MythlandsCharacter character, long ready) throws MythlandsServiceException {
		character.setAttackReady(ready);
	}
	
	/**
	 * Modifies the health of a user's active character.
	 * @param username
	 * @param amount
	 * @return true if a change was made to the health value; false otherwise
	 * @throws MythlandsServiceException
	 */
	@Transactional
	public boolean modifyHealth(String username, double amount) throws MythlandsServiceException {
		MythlandsCharacter character = getUserCharacter(username);
		return modifyHealth(character, amount);
	}
	
	/**
	 * Modifies the health of a character.
	 * @param heroId
	 * @param amount
	 * @return true if a change was made to the health value; false otherwise
	 * @throws MythlandsServiceException
	 */
	@Transactional
	public boolean modifyHealth(int heroId, double amount) throws MythlandsServiceException {
		MythlandsCharacter character = getCharacter(heroId);
		return modifyHealth(character, amount);
	}
	
	private boolean modifyHealth(MythlandsCharacter character, double amount) throws MythlandsServiceException {
		double prev = character.getCurrentHealth();
		character.modifyCurrentHealth(amount);
		return prev != character.getCurrentHealth();
	}
	
	/**
	 * Modifies the mana of a user's active character.
	 * @param username
	 * @param amount
	 * @return true if a change was made to the mana value; false otherwise
	 * @throws MythlandsServiceException
	 */
	@Transactional
	public boolean modifyMana(String username, double amount) throws MythlandsServiceException {
		MythlandsCharacter character = getUserCharacter(username);
		return modifyMana(character, amount);
	}
	
	/**
	 * Modifies the mana of a character.
	 * @param heroId
	 * @param amount
	 * @return true if a change was made to the mana value; false otherwise
	 * @throws MythlandsServiceException
	 */
	@Transactional
	public boolean modifyMana(int heroId, double amount) throws MythlandsServiceException {
		MythlandsCharacter character = getCharacter(heroId);
		return modifyMana(character, amount);
	}
	
	private boolean modifyMana(MythlandsCharacter character, double amount) throws MythlandsServiceException {
		double prev = character.getCurrentMana();
		character.modifyCurrentMana(amount);
		return prev != character.getCurrentMana();
	}
	
	/**
	 * Adds a stat modification to the stat of a user's active character.
	 * @param username
	 * @param stat
	 * @param additional
	 * @param increase
	 * @param more
	 * @return true if a change was made to the stat value; false otherwise
	 * @throws MythlandsServiceException
	 */
	@Transactional
	public boolean addStatModification(String username, StatType stat, double additional, double increase, double more) 
			throws MythlandsServiceException {
		MythlandsCharacter character = getUserCharacter(username);
		return addStatModification(character, stat, additional, increase, more);
	}
	
	/**
	 * Adds a stat modification to the stat of a character.
	 * @param heroId
	 * @param stat
	 * @param additional
	 * @param increase
	 * @param more
	 * @return true if a change was made to the stat value; false otherwise
	 * @throws MythlandsServiceException
	 */
	@Transactional
	public boolean addStatModification(int heroId, StatType stat, double additional, double increase, double more) 
			throws MythlandsServiceException {
		MythlandsCharacter character = getCharacter(heroId);
		return addStatModification(character, stat, additional, increase, more);
	}
	
	private boolean addStatModification(MythlandsCharacter character, StatType stat, double additional, double increase, double more) 
			throws MythlandsServiceException {
		
		// TODO: check for illegal argument values
		
		// Apply the stat changes
		var statValue = character.getStat(stat);
		double prev = statValue.getValue();
		statValue.addAdditional(additional);
		statValue.addIncrease(increase);
		statValue.addMultiplier(more);
		
		return prev != statValue.getValue();
	}
	
	/**
	 * Removes a stat modification from the stat of a user's active character.
	 * @param username
	 * @param stat
	 * @param additional
	 * @param increase
	 * @param more
	 * @return true if a change was made to the stat value; false otherwise
	 * @throws MythlandsServiceException
	 */
	@Transactional
	public boolean removeStatModification(String username, StatType stat, double additional, double increase, double more) 
			throws MythlandsServiceException {
		MythlandsCharacter character = getUserCharacter(username);
		return removeStatModification(character, stat, additional, increase, more);
	}
	
	/**
	 * Removes a stat modification from the stat of a character.
	 * @param heroId
	 * @param stat
	 * @param additional
	 * @param increase
	 * @param more
	 * @return true if a change was made to the stat value; false otherwise
	 * @throws MythlandsServiceException
	 */
	@Transactional
	public boolean removeStatModification(int heroId, StatType stat, double additional, double increase, double more) 
			throws MythlandsServiceException {
		MythlandsCharacter character = getCharacter(heroId);
		return removeStatModification(character, stat, additional, increase, more);
	}

	private boolean removeStatModification(MythlandsCharacter character, StatType stat, double additional, double increase, double more) 
			throws MythlandsServiceException {
		
		// TODO: check for illegal argument values
		
		// Apply the stat changes
		var statValue = character.getStat(stat);
		double prev = statValue.getValue();
		statValue.removeAdditional(additional);
		statValue.removeIncrease(increase);
		statValue.removeMultiplier(more);

		return prev != statValue.getValue();
	}
	
	/**
	 * Uses a skill point to increase a user's active character's stat value
	 * @param username
	 * @param skill
	 * @throws MythlandsServiceException
	 */
	@Transactional
	public void useSkillPoint(String username, StatType skill) throws MythlandsServiceException {
		MythlandsCharacter character = getUserCharacter(username);
		useSkillPoint(character, skill);
	}
	
	/**
	 * Uses a skill point to increase a stat value
	 * @param heroId
	 * @param skill
	 * @throws MythlandsServiceException
	 */
	@Transactional
	public void useSkillPoint(int heroId, StatType skill) throws MythlandsServiceException {
		MythlandsCharacter character = getCharacter(heroId);
		useSkillPoint(character, skill);
	}

	private void useSkillPoint(MythlandsCharacter character, StatType skill) throws MythlandsServiceException {
		// Check hero meets requirements to skill up
		if(character.isDeceased()) {
			throw new MythlandsServiceException("Hero is dead.");
		}
		else if(character.getSkillPoints() < 1) {
			throw new MythlandsServiceException("No skill points available to spend.");
		}
		
		character.setSkillPoints(character.getSkillPoints() - 1);
		
		switch(skill) {
		
		case ATTUNEMENT:
			character.getAttunement().modifyBase(1);
			break;
			
		case AVOIDANCE:
			character.getAvoidance().modifyBase(1);
			break;
			
		case DEXTERITY:
			character.getDexterity().modifyBase(1);
			break;
			
		case RESISTANCE:
			character.getResistance().modifyBase(1);
			break;
			
		case STRENGTH:
			character.getStrength().modifyBase(1);
			break;
			
		case TOUGHNESS:
			character.getToughness().modifyBase(1);
			break;
			
		case SPIRIT:
			character.getSpirit().modifyBase(1);
			character.recalculateMaxMana();
			break;
			
		case STAMINA:
			character.getStamina().modifyBase(1);
			character.recalculateMaxHealth();
			break;
			
		default:
			throw new MythlandsServiceException("Stat \"" + skill + "\" is not a valid skill point stat.");
		
		}
		
		eventPublisher.publishEvent(new CharacterStatsUpdateEvent(new MythlandsCharacterDTO(character)));
	}
	
	/**
	 * Grants xp to a user's active hero and handles any level up that may occur.
	 * @param username
	 * @param xp
	 * @throws MythlandsServiceException
	 */
	@Transactional
	public void grantXp(String username, int xp) throws MythlandsServiceException {
		MythlandsCharacter character = getUserCharacter(username);
		grantXp(character, xp);
	}
	
	/**
	 * Grants xp to a hero and handles any level up that may occur.
	 * @param heroId
	 * @param xp
	 * @throws MythlandsServiceException
	 */
	@Transactional
	public void grantXp(int heroId, int xp) throws MythlandsServiceException {
		MythlandsCharacter character = getCharacter(heroId);
		grantXp(character, xp);
	}
	
	private void grantXp(MythlandsCharacter character, int xp) throws MythlandsServiceException {
		int oldLevel = character.getLevel();
		
		int newXp = character.getXp() + xp;
		int newLevel = character.getLevel();
		int newSkillPoints = character.getSkillPoints();
		int xpRequired = 10 + (10 * (newLevel - 1));
		while(newXp >= xpRequired) {
			newXp -= xpRequired;
			newLevel++;
			newSkillPoints++;
			xpRequired = 10 + (10 * (newLevel - 1));
		}
		character.setXp(newXp);
		character.setLevel(newLevel);
		character.setSkillPoints(newSkillPoints);

		// If they leveled up, we also need to recalculate health and mana.
		if(oldLevel != newLevel) {
			character.recalculateMaxHealth();
			character.recalculateMaxMana();
		}
	}
	
	@Transactional
	public List<MythlandsCharacterDTO> getHallOfFameCharacters(int pageSize, int pageNum) {
		List<MythlandsCharacter> characters = characterRepository.findByOrderByLevelDescXpDesc(
			Pageable.ofSize(pageSize).withPage(pageNum)
		);
		return characters.stream()
			.map(MythlandsCharacterDTO::new)
			.collect(Collectors.toList());
		
	}
	
	/********************************************************/
	/*                 Inventory Methods                    */
	/********************************************************/
	
	/**
	 * Equips an item from the user's active character's inventory.
	 * @param username
	 * @param equipSlot
	 * @param invSlot
	 * @throws MythlandsServiceException if the equip is invalid
	 */
	@Transactional
	public void equipItem(String username, EquippableItemSlot equipSlot, int invSlot) throws MythlandsServiceException {
		MythlandsCharacter character = getUserCharacter(username);
		equipItem(character, equipSlot, invSlot);
	}
	
	/**
	 * Equips an item from the character's inventory.
	 * @param heroId
	 * @param equipSlot
	 * @param invSlot
	 * @throws MythlandsServiceException if the equip is invalid
	 */
	@Transactional
	public void equipItem(int heroId, EquippableItemSlot equipSlot, int invSlot) throws MythlandsServiceException {
		MythlandsCharacter character = getCharacter(heroId);
		equipItem(character, equipSlot, invSlot);
	}
	

	private void equipItem(MythlandsCharacter character, EquippableItemSlot equipSlot, int invSlot) throws MythlandsServiceException {
		// Check item is equippable
		ItemInstance equipInstance = character.getInventory().get(invSlot);
		if(!(equipInstance instanceof EquippableItemInstance) && equipInstance != null) {
			throw new MythlandsServiceException("Cannot equip non-equippable item.");
		}
		
		// Get items that we will be swapping
		EquippableItemInstance equip = (equipInstance == null ? null : (EquippableItemInstance) equipInstance);
		EquippableItemInstance dequip = character.getEquipped(equipSlot);

		// Make sure item slot matches
		if(equip != null) {
			var template = equip.getEquippableItemTemplate();
			if(template.getSlot() != equipSlot) {
				throw new MythlandsServiceException("Cannot equip " + template.getSlot() + " to " + equipSlot + " slot.");
			}
		}
		
		// Swap the items
		switch(equipSlot) {
		
		case ARMOR:
			character.setArmorItem(equip);
			break;
		
		case TRINKET:
			character.setTrinketItem(equip);
			break;
		
		case WEAPON:
			character.setWeaponItem(equip);
			break;
			
		default:
			break;
		
		}
		
		CombatContext context = new CombatContext(new MythlandsCharacterDTO(character), boss);
		
		if(dequip == null) {
			character.getInventory().remove(invSlot);
		}
		else {
			character.getInventory().put(invSlot, dequip);
			
			// Call the onDequip functions for each affix on the dequipped item
			var affixes = dequip.getAffixes();
			for(var affix : affixes) {
				var data = affix.getData();
				var func = getCombatActionFunction(affix.getTemplate().getOnDequip());
				func.execute(context, data);
			}
		}

		// Call the onEquip functions for each affix on the equipped item
		if(equip != null) {
			var affixes = equip.getAffixes();
			for(var affix : affixes) {
				var data = affix.getData();
				var func = getCombatActionFunction(affix.getTemplate().getOnEquip());
				func.execute(context, data);
			}
		}
	}
	
	/**
	 * Uses an item in the user's active character's inventory.
	 * @param username
	 * @param useSlot
	 * @return
	 * @throws MythlandsServiceException
	 */
	@Transactional
	public void useInventoryItem(String username, int useSlot) throws MythlandsServiceException {
		MythlandsCharacter character = getUserCharacter(username);
		useInventoryItem(character, useSlot);
	}
	
	/**
	 * Uses an item in the character's inventory.
	 * @param heroId
	 * @param useSlot
	 * @return
	 * @throws MythlandsServiceException
	 */
	@Transactional
	public void useInventoryItem(int heroId, int useSlot) throws MythlandsServiceException {
		MythlandsCharacter character = getCharacter(heroId);
		useInventoryItem(character, useSlot);
	}

	private void useInventoryItem(MythlandsCharacter character, int useSlot) throws MythlandsServiceException {
		ItemInstance use = character.getInventory().get(useSlot);
		
		// Can't use empty slot
		if(use == null) {
			throw new MythlandsServiceException("No item in that slot to use.");
		}
		
		// Use function is based on item type
		if(use instanceof ConsumableItemInstance) {
			
			// Check item cooldown
			var consumable = (ConsumableItemInstance) use;
			if(consumable.isOnCooldown()) {
				throw new MythlandsServiceException("That item is still on cooldown.");
			}
			
			// Use the ability
			CombatAction onConsume = consumable.getConsumableItemTemplate().getOnConsume();
			CombatActionFunction function = getCombatActionFunction(onConsume.getFunctionName());
			// TODO: include boss (thread safety!)
			function.execute(new CombatContext(new MythlandsCharacterDTO(character), null), onConsume.getActionData());
			consumable.modifyCount(-1);
			consumable.triggerCooldown();
			
			// Delete item if out
			if(use.getCount() == 0) {
				character.getInventory().remove(useSlot);
				itemRepository.delete(use);
			}
		}
		// Non-usable item type
		else {
			throw new MythlandsServiceException("Item with class " + use.getClass().getSimpleName() + " cannot be used.");
		}
	}
	
	/**
	 * Moves an item in the user's active character's inventory.
	 * @param username
	 * @param fromSlot
	 * @param toSlot
	 * @throws MythlandsServiceException if the move is invalid
	 */
	@Transactional
	public void moveInventoryItem(String username, int fromSlot, int toSlot) throws MythlandsServiceException {
		MythlandsCharacter character = getUserCharacter(username);
		moveInventoryItem(character, fromSlot, toSlot);
	}
	
	/**
	 * Moves an item in the character's inventory.
	 * @param heroId
	 * @param fromSlot
	 * @param toSlot
	 * @throws MythlandsServiceException if the move is invalid
	 */
	@Transactional
	public void moveInventoryItem(int heroId, int fromSlot, int toSlot) throws MythlandsServiceException {
		MythlandsCharacter character = getCharacter(heroId);
		moveInventoryItem(character, fromSlot, toSlot);
	}

	private void moveInventoryItem(MythlandsCharacter character, int fromSlot, int toSlot) throws MythlandsServiceException {
		ItemInstance fromItem = character.getInventory().get(fromSlot);
		ItemInstance toItem = character.getInventory().get(toSlot);
		
		// If we're not moving an actual item, just don't do anything.
		if(fromItem == null) {
			throw new MythlandsServiceException("Can't move from empty item slot.");
		}
		
		// If there's not item in the slot we're moving to, just move.
		if(toItem == null) {
			character.getInventory().remove(fromSlot);
			character.getInventory().put(toSlot, fromItem);
		}
		
		// If we're moving stackable items, just move as many from
		// the fromItem into the toItem as we can.
		else if(fromItem.isStackable(toItem)) {

			int stackSize = fromItem.getTemplate().getStackSize();
			
			// If adding the items would exceed the stack size, then only add
			// as many as we need to reach the stack size, then update the
			// fromItem count.
			if(toItem.getCount() + fromItem.getCount() > stackSize) {
				int toAdd = stackSize - toItem.getCount();
				toItem.modifyCount(toAdd);
				fromItem.modifyCount(-toAdd);
			}
			// Otherwise, add full count of fromItem to toItem stack,
			// then delete the fromItem instance and clear that slot.
			else {
				toItem.modifyCount(fromItem.getCount());
				character.getInventory().remove(fromSlot);
				itemRepository.delete(fromItem);
			}
			
		}
		
		// Moving non-stackable items, so just swap them thangs
		else {
			character.getInventory().remove(toSlot);
			character.getInventory().remove(fromSlot);
			characterRepository.saveAndFlush(character);
			character.getInventory().put(toSlot, fromItem);
			character.getInventory().put(fromSlot, toItem);
		}
		
	}
	
	/**
	 * Adds an item to the user's active character's inventory.
	 * @param username
	 * @param itemInstanceId
	 * @return true if the item instance was added in its entirety; false if there is any item(s) remaining
	 * @throws MythlandsServiceException
	 */
	@Transactional
	public boolean addInventoryItem(String username, UUID itemInstanceId) throws MythlandsServiceException {
		MythlandsCharacter character = getUserCharacter(username);
		return addInventoryItem(character, itemInstanceId);
	}
	
	/**
	 * Adds an item to the user's active character's inventory.
	 * @param username
	 * @param itemInstanceId
	 * @return true if the item instance was added in its entirety; false if there is any item(s) remaining
	 * @throws MythlandsServiceException
	 */
	@Transactional
	public boolean addInventoryItem(String username, String itemInstanceId) throws MythlandsServiceException {
		MythlandsCharacter character = getUserCharacter(username);
		return addInventoryItem(character, UUID.fromString(itemInstanceId));
	}
	
	/**
	 * Adds an item to the hero's inventory.
	 * @param heroId
	 * @param itemInstanceId
	 * @return true if the item instance was added in its entirety; false if there is any item(s) remaining
	 * @throws MythlandsServiceException
	 */
	@Transactional
	public boolean addInventoryItem(int heroId, UUID itemInstanceId) throws MythlandsServiceException {
		MythlandsCharacter character = getCharacter(heroId);
		return addInventoryItem(character, itemInstanceId);
	}
	
	/**
	 * Adds an item to the hero's inventory.
	 * @param heroId
	 * @param itemInstanceId
	 * @return true if the item instance was added in its entirety; false if there is any item(s) remaining
	 * @throws MythlandsServiceException
	 */
	@Transactional
	public boolean addInventoryItem(int heroId, String itemInstanceId) throws MythlandsServiceException {
		MythlandsCharacter character = getCharacter(heroId);
		return addInventoryItem(character, UUID.fromString(itemInstanceId));
	}

	private boolean addInventoryItem(MythlandsCharacter character, UUID itemInstanceId) throws MythlandsServiceException {
		ItemInstance add = getItemInstance(itemInstanceId);
		
		// Prevent adding same item multiple times
		if(character.getInventory().containsValue(add)) {
			throw new MythlandsServiceException("Item already in inventory");
		}
		
		// Check if the character's inventory contains any items which this one could stack with.
		List<ItemInstance> stackable = character.getInventory().values().stream()
				.filter(add::isStackable)
				.collect(Collectors.toList());
		
		if(stackable.size() == 0) {
			// Item cannot be stacked, so try to add it to an empty slot in the inventory
			int insertSlot = character.getFirstEmptyInventorySlot();
			if(insertSlot == -1) {
				return false;
			}
			else {
				character.getInventory().put(insertSlot, add);
				return true;
			}
		}
		else {
			
			// Add count to existing stacks until this item has run out
			int remaining = add.getCount();
			int stackSize = add.getTemplate().getStackSize();
			for(ItemInstance existing : stackable) {
				int stackAdd = stackSize - existing.getCount();
				if(stackAdd >= remaining) {
					existing.modifyCount(remaining);
					remaining = 0;
					break;
				}
				else {
					remaining -= stackAdd;
					existing.modifyCount(stackAdd);
				}
			}
			
			// If no remaining items in this instance, delete this instance instead
			if(remaining == 0) {
				itemRepository.delete(add);
				return true;
			}
			// Otherwise, adjust the item count in the stack and then add.
			else {
				add.setCount(remaining);
				int insertSlot = character.getFirstEmptyInventorySlot();
				if(insertSlot == -1) {
					return false;
				}
				else {
					character.getInventory().put(insertSlot, add);
					return true;
				}
			}
			
		}
	}
	
	/**
	 * Retrieves the inventory for user's active character.
	 * @param username
	 * @return
	 * @throws MythlandsServiceException
	 */
	@Transactional
	public Map<Integer, ItemInstanceDTO> getInventory(String username) throws MythlandsServiceException {
		MythlandsCharacter character = getUserCharacter(username);
		return getInventory(character);
	}
	
	/**
	 * Retrieves the inventory for a given hero.
	 * @param heroId
	 * @return
	 * @throws MythlandsServiceException
	 */
	@Transactional
	public Map<Integer, ItemInstanceDTO> getInventory(int heroId) throws MythlandsServiceException {
		MythlandsCharacter character = getCharacter(heroId);
		return getInventory(character);
	}

	private Map<Integer, ItemInstanceDTO> getInventory(MythlandsCharacter character) throws MythlandsServiceException {
		Map<Integer, ItemInstance> inventory = character.getInventory();
		return inventory.keySet().stream()
				.collect(Collectors.toMap(
						slot -> slot, 
						slot -> getInstanceDTO(inventory.get(slot))
				));
	}
	
	/********************************************************/
	/*                   Action Methods                     */
	/********************************************************/
	
	@Transactional
	public CombatActionDTO createCombatAction(String actionId, Map<String, String> actionData, String functionName) 
			throws MythlandsServiceException {
		
		// Check that the function exists
		if(!functionMap.containsKey(functionName)) {
			throw new MythlandsServiceException("No action function with name: " + functionName);
		}
		
		// Check that the action doesn't already exist
		if(actionRepository.findById(actionId).isPresent()) {
			throw new MythlandsServiceException("Combat action with id " + actionId + " already exists.");
		}
		
		// Create the action
		CombatAction action = new CombatAction(actionId, functionName, actionData);
		actionRepository.save(action);
		
		return new CombatActionDTO(action);
	}
	
	public void registerCombatActionFunction(String functionName, CombatActionFunction function) throws MythlandsServiceException {
		// Check that function doesn't already exist
		if(functionMap.containsKey(functionName)) {
			throw new MythlandsServiceException("A combat action function with name " + functionName + " already exists.");
		}
		
		functionMap.put(functionName, function);
	}
	
	/********************************************************/
	/*                    Status Effects                    */
	/********************************************************/
	
	@Transactional
	public StatusEffectTemplateDTO createStatusEffectTemplate
			(String id, String name, String onApplyFunction, String onRemoveFunction, String description, String icon, boolean isBuff, long duration) 
			throws MythlandsServiceException {
		
		// Make sure the template doesn't already exist
		var existing = statusTemplateRepository.findById(id);
		if(existing.isPresent()) {
			throw new MythlandsServiceException("A status effect template with that id already exists.");
		}
		
		StatusEffectTemplate effect = new StatusEffectTemplate(id, name, onApplyFunction, onRemoveFunction, description, icon, isBuff, duration);
		statusTemplateRepository.save(effect);
		return new StatusEffectTemplateDTO(effect);
	}
	
	/**
	 * Creates a new status effect instance and adds it to the specified user's 
	 * active character's list of effects.
	 * @param templateId
	 * @param heroId
	 * @param data
	 * @param startTime
	 * @throws MythlandsServiceException
	 */
	public void addStatusEffectInstance
			(String templateId, String username, Map<String, String> data, long startTime)
			throws MythlandsServiceException {
		
		MythlandsCharacter character = getUserCharacter(username);
		addStatusEffectInstance(templateId, character, data, startTime);
		
	}
	
	/**
	 * Creates a new status effect instance and adds it to the specified character's 
	 * list of effects.
	 * @param templateId
	 * @param heroId
	 * @param data
	 * @param startTime
	 * @throws MythlandsServiceException
	 */
	public void addStatusEffectInstance
			(String templateId, int heroId, Map<String, String> data, long startTime)
			throws MythlandsServiceException {
		
		MythlandsCharacter character = getCharacter(heroId);
		addStatusEffectInstance(templateId, character, data, startTime);
	}
	
	@Transactional
	private void addStatusEffectInstance
			(String templateId, MythlandsCharacter hero, Map<String, String> data, long startTime)
			throws MythlandsServiceException {
		
		var template = getEffectTemplate(templateId);
		StatusEffectInstance instance = new StatusEffectInstance(template, data, startTime, startTime + template.getDuration());
		instance.setCharacter(hero);
		hero.getStatusEffects().add(instance);
		
		MythlandsCharacterDTO heroDto = new MythlandsCharacterDTO(hero);
		
		CombatActionFunction onApply = getCombatActionFunction(template.getOnApplyFunction());
		//TODO: boss in context & thread safety
		onApply.execute(new CombatContext(heroDto, null), data);
		System.out.println("Added effect " + templateId);
		
		eventPublisher.publishEvent(new CharacterEffectsUpdateEvent(heroDto));
		
	}
	
	/**
	 * Retrieves a list of effects for a given user's active character.
	 * @param username
	 * @return
	 * @throws MythlandsServiceException
	 */
	@Transactional
	public List<StatusEffectInstanceDTO> getStatusEffects(String username) throws MythlandsServiceException {
		MythlandsCharacter character = getUserCharacter(username);
		return getStatusEffects(character);
	}
	
	/**
	 * Retrieves a list of effects for a given character.
	 * @param heroId
	 * @return
	 * @throws MythlandsServiceException
	 */
	@Transactional
	public List<StatusEffectInstanceDTO> getStatusEffects(int heroId) throws MythlandsServiceException {
		MythlandsCharacter character = getCharacter(heroId);
		return getStatusEffects(character);
	}

	private List<StatusEffectInstanceDTO> getStatusEffects(MythlandsCharacter character) throws MythlandsServiceException {
		return character.getStatusEffects().stream()
				.map(StatusEffectInstanceDTO::new)
				.toList();
	}
	
	/**
	 * Updates the status effects for a given user's active character.
	 * @param heroId
	 * @throws MythlandsServiceException
	 */
	@Transactional
	public void updateStatusEffects(String username) throws MythlandsServiceException {
		MythlandsCharacter character = getUserCharacter(username);
		updateStatusEffects(character);
	}
	
	/**
	 * Updates the status effects for a given character.
	 * @param heroId
	 * @throws MythlandsServiceException
	 */
	@Transactional
	public void updateStatusEffects(int heroId) throws MythlandsServiceException {
		MythlandsCharacter character = getCharacter(heroId);
		updateStatusEffects(character);
	}
	
	private void updateStatusEffects(MythlandsCharacter character) throws MythlandsServiceException {
		long now = System.currentTimeMillis();
		
		MythlandsCharacterDTO heroDto = new MythlandsCharacterDTO(character);
		
		// Loop through all effects
		boolean removed = false;
		Iterator<StatusEffectInstance> iter = character.getStatusEffects().iterator();
		while(iter.hasNext()) {
			StatusEffectInstance effect = iter.next();
			
			// Check if the effect has expired
			if(effect.getFinishTime() < now) {
				
				// Remove it
				iter.remove();
				removed = true;
				
				// And run it's removal function
				StatusEffectTemplate template = effect.getTemplate();
				String onRemoveFunction = template.getOnRemoveFunction();
			
				System.out.printf("Removed effect %s (%d)\n", template.getId(), now - effect.getFinishTime());
				
				try {
					CombatActionFunction remove = getCombatActionFunction(onRemoveFunction);
					//TODO: add boss & thread safety to combat context
					remove.execute(new CombatContext(heroDto, null), effect.getData());
				} catch (MythlandsServiceException e) {
					logger.warn("Unable to run remove function " + onRemoveFunction + " for effect " + template.getName());
				}
				
			}
		}
		
		// Fire update event if necessary
		if(removed) {
			eventPublisher.publishEvent(new CharacterEffectsUpdateEvent(heroDto));
		}
	}
	
	/********************************************************/
	/*                     Item Methods                     */
	/********************************************************/
	
	@Transactional 
	public ItemAffixTemplateDTO createItemAffix(String id, String onEquipFunction, String onDequipFunction, String description) 
			throws MythlandsServiceException {
		
		// Make sure that the affix doesn't already exist
		var existing = affixRepository.findById(id);
		if(existing.isPresent()) {
			throw new MythlandsServiceException("An affix template with that id already exists.");
		}
		
		// TODO: make sure the equip functions exist?
		
		// Create the affix
		ItemAffixTemplate affix = new ItemAffixTemplate(id, onEquipFunction, onDequipFunction, description);
		affix = affixRepository.save(affix);
		return new ItemAffixTemplateDTO(affix);
	}
	
	@Transactional
	public EquippableItemTemplateDTO createEquippableItemTemplate(
			String id, String name, String icon, String desc, ItemRarity rarity, EquippableItemSlot slot) 
			throws MythlandsServiceException {
		
		// Check if identical template already exists
		var existing = templateRepository.findById(id);
		if(existing.isPresent()) {
			throw new MythlandsServiceException("A template with that id already exists.");
		}
		
		EquippableItemTemplate template = new EquippableItemTemplate(
				id, name, icon, desc, rarity, slot
		);
		
		template = templateRepository.save(template);
		return new EquippableItemTemplateDTO(template);
	}
	
	@Transactional
	public ConsumableItemTemplateDTO createConsumableItemTemplate(
			String id, String name, String icon, String desc, ItemRarity rarity, int stackSize, long cooldownTime, String actionId) 
			throws MythlandsServiceException {
	
		// Check if an identical template already exists.
		var existing = templateRepository.findById(id);
		
		if(existing.isPresent()) {
			throw new MythlandsServiceException("A template with that id already exists.");
		}
		
		var action = getCombatAction(actionId);
		ConsumableItemTemplate template = new ConsumableItemTemplate(id, name, icon, desc, rarity, stackSize, cooldownTime, action);
		template = templateRepository.save(template);
		
		return new ConsumableItemTemplateDTO(template);
	}
	
	@Transactional
	public ItemInstanceDTO createConsumableItemInstance(String templateId) throws MythlandsServiceException {
		return createConsumableItemInstance(templateId, 1, new HashMap<String, String>());
	}
	
	@Transactional
	public ItemInstanceDTO createConsumableItemInstance(String templateId, int count) throws MythlandsServiceException {
		return createConsumableItemInstance(templateId, count, new HashMap<String, String>());
	}
	
	@Transactional
	public ItemInstanceDTO createConsumableItemInstance(String templateId, int count, Map<String, String> itemData) 
			throws MythlandsServiceException { 
		
		var template = getItemTemplate(templateId);
		if(!(template instanceof ConsumableItemTemplate)) {
			throw new MythlandsServiceException("Provided item template is not of type consumable: " + templateId);
		}
		ConsumableItemInstance instance = new ConsumableItemInstance((ConsumableItemTemplate) template, count);
		itemRepository.save(instance);
		
		return getInstanceDTO(instance);		
	}
	
	@Transactional
	public EquippableItemInstanceDTO createEquippableItemInstance(String templateId, Map<String, Map<String, String>> affixes) 
			throws MythlandsServiceException {
		
		var template = getItemTemplate(templateId);
		if(!(template instanceof EquippableItemTemplate)) {
			throw new MythlandsServiceException("Provided item template is not of type equippable: " + templateId);
		}

		// Collect the affixes
		ItemAffixInstance[] affixArray = new ItemAffixInstance[affixes.size()];
		int i = 0;
		for(String affixId : affixes.keySet()) {
			try {
				var affixTemplate = getItemAffix(affixId);
				affixArray[i++] = new ItemAffixInstance(affixTemplate, affixes.get(affixId));
			} catch (MythlandsServiceException e) {
				logger.warn(e.getMessage());
			}
		}
		
		EquippableItemInstance instance = new EquippableItemInstance((EquippableItemTemplate) template, affixArray);
		itemRepository.save(instance);
		
		return new EquippableItemInstanceDTO(instance);
	}
	
	/********************************************************/
	/*               Private Utility Methods                */
	/********************************************************/
	
	private MythlandsCharacter getUserCharacter(String username) throws MythlandsServiceException {
		MythlandsUser user = userRepository.findByUsername(username);
		if(user == null) {
			throw new MythlandsServiceException("No user with username " + username);
		}
		if(user.getActiveCharacter() == null) {
			throw new MythlandsServiceException("User " + username + " has no active character.");
		}
		return user.getActiveCharacter();
	}
	
	private MythlandsCharacter getCharacter(int id) throws MythlandsServiceException {
		Optional<MythlandsCharacter> hero = characterRepository.findById(id);
		if(hero.isEmpty()) {
			throw new MythlandsServiceException("No character with id " + id + " found.");
		}
		return hero.get();
	}
	
	private ItemTemplate getItemTemplate(String templateId) throws MythlandsServiceException {
		Optional<ItemTemplate> template = templateRepository.findById(templateId);
		if(template.isEmpty()) {
			throw new MythlandsServiceException("No item template with id " + templateId + " found.");
		}
		return template.get();
	}
	
	private CombatAction getCombatAction(String actionId) throws MythlandsServiceException {
		Optional<CombatAction> action = actionRepository.findById(actionId);
		if(action.isEmpty()) {
			throw new MythlandsServiceException("No combat action with id " + actionId + " found.");
		}
		return action.get();
	}
	
	private StatusEffectTemplate getEffectTemplate(String effectId) throws MythlandsServiceException {
		Optional<StatusEffectTemplate> effect = statusTemplateRepository.findById(effectId);
		if(effect.isEmpty()) {
			throw new MythlandsServiceException("No effect template with id " + effectId + " found.");
		}
		return effect.get();
	}
	
	private CombatActionFunction getCombatActionFunction(String functionName) throws MythlandsServiceException {
		if(!functionMap.containsKey(functionName)) {
			throw new MythlandsServiceException("No combat action function with name " + functionName + " found.");
		}
		return functionMap.get(functionName);
	}
	
	private ItemInstance getItemInstance(UUID id) throws MythlandsServiceException {
		Optional<ItemInstance> instance = itemRepository.findById(id);
		if(instance.isEmpty()) {
			throw new MythlandsServiceException("No item instance with id " + id + " found.");
		}
		return instance.get();
	}
	
	private ItemAffixTemplate getItemAffix(String id) throws MythlandsServiceException {
		Optional<ItemAffixTemplate> affix = affixRepository.findById(id);
		if(affix.isEmpty()) {
			throw new MythlandsServiceException("No item affix found with id " + id);
		}
		return affix.get();
	}
	
	private String getStatString(StatType stat) {
		switch(stat) {
		
		case ATTACK_COOLDOWN:	return "attackCooldown";
		case ATTUNEMENT:		return "attunement";
		case AVOIDANCE:			return "avoidance";
		case DEXTERITY:			return "dexterity";
		case GOLD_GAIN:			return "goldGain";
		case MAX_HEALTH:		return "maxHealth";
		case MAX_MANA:			return "maxMana";
		case RESISTANCE:		return "resistance";
		case SPIRIT:			return "spirit";
		case STAMINA:			return "stamina";
		case STRENGTH:			return "strength";
		case TOUGHNESS:			return "toughness";
		case XP_GAIN:			return "attunement";
		
		default: return null;
		
		}
	}
	
	private ItemInstanceDTO getInstanceDTO(ItemInstance instance) {
		if(instance instanceof EquippableItemInstance) {
			return new EquippableItemInstanceDTO((EquippableItemInstance) instance);
		}
		else if(instance instanceof ConsumableItemInstance) {
			return new ConsumableItemInstanceDTO((ConsumableItemInstance) instance);
		}
		else {
			throw new RuntimeException("No instance dto for clas: " + instance.getClass().getSimpleName());
		}
	}
	
}
