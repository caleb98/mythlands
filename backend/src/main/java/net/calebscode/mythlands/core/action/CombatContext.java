package net.calebscode.mythlands.core.action;

import net.calebscode.mythlands.core.Boss;

public class CombatContext {

	/**
	 * The name of the user who is executing an action.
	 */
	public final String username;
	
	/**
	 * The name of the hero who is executing an action.
	 */
	public final int heroId;
	
	/**
	 * The currently active boss.
	 */
	public final Boss boss;

	public CombatContext(String username, int heroId, Boss boss) {
		this.username = username;
		this.heroId = heroId;
		this.boss = boss;
	}
	
}
