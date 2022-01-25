package net.calebscode.mythlands.core;

public class Boss {

	private static int idCounter = 0;
	
	private int id;
	private String name;
	private int maxHealth;
	private int currentHealth;
	
	public Boss(String name, int maxHealth) {
		id = idCounter++;
		this.name = name;
		this.maxHealth = maxHealth;
		this.currentHealth = maxHealth;
	}

	public int getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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
	
	public void damage(int amount) {
		this.currentHealth -= amount;
	}
	
}
