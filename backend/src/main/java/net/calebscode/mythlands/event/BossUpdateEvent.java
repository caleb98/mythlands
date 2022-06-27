package net.calebscode.mythlands.event;

import net.calebscode.mythlands.core.Boss;

public class BossUpdateEvent {

	public final Boss boss;
	
	public BossUpdateEvent(Boss boss) {
		this.boss = boss;
	}
	
}
