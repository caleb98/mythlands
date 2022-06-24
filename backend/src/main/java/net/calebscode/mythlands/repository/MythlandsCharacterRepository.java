package net.calebscode.mythlands.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import net.calebscode.mythlands.entity.MythlandsCharacter;

@Repository
public interface MythlandsCharacterRepository extends JpaRepository<MythlandsCharacter, Integer> {
	
	public List<MythlandsCharacter> findByOrderByLevelDescXpDesc(Pageable pageable);
	
}
