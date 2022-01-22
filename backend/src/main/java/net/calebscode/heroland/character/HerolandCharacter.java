package net.calebscode.heroland.character;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import net.calebscode.heroland.user.HerolandUser;

@Entity
@Table(name="characters")
public class HerolandCharacter {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;
	
	@Column(nullable = false) private String firstName;
	@Column(nullable = false) private String lastName;
	@Column(nullable = false) private int level = 1;
	@Column(nullable = false) private int xp = 0;
	@Column(nullable = false) private boolean isDeceased = false;
	
	@Column(nullable = false) private int maxHealth = 10;
	@Column(nullable = false) private int currentHealth = 10;
	@Column(nullable = false) private int maxMana = 10;
	@Column(nullable = false) private int currentMana = 10;
	
	@Column(nullable = false) private int strength = 1;
	@Column(nullable = false) private int dexterity = 1;
	@Column(nullable = false) private int attunement = 1;
	@Column(nullable = false) private int toughness = 0;
	@Column(nullable = false) private int avoidance = 0;
	@Column(nullable = false) private int resistance = 0;
	
	@Column(nullable = false) private long attackReady = 0;
	@Column(nullable = false) private int skillPoints = 0;
	
	@ManyToOne
	@JoinColumn(name = "users_id")
	private HerolandUser owner;
	
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
	
	public HerolandUser getOwner() {
		return owner;
	}
	
	public void setOwner(HerolandUser owner) {
		this.owner = owner;
	}

	public int getMaxHealth() {
		return maxHealth;
	}

	public void setMaxHealth(int maxHealth) {
		this.maxHealth = maxHealth;
	}

	public int getCurrentHealth() {
		return currentHealth;
	}

	public void setCurrentHealth(int currentHealth) {
		this.currentHealth = currentHealth;
	}

	public int getMaxMana() {
		return maxMana;
	}

	public void setMaxMana(int maxMana) {
		this.maxMana = maxMana;
	}

	public int getCurrentMana() {
		return currentMana;
	}

	public void setCurrentMana(int currentMana) {
		this.currentMana = currentMana;
	}

	public int getStrength() {
		return strength;
	}

	public void setStrength(int strength) {
		this.strength = strength;
	}
	
	public void modifyStrength(int amount) {
		this.strength += amount;
	}

	public int getDexterity() {
		return dexterity;
	}

	public void setDexterity(int dexterity) {
		this.dexterity = dexterity;
	}
	
	public void modifyDexterity(int amount) {
		this.dexterity += amount;
	}

	public int getAttunement() {
		return attunement;
	}

	public void setAttunement(int attunement) {
		this.attunement = attunement;
	}
	
	public void modifyAttunement(int amount) {
		this.attunement += amount;
	}

	public int getToughness() {
		return toughness;
	}

	public void setToughness(int toughness) {
		this.toughness = toughness;
	}
	
	public void modifyToughness(int amount) {
		this.toughness += amount;
	}

	public int getAvoidance() {
		return avoidance;
	}

	public void setAvoidance(int avoidance) {
		this.avoidance = avoidance;
	}
	
	public void modifyAvoidance(int amount) {
		this.avoidance += amount;
	}

	public int getResistance() {
		return resistance;
	}

	public void setResistance(int resistance) {
		this.resistance = resistance;
	}
	
	public void modifyResistance(int amount) {
		this.resistance += amount;
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
	
}
