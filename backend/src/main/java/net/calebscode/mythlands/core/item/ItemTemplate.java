package net.calebscode.mythlands.core.item;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "item_type", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("Item")
@Table(name = "item_templates")
public class ItemTemplate {

	@Id
	private String id;
	
	@Column(nullable = false)
	private String name;
	
	@Column
	private String icon;
	
	@Column
	private String description;
	
	@Column(nullable = false)
	private int stackSize;
	
	@Enumerated(EnumType.STRING)
	private ItemRarity rarity;
	
	protected ItemTemplate() {}
	
	public ItemTemplate(String id, String name, String icon, String description, ItemRarity rarity, int stackSize) {
		this.id = id;
		this.name = name;
		this.icon = icon;
		this.description = description;
		this.rarity = rarity;
		this.stackSize = stackSize;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public int getStackSize() {
		return stackSize;
	}

	public void setStackSize(int stackSize) {
		this.stackSize = stackSize;
	}

	public ItemRarity getRarity() {
		return rarity;
	}

	public void setRarity(ItemRarity rarity) {
		this.rarity = rarity;
	}

	public String getId() {
		return id;
	}

	@Override
	public int hashCode() {
		return Objects.hash(icon, id, name, rarity, stackSize);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		
		// For match, only ids must equal. (If ids are equal but templates are not the same, we have much worse problems...)
		ItemTemplate other = (ItemTemplate) obj;
		return id == other.id;
	}
	
}
