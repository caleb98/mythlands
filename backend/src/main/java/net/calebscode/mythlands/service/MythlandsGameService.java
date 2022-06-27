package net.calebscode.mythlands.service;

import java.util.ArrayList;
import java.util.HashMap;
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
import com.google.gson.JsonObject;

import net.calebscode.mythlands.core.Boss;
import net.calebscode.mythlands.core.ContributionInfo;
import net.calebscode.mythlands.core.NameGenerator;
import net.calebscode.mythlands.core.Skill;
import net.calebscode.mythlands.core.action.CombatAction;
import net.calebscode.mythlands.core.action.CombatActionFunction;
import net.calebscode.mythlands.core.action.CombatContext;
import net.calebscode.mythlands.core.item.ConsumableItemTemplate;
import net.calebscode.mythlands.core.item.EquippableItemSlot;
import net.calebscode.mythlands.core.item.EquippableItemTemplate;
import net.calebscode.mythlands.core.item.ItemAffix;
import net.calebscode.mythlands.core.item.ItemAffixDTO;
import net.calebscode.mythlands.core.item.ItemInstance;
import net.calebscode.mythlands.core.item.ItemRarity;
import net.calebscode.mythlands.core.item.ItemTemplate;
import net.calebscode.mythlands.dto.BossDTO;
import net.calebscode.mythlands.dto.CombatActionDTO;
import net.calebscode.mythlands.dto.ConsumableItemTemplateDTO;
import net.calebscode.mythlands.dto.EquippableItemTemplateDTO;
import net.calebscode.mythlands.dto.ItemInstanceDTO;
import net.calebscode.mythlands.dto.MythlandsCharacterDTO;
import net.calebscode.mythlands.entity.MythlandsCharacter;
import net.calebscode.mythlands.entity.MythlandsUser;
import net.calebscode.mythlands.event.BossDiedEvent;
import net.calebscode.mythlands.event.BossUpdateEvent;
import net.calebscode.mythlands.event.CharacterUpdateEvent;
import net.calebscode.mythlands.event.CooldownUpdateEvent;
import net.calebscode.mythlands.exception.MythlandsServiceException;
import net.calebscode.mythlands.repository.MythlandsAffixRepository;
import net.calebscode.mythlands.repository.MythlandsCharacterRepository;
import net.calebscode.mythlands.repository.MythlandsCombatActionRepository;
import net.calebscode.mythlands.repository.MythlandsItemRepository;
import net.calebscode.mythlands.repository.MythlandsItemTemplateRepository;
import net.calebscode.mythlands.repository.MythlandsUserRepository;

@Service
public class MythlandsGameService {

	@Autowired private MythlandsItemRepository itemRepository;
	@Autowired private MythlandsItemTemplateRepository<ItemTemplate> templateRepository;
	@Autowired private MythlandsCombatActionRepository actionRepository;	
	@Autowired private MythlandsCharacterRepository characterRepository;	
	@Autowired private MythlandsAffixRepository affixRepository;
	@Autowired private MythlandsUserRepository userRepository;
	
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
	
	@Transactional
	public void attackBoss(String username, int bossId) throws MythlandsServiceException {
		// Make sure character is alive
		MythlandsCharacter hero = getUserCharacter(username);
		if(hero.isDeceased()) {
			throw new MythlandsServiceException("Hero is dead.");
		}
		
		// Check attack cooldown
		long currentTime = System.currentTimeMillis();
		if(currentTime < hero.getAttackReady()) {
			throw new MythlandsServiceException("Attack is still on cooldown.");
		}
		
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
				contribs.put(hero.getId(), new ContributionInfo(username));
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
		var receivedDamage = dealDamage(username, 1);
		var xpGained = grantXp(username, 1);
		// TODO: xp gain modifier
		// TODO: gold?
		
		eventPublisher.publishEvent(new CharacterUpdateEvent(username, receivedDamage, xpGained));
		
		// Set attack cooldown
		int cooldownTime = (int) Math.round(hero.getAttackCooldown().getValue());
		setAttackCooldown(username, currentTime + cooldownTime);
		eventPublisher.publishEvent(new CooldownUpdateEvent(username, cooldownTime / 1000.0));
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
				JsonObject updates = grantXp(contrib.getUsername(), xpGained);
				eventPublisher.publishEvent(new CharacterUpdateEvent(contrib.getUsername(), updates));
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
	
	@Transactional
	public void setAttackCooldown(String username, long ready) throws MythlandsServiceException {
		MythlandsCharacter hero = getUserCharacter(username);
		hero.setAttackReady(ready);
	}
	
	@Transactional
	public JsonObject dealDamage(String username, double amount) throws MythlandsServiceException {
		MythlandsCharacter hero = getUserCharacter(username);
		hero.modifyCurrentHealth(-amount);
		double newHealth = hero.getCurrentHealth();
		
		JsonObject updates = new JsonObject();
		updates.addProperty("currentHealth", newHealth);
		if(newHealth <= 0) {
			updates.addProperty("isDeceased", true);
			hero.setDeceased(true);
		}
		
		return updates;
	}
	
	@Transactional
	public JsonObject gainHealth(String username, double amount) throws MythlandsServiceException {
		MythlandsCharacter hero = getUserCharacter(username);
		double prevHealth = hero.getCurrentHealth();
		hero.modifyCurrentHealth(amount);
		JsonObject update = new JsonObject();
		if(prevHealth != hero.getCurrentHealth()) {
			update.addProperty("currentHealth", hero.getCurrentHealth());
		}
		return update;
	}
	
	@Transactional
	public JsonObject loseMana(String username, double amount) throws MythlandsServiceException {
		MythlandsCharacter hero = getUserCharacter(username);
		double prevMana = hero.getCurrentMana();
		hero.modifyCurrentMana(-amount);
		JsonObject update = new JsonObject();
		if(prevMana != hero.getCurrentMana()) {
			update.addProperty("currentMana", hero.getCurrentMana());
		}
		return update;
	}
	
	@Transactional
	public JsonObject gainMana(String username, double amount) throws MythlandsServiceException {
		MythlandsCharacter hero = getUserCharacter(username);
		double prevMana = hero.getCurrentMana();
		hero.modifyCurrentMana(amount);
		JsonObject update = new JsonObject();
		if(prevMana != hero.getCurrentMana()) {
			update.addProperty("currentMana", hero.getCurrentMana());
		}
		return update;
	}
	
	@Transactional
	public void useSkillPoint(String username, Skill skill) throws MythlandsServiceException {
		MythlandsCharacter hero = getUserCharacter(username);
		
		// Check hero meets requirements to skill up
		if(hero.isDeceased()) {
			throw new MythlandsServiceException("Hero is dead.");
		}
		else if(hero.getSkillPoints() < 1) {
			throw new MythlandsServiceException("No skill points available to spend.");
		}
		
		JsonObject updates = new JsonObject();
		hero.setSkillPoints(hero.getSkillPoints() - 1);
		updates.addProperty("skillPoints", hero.getSkillPoints());
		
		switch(skill) {
		
		case ATTUNEMENT:
			hero.getAttunement().modifyBase(1);
			updates.addProperty("attunement", hero.getAttunement().getValue());
			break;
			
		case AVOIDANCE:
			hero.getAvoidance().modifyBase(1);
			updates.addProperty("avoidance", hero.getAvoidance().getValue());
			break;
			
		case DEXTERITY:
			hero.getDexterity().modifyBase(1);
			updates.addProperty("dexterity", hero.getDexterity().getValue());
			break;
			
		case RESISTANCE:
			hero.getResistance().modifyBase(1);
			updates.addProperty("resistance", hero.getResistance().getValue());
			break;
			
		case STRENGTH:
			hero.getStrength().modifyBase(1);
			updates.addProperty("strength", hero.getStrength().getValue());
			break;
			
		case TOUGHNESS:
			hero.getToughness().modifyBase(1);
			updates.addProperty("toughness", hero.getToughness().getValue());
			break;
			
		case SPIRIT:
			hero.getSpirit().modifyBase(1);
			hero.recalculateMaxMana();
			updates.addProperty("spirit", hero.getSpirit().getValue());
			updates.addProperty("currentMana", hero.getCurrentMana());
			updates.addProperty("maxMana", hero.getMaxMana());
			break;
			
		case STAMINA:
			hero.getStamina().modifyBase(1);
			hero.recalculateMaxHealth();
			updates.addProperty("stamina", hero.getStamina().getValue());
			updates.addProperty("currentHealth", hero.getCurrentHealth());
			updates.addProperty("maxHealth", hero.getMaxHealth());
			break;
		
		}		
		
		if(updates.size() > 0) {
			eventPublisher.publishEvent(new CharacterUpdateEvent(username, updates));
		}
	}
	
	@Transactional
	public JsonObject grantXp(String username, int xp) 
			throws MythlandsServiceException {
		
		MythlandsCharacter hero = getUserCharacter(username);
		
		int oldLevel = hero.getLevel();
		
		int newXp = hero.getXp() + xp;
		int newLevel = hero.getLevel();
		int newSkillPoints = hero.getSkillPoints();
		int xpRequired = 10 + (10 * (newLevel - 1));
		while(newXp >= xpRequired) {
			newXp -= xpRequired;
			newLevel++;
			newSkillPoints++;
			xpRequired = 10 + (10 * (newLevel - 1));
		}
		hero.setXp(newXp);
		hero.setLevel(newLevel);
		hero.setSkillPoints(newSkillPoints);
		
		JsonObject updates = new JsonObject();
		updates.addProperty("xp", newXp);
		if(oldLevel != newLevel) {
			updates.addProperty("level", newLevel);
			updates.addProperty("skillPoints", newSkillPoints);
			
			// If they leveled up, we also need to recalculate health and mana.
			hero.recalculateMaxHealth();
			hero.recalculateMaxMana();
			
			updates.addProperty("currentHealth", hero.getCurrentHealth());
			updates.addProperty("maxHealth", hero.getMaxHealth());
			updates.addProperty("currentMana", hero.getCurrentMana());
			updates.addProperty("maxMana", hero.getMaxMana());
		}
		
		return updates;
	}
	
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
	
	@Transactional
	public JsonObject equipItem(int heroId, EquippableItemSlot equipSlot, int invSlot) throws MythlandsServiceException {
		MythlandsCharacter hero = getCharacter(heroId);
		
		// Get the items that we will be swapping
		ItemInstance equip = hero.getInventory().get(invSlot);
		ItemInstance dequip = hero.getEquipped(equipSlot);

		// Make sure item is equippable (if we're even equipping anything)
		if(equip != null) {
			if(!(equip.getTemplate() instanceof EquippableItemTemplate)) {
				throw new MythlandsServiceException("Cannot equip non-equippable item.");
			}
			
			// Make sure item slot matches
			var template = (EquippableItemTemplate) equip.getTemplate();
			if(template.getSlot() != equipSlot) {
				throw new MythlandsServiceException("Cannot equip " + template.getSlot() + " to " + equipSlot + " slot.");
			}
		}
		
		// Swap the items
		switch(equipSlot) {
		
		case ARMOR:
			hero.setArmorItem(equip);
			break;
		
		case TRINKET:
			hero.setTrinketItem(equip);
			break;
		
		case WEAPON:
			hero.setWeaponItem(equip);
			break;
			
		default:
			break;
		
		}
		
		if(dequip == null) {
			hero.getInventory().remove(invSlot);
		}
		else {
			hero.getInventory().put(invSlot, dequip);
		}

		// TODO: call on equip and on dequip for affixes!
		
		// Create updates
		JsonObject updates = new JsonObject();
		updates.add("" + invSlot, gson.toJsonTree(dequip));
		updates.add("" + equipSlot, gson.toJsonTree(equip));
		return updates;
	}
	
	@Transactional
	public JsonObject useInventoryItem(String username, int heroId, int useSlot) throws MythlandsServiceException {
		MythlandsCharacter hero = getCharacter(heroId);
		ItemInstance use = hero.getInventory().get(useSlot);
		
		// Can't use empty slot
		if(use == null) {
			return new JsonObject();
		}
		
		// Use function is based on item type
		ItemTemplate template = use.getTemplate();
		if(template instanceof ConsumableItemTemplate) {
			var consumable = (ConsumableItemTemplate) template;
			CombatAction onConsume = consumable.getOnConsume();
			CombatActionFunction function = getCombatActionFunction(onConsume.getFunctionName());
			// TODO: include boss
			function.execute(new CombatContext(username, heroId, null), onConsume.getActionData());
			use.modifyCount(-1);
			
			// Delete item if out
			if(use.getCount() == 0) {
				hero.getInventory().remove(useSlot);
				itemRepository.delete(use);
			}
		}
		// Non-usable item type
		else {
			return new JsonObject();
		}
		
		JsonObject change = new JsonObject();
		change.add("" + useSlot, gson.toJsonTree(hero.getInventory().get(useSlot)));
		return change;
	}
	
	@Transactional
	public JsonObject moveInventoryItem(int heroId, int fromSlot, int toSlot) throws MythlandsServiceException {
		MythlandsCharacter hero = getCharacter(heroId);
		ItemInstance fromItem = hero.getInventory().get(fromSlot);
		ItemInstance toItem = hero.getInventory().get(toSlot);
		
		// If we're not moving an actual item, just don't do anything.
		if(fromItem == null) {
			return new JsonObject();
		}
		
		// If there's not item in the slot we're moving to, just move.
		if(toItem == null) {
			hero.getInventory().remove(fromSlot);
			hero.getInventory().put(toSlot, fromItem);
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
				hero.getInventory().remove(fromSlot);
				itemRepository.delete(fromItem);
			}
			
		}
		
		// Moving non-stackable items, so just swap them thangs
		else {
			hero.getInventory().remove(toSlot);
			hero.getInventory().remove(fromSlot);
			characterRepository.saveAndFlush(hero);
			hero.getInventory().put(toSlot, fromItem);
			hero.getInventory().put(fromSlot, toItem);
		}
		
		// Indicate the new values in each slot
		JsonObject changes = new JsonObject();
		ItemInstance fromResult = hero.getInventory().get(fromSlot);
		ItemInstance toResult = hero.getInventory().get(toSlot);
		changes.add("" + fromSlot, gson.toJsonTree(fromResult == null ? null : new ItemInstanceDTO(fromResult)));
		changes.add("" + toSlot, gson.toJsonTree(toResult == null ? null : new ItemInstanceDTO(toResult)));
		return changes;
		
	}
	
	public void addInventoryItem(int heroId, String itemInstanceId) throws MythlandsServiceException {
		addInventoryItem(heroId, UUID.fromString(itemInstanceId));
	}
	
	/**
	 * 
	 * @param heroId
	 * @param itemInstanceId
	 * @return true if the item instance was added in its entirety; false if there is any item(s) remaining
	 * @throws MythlandsServiceException
	 */
	@Transactional
	public boolean addInventoryItem(int heroId, UUID itemInstanceId) throws MythlandsServiceException {
		MythlandsCharacter hero = getCharacter(heroId);
		ItemInstance add = getItemInstance(itemInstanceId);
		
		// Prevent adding same item multiple times
		if(hero.getInventory().containsValue(add)) {
			throw new MythlandsServiceException("Item already in inventory");
		}
		
		// Check if the character's inventory contains any items which this one could stack with.
		List<ItemInstance> stackable = hero.getInventory().values().stream()
				.filter(add::isStackable)
				.collect(Collectors.toList());
		
		if(stackable.size() == 0) {
			// Item cannot be stacked, so try to add it to an empty slot in the inventory
			int insertSlot = hero.getFirstEmptyInventorySlot();
			if(insertSlot == -1) {
				return false;
			}
			else {
				hero.getInventory().put(insertSlot, add);
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
				int insertSlot = hero.getFirstEmptyInventorySlot();
				if(insertSlot == -1) {
					return false;
				}
				else {
					hero.getInventory().put(insertSlot, add);
					return true;
				}
			}
			
		}
		
	}
	
	public Map<Integer, ItemInstanceDTO> getInventory(int heroId) throws MythlandsServiceException {
		MythlandsCharacter hero = getCharacter(heroId);
		Map<Integer, ItemInstance> inventory = hero.getInventory();
		return inventory.keySet().stream()
				.collect(Collectors.toMap(
						slot -> slot, 
						slot -> new ItemInstanceDTO(inventory.get(slot))
				));
	}
	
	public boolean isDeceased(int heroId) throws MythlandsServiceException {
		return getCharacter(heroId).isDeceased();
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
	/*                     Item Methods                     */
	/********************************************************/
	
	@Transactional 
	public ItemAffixDTO createItemAffix(String id, String onEquipId, String onDequipId) throws MythlandsServiceException {
		
		// Make sure that the affix doesn't already exist
		var existing = affixRepository.findById(id);
		if(existing.isPresent()) {
			throw new MythlandsServiceException("An affix with that id already exists.");
		}
		
		// Get the equip/dequip actions
		CombatAction onEquip = getCombatAction(onEquipId);
		CombatAction onDequip = getCombatAction(onDequipId);
		
		// Create the affix
		ItemAffix affix = new ItemAffix(id, onEquip, onDequip);
		affix = affixRepository.save(affix);
		return new ItemAffixDTO(affix);
	}
	
	@Transactional
	public EquippableItemTemplateDTO createEquippableItemTemplate(
			String id, String name, String icon, String desc, ItemRarity rarity, EquippableItemSlot slot, String... affixIds) 
			throws MythlandsServiceException {
		
		// Check if identical template already exists
		var existing = templateRepository.findById(id);
		
		if(existing.isPresent()) {
			throw new MythlandsServiceException("A template with that id already exists.");
		}
		
		// Collect the affixes
		ArrayList<ItemAffix> affixes = new ArrayList<>();
		for(var affixId : affixIds) {
			affixes.add(getItemAffix(affixId));
		}
		
		EquippableItemTemplate template = new EquippableItemTemplate(
				id, name, icon, desc, rarity, slot, affixes.toArray(new ItemAffix[affixes.size()])
		);
		
		template = templateRepository.save(template);
		return new EquippableItemTemplateDTO(template);
	}
	
	@Transactional
	public ConsumableItemTemplateDTO createConsumableItemTemplate(
			String id, String name, String icon, String desc, ItemRarity rarity, int stackSize, String actionId) 
			throws MythlandsServiceException {
	
		// Check if an identical template already exists.
		var existing = templateRepository.findById(id);
		
		if(existing.isPresent()) {
			throw new MythlandsServiceException("A template with that id already exists.");
		}
		
		var action = getCombatAction(actionId);
		ConsumableItemTemplate template = new ConsumableItemTemplate(id, name, icon, desc, rarity, stackSize, action);
		template = templateRepository.save(template);
		
		return new ConsumableItemTemplateDTO(template);
	}
	
	public ItemInstanceDTO createItemInstance(String templateId) throws MythlandsServiceException {
		return createItemInstance(templateId, 1, new HashMap<String, String>());
	}
	
	public ItemInstanceDTO createItemInstance(String templateId, int count) throws MythlandsServiceException {
		return createItemInstance(templateId, count, new HashMap<String, String>());
	}
	
	@Transactional
	public ItemInstanceDTO createItemInstance(String templateId, int count, Map<String, String> itemData) 
			throws MythlandsServiceException { 
		
		var template = getItemTemplate(templateId);
		ItemInstance instance = new ItemInstance(template, count);
		itemRepository.save(instance);
		
		return new ItemInstanceDTO(instance);		
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
	
	private ItemAffix getItemAffix(String id) throws MythlandsServiceException {
		Optional<ItemAffix> affix = affixRepository.findById(id);
		if(affix.isEmpty()) {
			throw new MythlandsServiceException("No item affix found with id " + id);
		}
		return affix.get();
	}
	
}
