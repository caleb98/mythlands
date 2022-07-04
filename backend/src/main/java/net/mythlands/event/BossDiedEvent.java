package net.mythlands.event;

import net.mythlands.core.Boss;
import net.mythlands.dto.MythlandsCharacterDTO;

public class BossDiedEvent {

	public final Boss boss;
	public final MythlandsCharacterDTO killedBy;
	
	public BossDiedEvent(Boss boss, MythlandsCharacterDTO killedBy) {
		this.boss = boss;
		this.killedBy = killedBy;
	}
	
}
