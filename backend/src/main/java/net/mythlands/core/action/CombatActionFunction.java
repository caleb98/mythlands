package net.mythlands.core.action;

import java.util.Map;

@FunctionalInterface
public interface CombatActionFunction {

	public static final long UPDATE_FLAG_PLAYER_STATS = 1 << 0;
	public static final long UPDATE_FLAG_PLAYER_EFFECTS = 1 << 1;
	public static final long UPDATE_FLAG_PLAYER_INFO = 1 << 2;
	public static final long UPDATE_FLAG_PLAYER_INVENTORY = 1 << 3;
	public static final long UPDATE_FLAG_PLAYER_EQUIPMENT = 1 << 4;
	
	public static final long UPDATE_FLAG_BOSS_STATUS = 1 << 5;
	public static final long UPDATE_FLAG_BOSS_DIED = 1 << 6;
	
	
	/**
	 * An action that occurs in the Mythlands combat context.
	 * @param context context to execute the action in
	 * @param data action-specific data
	 * 
	 * @return flags value indicating any updates that were made and will need to be published as an event
	 */
	public long execute(CombatContext context, Map<String, String> data);
	
}
