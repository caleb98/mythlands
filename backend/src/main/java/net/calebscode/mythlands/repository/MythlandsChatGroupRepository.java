package net.calebscode.mythlands.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import net.calebscode.mythlands.entity.ChatGroup;

@Repository
public interface MythlandsChatGroupRepository extends JpaRepository<ChatGroup, Integer> {
	
}
