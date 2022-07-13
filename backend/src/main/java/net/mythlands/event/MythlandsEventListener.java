package net.mythlands.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.google.gson.Gson;

import net.mythlands.dto.BossDTO;
import net.mythlands.messages.out.BossDiedMessage;
import net.mythlands.messages.out.CooldownMessage;
import net.mythlands.response.CharacterEffectsRO;
import net.mythlands.response.CharacterStatsRO;
import net.mythlands.response.TimestampedRO;
import net.mythlands.service.MythlandsGameService;

@Component
public class MythlandsEventListener {
	
	@Autowired private MythlandsGameService gameService;
	@Autowired private SimpMessagingTemplate messenger;
	@Autowired private Gson gson;

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
	public void cooldownUpdateListener(CooldownUpdateEvent event) {
		messenger.convertAndSendToUser(
				event.username,
				"/local/character.cooldown",
				new CooldownMessage(event.cooldown)
		);
	}
	
	@Async
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
	public void characterStatsUpdateListener(CharacterStatsUpdateEvent event) {
		CharacterStatsRO data = new CharacterStatsRO(event.character);
		TimestampedRO<CharacterStatsRO> response = new TimestampedRO<>(data);
		messenger.convertAndSendToUser(
				event.character.owner.username,
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
				event.character.owner.username, 
				"/local/character.effects",
				response
		);
	}
	
}
