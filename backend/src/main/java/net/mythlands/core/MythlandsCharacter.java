package net.mythlands.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import net.mythlands.core.effect.StatusEffectInstance;
import net.mythlands.core.item.EquippableItemInstance;
import net.mythlands.core.item.EquippableItemSlot;
import net.mythlands.core.item.EquippableItemTemplate;
import net.mythlands.core.item.ItemInstance;

@Entity
@Table(name = "mythlands_character")
public class MythlandsCharacter {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	/********************************************************/
	/*                   Character Data                     */
	/********************************************************/
	
	@Column(nullable = false) private String firstName;
	@Column(nullable = false) private String lastName;
	@Column(nullable = false) private int level = 1;
	@Column(nullable = false) private int xp = 0;
	@Column(nullable = false) private boolean isDeceased = false;
	
	@Column(nullable = false) private long attackReady = 0;
	@Column(nullable = false) private int skillPoints = 0;
	
	@ManyToOne
	@JoinColumn(name = "owner_id")
	private MythlandsUser owner;

	@Cascade(CascadeType.ALL)
	@ElementCollection
	@MapKeyColumn(name = "inventory_slot")
	@JoinTable(
			name = "character_inventory",
			inverseJoinColumns = @JoinColumn(name = "item_instance_id")			
	)
	private Map<Integer, ItemInstance> inventory;
	
	@Column(nullable = false) 
	private int inventoryCapacity = 100;
	
	@OneToOne(cascade = {javax.persistence.CascadeType.ALL})
	private EquippableItemInstance weaponItem;
	@OneToOne(cascade = {javax.persistence.CascadeType.ALL})
	private EquippableItemInstance armorItem;
	@OneToOne(cascade = {javax.persistence.CascadeType.ALL})
	private EquippableItemInstance trinketItem;
	
	@OneToMany(mappedBy = "character", orphanRemoval = true)
	@Cascade(CascadeType.ALL)
	private List<StatusEffectInstance> statusEffects;
	
	/********************************************************/
	/*                     Core Stats                       */
	/********************************************************/
	
	@Embedded
	@AttributeOverrides({
		@AttributeOverride(column = @Column(name = "max_heath_base"), name = "base"),
		@AttributeOverride(column = @Column(name = "max_heath_additional"), name = "additional"),
		@AttributeOverride(column = @Column(name = "max_heath_increase"), name = "increase"),
		@AttributeOverride(column = @Column(name = "max_heath_multiplier"), name = "multiplier")
	})
	private StatValue maxHealth = new StatValue();
	private double currentHealth;
	
	@Embedded
	@AttributeOverrides({
		@AttributeOverride(column = @Column(name = "max_mana_base"), name = "base"),
		@AttributeOverride(column = @Column(name = "max_mana_additional"), name = "additional"),
		@AttributeOverride(column = @Column(name = "max_mana_increase"), name = "increase"),
		@AttributeOverride(column = @Column(name = "max_mana_multiplier"), name = "multiplier")
	})
	private StatValue maxMana = new StatValue();
	private double currentMana;
	
	@Embedded
	@AttributeOverrides({
		@AttributeOverride(column = @Column(name = "stamina_base"), name = "base"),
		@AttributeOverride(column = @Column(name = "stamina_additional"), name = "additional"),
		@AttributeOverride(column = @Column(name = "stamina_increase"), name = "increase"),
		@AttributeOverride(column = @Column(name = "stamina_multiplier"), name = "multiplier")
	})
	private StatValue stamina = new StatValue(1);
	
	@Embedded
	@AttributeOverrides({
		@AttributeOverride(column = @Column(name = "spirit_base"), name = "base"),
		@AttributeOverride(column = @Column(name = "spirit_additional"), name = "additional"),
		@AttributeOverride(column = @Column(name = "spirit_increase"), name = "increase"),
		@AttributeOverride(column = @Column(name = "spirit_multiplier"), name = "multiplier")
	})
	private StatValue spirit = new StatValue(1);
	
	@Embedded
	@AttributeOverrides({
		@AttributeOverride(column = @Column(name = "strength_base"), name = "base"),
		@AttributeOverride(column = @Column(name = "strength_additional"), name = "additional"),
		@AttributeOverride(column = @Column(name = "strength_increase"), name = "increase"),
		@AttributeOverride(column = @Column(name = "strength_multiplier"), name = "multiplier")
	})
	private StatValue strength = new StatValue(1);

	@Embedded
	@AttributeOverrides({
		@AttributeOverride(column = @Column(name = "dexterity_base"), name = "base"),
		@AttributeOverride(column = @Column(name = "dexterity_additional"), name = "additional"),
		@AttributeOverride(column = @Column(name = "dexterity_increase"), name = "increase"),
		@AttributeOverride(column = @Column(name = "dexterity_multiplier"), name = "multiplier")
	})
	private StatValue dexterity = new StatValue(1);
	
	@Embedded
	@AttributeOverrides({
		@AttributeOverride(column = @Column(name = "attunement_base"), name = "base"),
		@AttributeOverride(column = @Column(name = "attunement_additional"), name = "additional"),
		@AttributeOverride(column = @Column(name = "attunement_increase"), name = "increase"),
		@AttributeOverride(column = @Column(name = "attunement_multiplier"), name = "multiplier")
	})
	private StatValue attunement = new StatValue(1);
	
	@Embedded
	@AttributeOverrides({
		@AttributeOverride(column = @Column(name = "toughness_base"), name = "base"),
		@AttributeOverride(column = @Column(name = "toughness_additional"), name = "additional"),
		@AttributeOverride(column = @Column(name = "toughness_increase"), name = "increase"),
		@AttributeOverride(column = @Column(name = "toughness_multiplier"), name = "multiplier")
	})
	private StatValue toughness = new StatValue(0);
	
	@Embedded
	@AttributeOverrides({
		@AttributeOverride(column = @Column(name = "avoidance_base"), name = "base"),
		@AttributeOverride(column = @Column(name = "avoidance_additional"), name = "additional"),
		@AttributeOverride(column = @Column(name = "avoidance_increase"), name = "increase"),
		@AttributeOverride(column = @Column(name = "avoidance_multiplier"), name = "multiplier")
	})
	private StatValue avoidance = new StatValue(0);
	
	@Embedded
	@AttributeOverrides({
		@AttributeOverride(column = @Column(name = "resistance_base"), name = "base"),
		@AttributeOverride(column = @Column(name = "resistance_additional"), name = "additional"),
		@AttributeOverride(column = @Column(name = "resistance_increase"), name = "increase"),
		@AttributeOverride(column = @Column(name = "resistance_multiplier"), name = "multiplier")
	})
	private StatValue resistance = new StatValue(0);
	
	/********************************************************/
	/*                  Alternate Stats                     */
	/********************************************************/
	
	@Embedded
	@AttributeOverrides({
		@AttributeOverride(column = @Column(name = "gold_gain_base"), name = "base"),
		@AttributeOverride(column = @Column(name = "gold_gain_additional"), name = "additional"),
		@AttributeOverride(column = @Column(name = "gold_gain_increase"), name = "increase"),
		@AttributeOverride(column = @Column(name = "gold_gain_multiplier"), name = "multiplier")
	})
	private StatValue goldGain = new StatValue(1);
	
	@Embedded
	@AttributeOverrides({
		@AttributeOverride(column = @Column(name = "xp_gain_base"), name = "base"),
		@AttributeOverride(column = @Column(name = "xp_gain_additional"), name = "additional"),
		@AttributeOverride(column = @Column(name = "xp_gain_increase"), name = "increase"),
		@AttributeOverride(column = @Column(name = "xp_gain_multiplier"), name = "multiplier")
	})
	private StatValue xpGain = new StatValue(1);
	
	@Embedded
	@AttributeOverrides({
		@AttributeOverride(column = @Column(name = "attack_cooldown_base"), name = "base"),
		@AttributeOverride(column = @Column(name = "attack_cooldown_additional"), name = "additional"),
		@AttributeOverride(column = @Column(name = "attack_cooldown_increase"), name = "increase"),
		@AttributeOverride(column = @Column(name = "attack_cooldown_multiplier"), name = "multiplier")
	})
	private StatValue attackCooldown = new StatValue(2000);
	
	/********************************************************/
	/*                    Constructors                      */
	/********************************************************/
	
	public MythlandsCharacter() {
		maxHealth.setBase(9 + (stamina.getValue() * level));
		maxMana.setBase(9 + (spirit.getValue() * level));
		
		currentHealth = maxHealth.getValue();
		currentMana = maxHealth.getValue();
		
		inventory = new HashMap<>();
		statusEffects = new ArrayList<>();
	}
	
	/********************************************************/
	/*                       Methods                        */
	/********************************************************/
	
	public Integer getId() {
		return id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getXp() {
		return xp;
	}

	public void setXp(int xp) {
		this.xp = xp;
	}
	
	public boolean isDeceased() {
		return isDeceased;
	}
	
	public void setDeceased(boolean deceased) {
		this.isDeceased = deceased;
	}
	
	public MythlandsUser getOwner() {
		return owner;
	}
	
	public void setOwner(MythlandsUser owner) {
		this.owner = owner;
	}
	
	public int getInventoryCapacity() {
		return inventoryCapacity;
	}
	
	public EquippableItemInstance getArmorItem() {
		return armorItem;
	}
	
	public void setArmorItem(EquippableItemInstance armor) {
		if(armor != null) {
			if(!(armor.getTemplate() instanceof EquippableItemTemplate)) {
				throw new IllegalArgumentException("Armor equip item must be an equippable item type.");
			}
			
			var template = (EquippableItemTemplate) armor.getTemplate();
			if(template.getSlot() != EquippableItemSlot.ARMOR) {
				throw new IllegalArgumentException("Armor equip must have slot type ARMOR.");
			}
		}
		
		armorItem = armor;
	}
	
	public boolean hasArmorItem() {
		return armorItem != null;
	}
	
	public EquippableItemInstance getTrinketItem() {
		return trinketItem;
	}
	
	public void setTrinketItem(EquippableItemInstance trinket) {
		if(trinket != null) {
			if(!(trinket.getTemplate() instanceof EquippableItemTemplate)) {
				throw new IllegalArgumentException("Trinket equip item must be an equippable item type.");
			}
			
			var template = (EquippableItemTemplate) trinket.getTemplate();
			if(template.getSlot() != EquippableItemSlot.TRINKET) {
				throw new IllegalArgumentException("Trinket equip must have slot type TRINKET.");
			}
		}
		
		trinketItem = trinket;
	}
	
	public boolean hasTrinketItem() {
		return trinketItem != null;
	}
	
	public EquippableItemInstance getWeaponItem() {
		return weaponItem;
	}
	
	public void setWeaponItem(EquippableItemInstance weapon) {
		if(weapon != null) {
			if(!(weapon.getTemplate() instanceof EquippableItemTemplate)) {
				throw new IllegalArgumentException("Weapon equip item must be an equippable item type.");
			}
			
			var template = (EquippableItemTemplate) weapon.getTemplate();
			if(template.getSlot() != EquippableItemSlot.WEAPON) {
				throw new IllegalArgumentException("Weapon equip must have slot type WEAPON.");
			}
		}
		
		weaponItem = weapon;
	}
	
	public boolean hasWeaponItem() {
		return weaponItem != null;
	}
	
	public void setInventorySize(int size) {
		inventoryCapacity = size;
	}
	
	public Map<Integer, ItemInstance> getInventory() {
		return inventory;
	}
	
	public List<StatusEffectInstance> getStatusEffects() {
		return statusEffects;
	}
	
	public long getAttackReady() {
		return attackReady;
	}
	
	public void setAttackReady(long attackCooldown) {
		this.attackReady = attackCooldown;
	}
	
	public int getSkillPoints() {
		return skillPoints;
	}
	
	public void setSkillPoints(int skillPoints) {
		this.skillPoints = skillPoints;
	}
	
	public String getFullName() {
		return firstName + " " + lastName;
	}
	
	public double getMaxHealth() {
		return maxHealth.getValue();
	}
	
	public double getMaxHealthBase() {
		return maxHealth.getBase();
	}
	
	public void setMaxHealthBase(double base) {
		maxHealth.setBase(base);
	}
	
	public double getMaxHealthAdditional() {
		return maxHealth.getAdditional();
	}
	
	public void addMaxHealthAdditional(double amount) {
		maxHealth.addAdditional(amount);
	}
	
	public void removeMaxHealthAdditional(double amount) {
		maxHealth.removeAdditional(amount);
	}
	
	public double getMaxHealthIncrease() {
		return maxHealth.getIncrease();
	}
	
	public void addMaxHealthIncrease(double amount) {
		maxHealth.addIncrease(amount);
	}
	
	public void removeMaxHealthIncrease(double amount) {
		maxHealth.removeIncrease(amount);
	}
	
	public double getMaxHealthMultipliers() {
		return maxHealth.getMultiplier();
	}
	
	public void addMaxHealthMultiplier(double amount) {
		maxHealth.addMultiplier(amount);
	}
	
	public void removeMaxHealthMultiplier(double amount) {
		maxHealth.removeMultiplier(amount);
	}

	public double getCurrentHealth() {
		return currentHealth;
	}

	public void setCurrentHealth(double currentHealth) {
		this.currentHealth = currentHealth;
	}

	public void modifyCurrentHealth(double amount) {
		currentHealth += amount;
		// Trim the health to two decimal places to prevent accumulation
		// of floating point errors.
		currentHealth = (int) (currentHealth * 100) / 100.0;
		if(currentHealth < 0) {
			currentHealth = 0;
		}
		else if(currentHealth > maxHealth.getValue()) {
			currentHealth = maxHealth.getValue();
		}
	}

	public double getMaxMana() {
		return maxMana.getValue();
	}
	
	public double getMaxManaBase() {
		return maxMana.getBase();
	}
	
	public void setMaxManaBase(double base) {
		maxMana.setBase(base);
	}
	
	public double getMaxManaAdditional() {
		return maxMana.getAdditional();
	}
	
	public void addMaxManaAdditional(double amount) {
		maxMana.addAdditional(amount);
	}
	
	public void removeMaxManaAdditional(double amount) {
		maxMana.removeAdditional(amount);
	}
	
	public double getMaxManaIncrease() {
		return maxMana.getIncrease();
	}
	
	public void addMaxManaIncrease(double amount) {
		maxMana.addIncrease(amount);
	}
	
	public void removeMaxManaIncrease(double amount) {
		maxMana.removeIncrease(amount);
	}
	
	public double getMaxManaMultipliers() {
		return maxMana.getMultiplier();
	}
	
	public void addMaxManaMultiplier(double amount) {
		maxMana.addMultiplier(amount);
	}
	
	public void removeMaxManaMultiplier(double amount) {
		maxMana.removeMultiplier(amount);
	}
	
	public double getCurrentMana() {
		return currentMana;
	}

	public void setCurrentMana(double currentMana) {
		this.currentMana = currentMana;
	}
	
	public void modifyCurrentMana(double amount) {
		currentMana += amount;
		if(currentMana < 0) {
			currentMana = 0;
		}
		else if(currentMana > maxMana.getValue()) {
			currentMana = maxMana.getValue();
		}
	}
	
	public StatValue getStamina() {
		return stamina;
	}
	
	public StatValue getSpirit() {
		return spirit;
	}
	
	public StatValue getStrength() {
		return strength;
	}
	
	public StatValue getDexterity() {
		return dexterity;
	}

	public StatValue getAttunement() {
		return attunement;
	}

	public StatValue getToughness() {
		return toughness;
	}

	public StatValue getAvoidance() {
		return avoidance;
	}

	public StatValue getResistance() {
		return resistance;
	}
	
	public StatValue getGoldGain() {
		return goldGain;
	}
	
	public StatValue getXpGain() {
		return xpGain;
	}
	
	public StatValue getAttackCooldown() {
		return attackCooldown;
	}
	
	public void recalculateMaxHealth() {
		double currentPercent = currentHealth / maxHealth.getValue();
		maxHealth.setBase(9 + (stamina.getValue() * level));
		currentHealth = currentPercent * maxHealth.getValue();
	}
	
	public void recalculateMaxMana() {
		double currentPercent = currentMana / maxMana.getValue();
		maxMana.setBase(9 + (spirit.getValue() * level));
		currentMana = currentPercent * maxMana.getValue();
	}
	
	public int getFirstEmptyInventorySlot() {
		for(int i = 0; i < inventoryCapacity; i++) {
			if(!inventory.containsKey(i))
				return i;
		}
		return -1;
	}
	
	public boolean isInventoryFull() {
		return inventory.size() >= inventoryCapacity;
	}
	
	/**
	 * Returns the item currently equipped in the given slot
	 * @param slot
	 * @return item in slot; null if no item equipped
	 */
	public EquippableItemInstance getEquipped(EquippableItemSlot slot) {
		switch(slot) {
		
		case ARMOR:		return armorItem;
		case TRINKET:	return trinketItem;
		case WEAPON:	return weaponItem;
		
		default:		return null;
		
		}
	}

	/**
	 * Returns the StatValue object for a given StatType.
	 * @param stat
	 * @return the statvalue
	 */
	public StatValue getStat(StatType stat) {
		switch(stat) {
		
		case ATTACK_COOLDOWN:	return attackCooldown;
		case ATTUNEMENT:		return attunement;
		case AVOIDANCE:			return avoidance;
		case DEXTERITY:			return dexterity;
		case GOLD_GAIN:			return goldGain;
		case MAX_HEALTH:		return maxHealth;
		case MAX_MANA:			return maxMana;
		case RESISTANCE:		return resistance;
		case SPIRIT:			return spirit;
		case STAMINA:			return stamina;
		case STRENGTH:			return strength;
		case TOUGHNESS:			return toughness;
		case XP_GAIN:			return xpGain;
		
		default: return null;
		
		}
	}
	
}
