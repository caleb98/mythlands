package net.calebscode.mythlands.entity;

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
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "users")
public class MythlandsUser {

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
	private List<MythlandsCharacter> characters = new ArrayList<>();
	
	@OneToOne
	private MythlandsCharacter activeCharacter;
	
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
	
	public List<MythlandsCharacter> getCharacters() {
		return characters;
	}
	
	public MythlandsCharacter getActiveCharacter() {
		return activeCharacter;
	}
	
	public void setActiveCharacter(MythlandsCharacter character) {
		this.activeCharacter = character;
	}
	
}
