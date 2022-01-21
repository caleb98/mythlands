package net.calebscode.heroland.character;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HerolandCharacterRepository extends JpaRepository<HerolandCharacter, Integer> {

	public HerolandCharacter findById(int id);
	
}
