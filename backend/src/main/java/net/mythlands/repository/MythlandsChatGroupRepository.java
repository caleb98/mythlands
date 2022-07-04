package net.mythlands.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import net.mythlands.core.chat.ChatGroup;

@Repository
public interface MythlandsChatGroupRepository extends JpaRepository<ChatGroup, Integer> {
	
}
