package net.mythlands.core.action;

import net.mythlands.core.Boss;
import net.mythlands.dto.MythlandsCharacterDTO;

public class CombatContext {
	
	/**
	 * The name of the hero who is executing an action.
	 */
	public final MythlandsCharacterDTO hero;
	
	/**
	 * The currently active boss.
	 */
	public final Boss boss;

	public CombatContext(MythlandsCharacterDTO hero, Boss boss) {
		this.hero = hero;
		this.boss = boss;
	}
	
}
