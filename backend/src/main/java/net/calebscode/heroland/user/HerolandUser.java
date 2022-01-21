package net.calebscode.heroland.user;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import net.calebscode.heroland.character.HerolandCharacter;

@Entity
@Table(name = "users")
public class HerolandUser {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;
	
	@Column(nullable = false, unique = true, length = 25)
	private String username;
	
	@Column(nullable = true, unique = true)
	private String email;
	
	@Column(nullable = false)
	private String passwordHash;
	
	@ElementCollection(fetch = FetchType.EAGER)
	private List<String> authorities = new ArrayList<>();
	
	@OneToMany(mappedBy = "owner")
	private List<HerolandCharacter> characters = new ArrayList<>();
	
	public Integer getId() {
		return id;
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getPasswordHash() {
		return passwordHash;
	}
	
	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}
	
	public List<String> getAuthorities() {
		return authorities;
	}
	
	public List<HerolandCharacter> getCharacters() {
		return characters;
	}
	
	public HerolandCharacter getActiveCharacter() {
		return characters.stream().filter(c -> !c.isDeceased()).findFirst().orElseGet(() -> null);
	}
	
}
