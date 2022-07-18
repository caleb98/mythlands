package net.mythlands.event;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import net.mythlands.dto.BossDTO;
import net.mythlands.messages.out.BossDiedMessage;
import net.mythlands.response.CharacterEffectsRO;
import net.mythlands.response.CharacterEquipmentRO;
import net.mythlands.response.CharacterInfoRO;
import net.mythlands.response.CharacterStatsRO;
import net.mythlands.response.ItemRO;
import net.mythlands.response.TimestampedRO;

@Component
public class MythlandsEventListener {
	
	@Autowired private SimpMessagingTemplate messenger;

	@Async
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
	public void bossDiedListener(BossDiedEvent event) {
		messenger.convertAndSend(
				"/gloabl/boss.died", 
				new BossDiedMessage(
					event.boss.getName(),
					event.killedBy.firstName + " " + event.killedBy.lastName
				)
		);
	}
	
	@Async
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
	public void bossUpdateListener(BossUpdateEvent event) {
		messenger.convertAndSend(
				"/global/boss.status",
				new BossDTO(event.boss)
		);
	}
	
	@Async
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
	public void characterInfoUpdateListener(CharacterInfoUpdateEvent event) {
		var info = new CharacterInfoRO(event.character);
		var data = new TimestampedRO<>(info);
		messenger.convertAndSendToUser(
				event.character.owner,
				"/local/character.info",
				data
		);
	}
	
	@Async
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
	public void characterStatsUpdateListener(CharacterStatsUpdateEvent event) {
		CharacterStatsRO data = new CharacterStatsRO(event.character);
		TimestampedRO<CharacterStatsRO> response = new TimestampedRO<>(data);
		messenger.convertAndSendToUser(
				event.character.owner,
				"/local/character.stats",
				response
		);
	}	
	
	@Async
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
	public void statusEffectUpdateListener(CharacterEffectsUpdateEvent event) {
		CharacterEffectsRO data = new CharacterEffectsRO(event.character);
		TimestampedRO<CharacterEffectsRO> response = new TimestampedRO<>(data);
		messenger.convertAndSendToUser(
				event.character.owner, 
				"/local/character.effects",
				response
		);
	}
	
	@Async
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
	public void inventoryUpdateListener(InventoryUpdateEvent event) {
		var data = new HashMap<Integer, ItemRO>();
		for(int slot : event.updates.keySet()) {
			data.put(slot, ItemRO.getItemRO(event.updates.get(slot)));
		}
		messenger.convertAndSendToUser(
				event.character.owner, 
				"/local/character.inventory", 
				data
		);	
	}
	
	@Async
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
	public void equipmentUpdateListener(CharacterEquipmentUpdateEvent event) {
		var data = new CharacterEquipmentRO(event.character);
		var response = new TimestampedRO<>(data);
		messenger.convertAndSendToUser(
				event.character.owner, 
				"/local/character.equipment", 
				response
		);
	}
	
}
