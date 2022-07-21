package net.mythlands.core;

import java.util.LinkedList;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;

import net.mythlands.core.action.CombatActionFunction;
import net.mythlands.core.action.CombatContext;
import net.mythlands.dto.MythlandsCharacterDTO;
import net.mythlands.event.BossDiedEvent;
import net.mythlands.event.BossUpdateEvent;
import net.mythlands.event.CharacterEffectsUpdateEvent;
import net.mythlands.event.CharacterInfoUpdateEvent;
import net.mythlands.event.CharacterStatsUpdateEvent;
import net.mythlands.exception.MythlandsServiceException;
import net.mythlands.service.MythlandsGameService;

public class CombatFunctionQueue {
	
	private Logger logger = LoggerFactory.getLogger(CombatFunctionQueue.class);
	private LinkedList<CombatFunctionEntry> entries = new LinkedList<>();
	
	public void add(CombatActionFunction function, Map<String, String> data) {
		entries.add(new CombatFunctionEntry(function, data));
	}
	
	public void run(MythlandsGameService service, CombatContext context, ApplicationEventPublisher eventPublisher) {
		long updateFlags = 0;
		while(entries.size() > 0) {
			var entry = entries.removeFirst();
			updateFlags |= entry.function.execute(context, entry.data);
		}
		
		MythlandsCharacterDTO character = new MythlandsCharacterDTO(context.character);
		
		if((updateFlags & CombatActionFunction.UPDATE_FLAG_PLAYER_STATS) != 0) {
			eventPublisher.publishEvent(new CharacterStatsUpdateEvent(character));
		}
		
		if((updateFlags & CombatActionFunction.UPDATE_FLAG_PLAYER_EFFECTS) != 0) {
			eventPublisher.publishEvent(new CharacterEffectsUpdateEvent(character));
		}
		
		if((updateFlags & CombatActionFunction.UPDATE_FLAG_PLAYER_INFO) != 0) {
			eventPublisher.publishEvent(new CharacterInfoUpdateEvent(character));
		}
		
		if((updateFlags & CombatActionFunction.UPDATE_FLAG_PLAYER_INVENTORY) != 0) {
			logger.warn("Player inventory update flag not yet implemented!");
		}
		
		if((updateFlags & CombatActionFunction.UPDATE_FLAG_BOSS_STATUS) != 0) {
			eventPublisher.publishEvent(new BossUpdateEvent(context.boss));
		}
		
		if((updateFlags & CombatActionFunction.UPDATE_FLAG_BOSS_DIED) != 0) {
			// TODO: can we be sure this character is the one who got the kill?
			eventPublisher.publishEvent(new BossDiedEvent(context.boss, character));
		}
	}
	
	private class CombatFunctionEntry {
		
		CombatActionFunction function;
		Map<String, String> data;
		
		CombatFunctionEntry(CombatActionFunction function, Map<String, String> data) {
			this.function = function;
			this.data = data;
		}
		
	}
	
}
