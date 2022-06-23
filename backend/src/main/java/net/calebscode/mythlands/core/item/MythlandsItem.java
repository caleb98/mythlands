package net.calebscode.mythlands.core.item;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import net.calebscode.mythlands.entity.MythlandsCharacter;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Table(name = "items")
public class MythlandsItem {

	@Id
	@GeneratedValue(generator = "uuid2")
	@GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
	@Column(name = "id", updatable = false, columnDefinition = "BINARY(16)")
	private UUID id;
	
	@Column(nullable = false) 
	private String name;
	
	@Enumerated(EnumType.STRING)
	private ItemRarity rarity;

	@ManyToOne
	@JoinColumn(name = "character_owner_id")
	private MythlandsCharacter characterOwner;

	public MythlandsItem() {
		this(null, null, null);
	}
	
	public MythlandsItem(String name, ItemRarity rarity) {
		this(name, rarity, null);
	}
	
	public MythlandsItem(String name, ItemRarity rarity, MythlandsCharacter characterOwner) {
		this.name = name;
		this.rarity = rarity;
		this.characterOwner = characterOwner;
	}
	
	public UUID getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ItemRarity getRarity() {
		return rarity;
	}

	public void setRarity(ItemRarity rarity) {
		this.rarity = rarity;
	}

	public MythlandsCharacter getCharacterOwner() {
		return characterOwner;
	}

	public void setCharacterOwner(MythlandsCharacter characterOwner) {
		this.characterOwner = characterOwner;
	}
	
}
