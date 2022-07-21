package net.mythlands.core;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import net.mythlands.core.action.CombatActionFunction;
import net.mythlands.core.action.CombatContext;
import net.mythlands.exception.MythlandsServiceException;
import net.mythlands.service.MythlandsGameService;

@Component
public class CombatActionFunctionInitializer implements ApplicationRunner {

	@Autowired private MythlandsGameService gameService;
	
	@Override
	public void run(ApplicationArguments args) throws Exception {
		gameService.registerCombatActionFunction("HealPlayer", this::healPlayer);
		gameService.registerCombatActionFunction("RestorePlayerMana", this::restorePlayerMana);
		gameService.registerCombatActionFunction("ApplyStatMod", this::addPlayerStats);
		gameService.registerCombatActionFunction("RemoveStatMod", this::removePlayerStats);
	}
	
	/**
	 * Restores health to the player equal to the "amount" value in the data.<br>
	 *
	 * <b>Data</b>
	 * <ul>
	 * 		<li>amount
	 * </ul>
	 *
	 * @param heroId
	 * @param boss
	 * @param data
	 */
	private long healPlayer(CombatContext context, Map<String, String> data) {
		double oldHealth = context.character.getCurrentHealth();
		context.character.modifyCurrentHealth(Double.parseDouble(data.get("amount")));
		double newHealth = context.character.getCurrentHealth();
		
		// Check whether an update flag needs to be set
		if(oldHealth != newHealth) {
			return CombatActionFunction.UPDATE_FLAG_PLAYER_STATS;
		}
		else {
			return 0;
		}
	}
	
	/**
	 * Restores mana to the player equal to the "amount" value in the data.<br>
	 * 
	 * <b>Data</b>
	 * <ul>
	 * 		<li>amount
	 * </ul>
	 * 
	 * @param context
	 * @param data
	 */
	private long restorePlayerMana(CombatContext context, Map<String, String> data) {
		double oldMana = context.character.getCurrentMana();
		context.character.modifyCurrentMana(Double.parseDouble(data.get("amount")));
		double newMana = context.character.getCurrentMana();
		
		// Check whether an update flag needs to be set
		if(oldMana != newMana) {
			return CombatActionFunction.UPDATE_FLAG_PLAYER_STATS;
		}
		else {
			return 0;
		}
	}
	
	/**
	 * Adds a modification to a stat.<br>
	 * 
	 * <b>Data</b>
	 * <ul>
	 * 		<li>stat</li>
	 * 		<li>additional (<i>optional</i>)
	 * 		<li>increase (<i>optional</i>)
	 * 		<li>multiplier (<i>optional</i>)
	 * </ul>
	 * 
	 * @param context
	 * @param data
	 */
	private long addPlayerStats(CombatContext context, Map<String, String> data) {
		try {
			StatType stat = StatType.valueOf(data.get("stat"));
			double additional = 0;
			double increase = 0;
			double multiplier = 0;
			
			if(data.containsKey("additional")) 
				additional = Double.parseDouble(data.get("additional"));
			
			if(data.containsKey("increase"))
				increase = Double.parseDouble(data.get("increase"));
			
			if(data.containsKey("multiplier"))
				multiplier = Double.parseDouble(data.get("multiplier"));
			
			boolean updated = gameService.addStatModification(context.character.getId(), stat, additional, increase, multiplier);
			if(updated) {
				return CombatActionFunction.UPDATE_FLAG_PLAYER_STATS;
			}
		} catch (MythlandsServiceException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	/**
	 * Removes a modification to a stat.<br>
	 * 
	 * <b>Data</b>
	 * <ul>
	 * 		<li>stat</li>
	 * 		<li>additional (<i>optional</i>)
	 * 		<li>increase (<i>optional</i>)
	 * 		<li>multiplier (<i>optional</i>)
	 * </ul>
	 * 
	 * @param context
	 * @param data
	 */
	private long removePlayerStats(CombatContext context, Map<String, String> data) {
		try {
			StatType stat = StatType.valueOf(data.get("stat"));
			double additional = 0;
			double increase = 0;
			double multiplier = 0;
			
			if(data.containsKey("additional")) 
				additional = Double.parseDouble(data.get("additional"));
			
			if(data.containsKey("increase"))
				increase = Double.parseDouble(data.get("increase"));
			
			if(data.containsKey("multiplier"))
				multiplier = Double.parseDouble(data.get("multiplier"));
			
			boolean updated = gameService.removeStatModification(context.character.getId(), stat, additional, increase, multiplier);
			if(updated) {
				return CombatActionFunction.UPDATE_FLAG_PLAYER_STATS;
			}
		} catch (MythlandsServiceException e) {
			e.printStackTrace();
		}
		return 0;
	}

}
