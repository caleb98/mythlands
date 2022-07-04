package net.mythlands.core.item;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "affix_templates")
public class ItemAffixTemplate {

	@Id
	private String id;
	
	@Column private String onEquipFunction;
	@Column private String onDequipFunction;
	@Column private String description;
	
	@SuppressWarnings("unused")
	private ItemAffixTemplate() {}
	
	public ItemAffixTemplate(String id, String onEquipFunction, String onDequipFunction, String description) {
		this.id = id;
		this.onEquipFunction = onEquipFunction;
		this.onDequipFunction = onDequipFunction;
		this.description = description;
	}
	
	public String getId() {
		return id;
	}
	
	public String getOnEquip() {
		return onEquipFunction;
	}
	
	public void setOnEquip(String onEquipFunction) {
		this.onEquipFunction = onEquipFunction;
	}
	
	public String getOnDequip() {
		return onDequipFunction;
	}
	
	public void setOnDequip(String onDequipFunction) {
		this.onDequipFunction = onDequipFunction;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
}
