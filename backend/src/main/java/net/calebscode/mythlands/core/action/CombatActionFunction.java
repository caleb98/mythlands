package net.calebscode.mythlands.core.action;

import java.util.Map;

import net.calebscode.mythlands.core.Boss;

@FunctionalInterface
public interface CombatActionFunction {

	public void execute(int heroId, Boss boss, Map<String, String> data);
	
}
