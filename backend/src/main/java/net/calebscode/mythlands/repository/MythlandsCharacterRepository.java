package net.calebscode.mythlands.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import net.calebscode.mythlands.entity.MythlandsCharacter;

@Repository
public interface MythlandsCharacterRepository extends JpaRepository<MythlandsCharacter, Integer> {

	public MythlandsCharacter findById(int id);
	
}
