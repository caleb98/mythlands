package net.mythlands.core.action;

import net.mythlands.core.Boss;
import net.mythlands.core.MythlandsCharacter;

public class CombatContext {
	
	/**
	 * The name of the hero who is executing an action.
	 */
	public final MythlandsCharacter character;
	
	/**
	 * The currently active boss.
	 */
	public final Boss boss;

	public CombatContext(MythlandsCharacter character, Boss boss) {
		this.character = character;
		this.boss = boss;
	}
	
}
