package net.calebscode.mythlands.core;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import net.calebscode.mythlands.core.action.CombatContext;
import net.calebscode.mythlands.exception.MythlandsServiceException;
import net.calebscode.mythlands.messages.out.CharacterUpdateMessage;
import net.calebscode.mythlands.service.MythlandsGameService;

@Component
public class CombatActionFunctionInitializer implements ApplicationRunner {

	@Autowired private MythlandsGameService gameService;
	@Autowired private SimpMessagingTemplate messenger;
	@Autowired private Gson gson;
	
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
	private void healPlayer(CombatContext context, Map<String, String> data) {
		try {
			JsonObject update = gameService.gainHealth(context.heroId, Double.parseDouble(data.get(("amount"))));
			if(update.size() > 0) {
				messenger.convertAndSendToUser(context.username, "/local/character", 
						gson.toJson(new CharacterUpdateMessage(update)));
			}
		} catch (MythlandsServiceException e) {
			e.printStackTrace();
		}
	}
	
	private void restorePlayerMana(CombatContext context, Map<String, String> data) {
		try {
			JsonObject update = gameService.gainMana(context.heroId, Double.parseDouble(data.get(("amount"))));
			if(update.size() > 0) {
				messenger.convertAndSendToUser(context.username, "/local/character", 
						gson.toJson(new CharacterUpdateMessage(update)));
			}
		} catch (MythlandsServiceException e) {
			e.printStackTrace();
		}
	}

}
