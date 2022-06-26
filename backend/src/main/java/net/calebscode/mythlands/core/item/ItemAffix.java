package net.calebscode.mythlands.core.item;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import net.calebscode.mythlands.core.action.CombatAction;

@Entity
public class ItemAffix {

	@Id
	private String id;
	
	@ManyToOne private CombatAction onEquip;
	@ManyToOne private CombatAction onDequip;
	
	private ItemAffix() {}
	
	public ItemAffix(String id, CombatAction onEquip, CombatAction onDequip) {
		this.id = id;
		this.onEquip = onEquip;
		this.onDequip = onDequip;
	}
	
	public String getId() {
		return id;
	}
	
	public CombatAction getOnEquip() {
		return onEquip;
	}
	
	public void setOnEquip(CombatAction onEquip) {
		this.onEquip = onEquip;
	}
	
	public CombatAction getOnDequip() {
		return onDequip;
	}
	
	public void setOnDequip(CombatAction onDequip) {
		this.onDequip = onDequip;
	}
	
}
