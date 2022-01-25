package net.calebscode.heroland.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import net.calebscode.heroland.entity.HerolandUser;

@Repository
public interface HerolandUserRepository extends JpaRepository<HerolandUser, Integer> {
	
	HerolandUser findByEmail(String email);
	HerolandUser findByUsername(String username);
	
}
