package net.mythlands.response;

import net.mythlands.dto.BossDTO;

public class BossRO {

	public final int id;
	public final String name;
	public final int maxHealth;
	public final int currentHealth;
	
	public BossRO(BossDTO boss) {
		id = boss.id;
		name = boss.name;
		maxHealth = boss.maxHealth;
		currentHealth = boss.maxHealth;
	}
	
}
