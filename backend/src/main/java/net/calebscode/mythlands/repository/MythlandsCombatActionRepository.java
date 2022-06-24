package net.calebscode.mythlands.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import net.calebscode.mythlands.core.action.CombatAction;

@Repository
public interface MythlandsCombatActionRepository extends JpaRepository<CombatAction, String> {
	
}
