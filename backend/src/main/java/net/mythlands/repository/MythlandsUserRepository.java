package net.mythlands.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import net.mythlands.core.MythlandsUser;

@Repository
public interface MythlandsUserRepository extends JpaRepository<MythlandsUser, Integer> {
	
	MythlandsUser findByEmail(String email);
	MythlandsUser findByUsername(String username);
	
}
