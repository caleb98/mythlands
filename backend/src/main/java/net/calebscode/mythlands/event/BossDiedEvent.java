package net.calebscode.mythlands.event;

import net.calebscode.mythlands.core.Boss;
import net.calebscode.mythlands.dto.MythlandsCharacterDTO;

public class BossDiedEvent {

	public final Boss boss;
	public final MythlandsCharacterDTO killedBy;
	
	public BossDiedEvent(Boss boss, MythlandsCharacterDTO killedBy) {
		this.boss = boss;
		this.killedBy = killedBy;
	}
	
}
