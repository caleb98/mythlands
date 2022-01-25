package net.calebscode.heroland.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import net.calebscode.heroland.entity.HerolandCharacter;

@Repository
public interface HerolandCharacterRepository extends JpaRepository<HerolandCharacter, Integer> {

	public HerolandCharacter findById(int id);
	
}
