package net.calebscode.mythlands.entity;

import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import net.calebscode.mythlands.core.StatValue;
import net.calebscode.mythlands.core.item.ItemInstance;

@Entity
@Table(name = "characters")
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
	
	@OneToMany(cascade = CascadeType.ALL)
	@JoinTable(inverseJoinColumns = @JoinColumn(name = "item_instance_id"))
	private List<ItemInstance> inventory;
	
	@Column(nullable = false) 
	private int inventoryCapacity = 100;
	
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
	
	public void setInventorySize(int size) {
		inventoryCapacity = size;
	}
	
	public List<ItemInstance> getInventory() {
		return inventory;
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
	
	public StatValue getGoldGain() {
		return goldGain;
	}
	
	public StatValue getXpGain() {
		return xpGain;
	}
	
	public StatValue getAttackCooldown() {
		return attackCooldown;
	}
	
}
