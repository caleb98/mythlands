package net.mythlands.event;

import net.mythlands.core.Boss;

public class BossUpdateEvent {

	public final Boss boss;
	
	public BossUpdateEvent(Boss boss) {
		this.boss = boss;
	}
	
}
