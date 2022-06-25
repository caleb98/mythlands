package net.calebscode.mythlands.core;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import net.calebscode.mythlands.exception.MythlandsServiceException;
import net.calebscode.mythlands.service.MythlandsGameService;

@Component
public class CombatActionFunctionInitializer implements ApplicationRunner {

	@Autowired private MythlandsGameService gameService;
	
	@Override
	public void run(ApplicationArguments args) throws Exception {
		gameService.registerCombatActionFunction("HealPlayer", this::healPlayer);
		gameService.registerCombatActionFunction("RestorePlayerMana", this::restorePlayerMana);
	}
	
	/**
	 * Restores health to the player equal to the "amount" value in the data.
	 * 
	 * @param heroId
	 * @param boss
	 * @param data
	 */
	private void healPlayer(int heroId, Boss boss, Map<String, String> data) {
		try {
			gameService.gainHealth(heroId, Double.parseDouble(data.get(("amount"))));
		} catch (MythlandsServiceException e) {
			e.printStackTrace();
		}
	}
	
	private void restorePlayerMana(int heroId, Boss boss, Map<String, String> data) {
		try {
			gameService.gainMana(heroId, Double.parseDouble(data.get(("amount"))));
		} catch (MythlandsServiceException e) {
			e.printStackTrace();
		}
	}

}
