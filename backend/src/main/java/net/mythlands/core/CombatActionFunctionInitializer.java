package net.mythlands.core;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.google.gson.JsonObject;

import net.mythlands.core.action.CombatContext;
import net.mythlands.event.CharacterStatsUpdateEvent;
import net.mythlands.exception.MythlandsServiceException;
import net.mythlands.service.MythlandsGameService;

@Component
public class CombatActionFunctionInitializer implements ApplicationRunner {

	@Autowired private ApplicationEventPublisher eventPublisher;
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
	private void healPlayer(CombatContext context, Map<String, String> data) {
		try {
			boolean updated = gameService.modifyHealth(context.hero.id, Double.parseDouble(data.get(("amount"))));
			if(updated) {
				eventPublisher.publishEvent(new CharacterStatsUpdateEvent(context.hero));
			}
		} catch (MythlandsServiceException e) {
			e.printStackTrace();
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
	private void restorePlayerMana(CombatContext context, Map<String, String> data) {
		try {
			boolean updated = gameService.modifyMana(context.hero.id, Double.parseDouble(data.get(("amount"))));
			if(updated) {
				eventPublisher.publishEvent(new CharacterStatsUpdateEvent(context.hero));
			}
		} catch (MythlandsServiceException e) {
			e.printStackTrace();
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
	private void addPlayerStats(CombatContext context, Map<String, String> data) {
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
			
			boolean updated = gameService.addStatModification(context.hero.id, stat, additional, increase, multiplier);
			if(updated) {
				eventPublisher.publishEvent(new CharacterStatsUpdateEvent(context.hero));
			}
		} catch (MythlandsServiceException e) {
			e.printStackTrace();
		}
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
	private void removePlayerStats(CombatContext context, Map<String, String> data) {
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
			
			boolean updated = gameService.removeStatModification(context.hero.owner.username, stat, additional, increase, multiplier);
			if(updated) {
				eventPublisher.publishEvent(new CharacterStatsUpdateEvent(context.hero));
			}
		} catch (MythlandsServiceException e) {
			e.printStackTrace();
		}
	}

}
