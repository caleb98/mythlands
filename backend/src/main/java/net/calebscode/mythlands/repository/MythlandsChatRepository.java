package net.calebscode.mythlands.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import net.calebscode.mythlands.entity.ChatMessage;

@Repository
public interface MythlandsChatRepository extends JpaRepository<ChatMessage, Integer> {
	
}
