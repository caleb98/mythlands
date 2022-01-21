package net.calebscode.heroland.messages;

public class BossStatusMessage {

	private String name;
	private int currentHealth;
	private int maxHealth;
	
	public BossStatusMessage(String name, int currentHealth, int maxHealth) {
		this.name = name;
		this.currentHealth = currentHealth;
		this.maxHealth = maxHealth;
	}
	
	public String getName() {
		return name;
	}
	
	public int getCurrentHealth() {
		return currentHealth;
	}
	
	public int getMaxHealth() {
		return maxHealth;
	}
	
}
