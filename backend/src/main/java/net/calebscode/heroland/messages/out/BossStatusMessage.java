package net.calebscode.heroland.messages.out;

import net.calebscode.heroland.core.Boss;

public class BossStatusMessage {

	public final int id;
	public final String name;
	public final int currentHealth;
	public final int maxHealth;
	
	public BossStatusMessage(Boss boss) {
		id = boss.getId();
		name = boss.getName();
		currentHealth = boss.getCurrentHealth();
		maxHealth = boss.getMaxHealth();
	}
	
	public BossStatusMessage(int id, String name, int currentHealth, int maxHealth) {
		this.id = id;
		this.name = name;
		this.currentHealth = currentHealth;
		this.maxHealth = maxHealth;
	}

}
