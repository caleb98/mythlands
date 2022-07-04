package net.mythlands.dto;

import net.mythlands.core.Boss;

public class BossDTO {

	public final int id;
	public final String name;
	public final int maxHealth;
	public final int currentHealth;
	
	public BossDTO(Boss boss) {
		this.id = boss.getId();
		this.name = boss.getName();
		this.maxHealth = boss.getMaxHealth();
		this.currentHealth = boss.getCurrentHealth();
	}
	
}
