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
import net.mythlands.messages.out.CharacterUpdateMessage;
import net.mythlands.messages.out.CooldownMessage;

@Component
public class DataUpdateListener {
	
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
	public void characterUpdateListener(CharacterUpdateEvent event) {
		messenger.convertAndSendToUser(
				event.username,
				"/local/character",
				gson.toJson(new CharacterUpdateMessage(event.updates))
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
				"/local/cooldown",
				new CooldownMessage(event.cooldown)
		);
	}	
	
}
