package net.calebscode.heroland.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<HerolandUser, Integer> {
	
	//@Query("SELECT u FROM HerolandUser u WHERE u.email = ?1")
	HerolandUser findByEmail(String email);
	
	//@Query("SELECT u FROM HerolandUser u WHERE u.username = ?1")
	HerolandUser findByUsername(String username);
	
}
