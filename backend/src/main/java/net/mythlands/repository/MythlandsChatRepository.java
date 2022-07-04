package net.mythlands.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import net.mythlands.core.chat.ChatMessage;

@Repository
public interface MythlandsChatRepository extends JpaRepository<ChatMessage, Integer> {
	
}
