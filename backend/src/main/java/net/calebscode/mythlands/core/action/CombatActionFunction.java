package net.calebscode.mythlands.core.action;

import java.util.Map;

@FunctionalInterface
public interface CombatActionFunction {

	/**
	 * An action that occurs in the Mythlands combat context.
	 * @param context context to execute the action in
	 * @param data action-specific data
	 */
	public void execute(CombatContext context, Map<String, String> data);
	
}
