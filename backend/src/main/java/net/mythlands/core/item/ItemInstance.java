package net.mythlands.core.item;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.DiscriminatorFormula;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorFormula("(select item_template.item_type from item_template where item_template.id = item_template)")
@Table(name = "item_instance")
public abstract class ItemInstance {

	@Id
	@GeneratedValue(generator = "uuid2")
	@GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
	@Column(name = "id", updatable = false, columnDefinition = "BINARY(16)")
	private UUID id;
	
	@ManyToOne
	@JoinColumn(name = "item_template")
	private ItemTemplate template;
	
	@Column
	private int count;

	protected ItemInstance() {}
	
	public ItemInstance(ItemTemplate template) {
		this(template, 1);
	}
	
	public ItemInstance(ItemTemplate template, int count) {
		if(count > template.getStackSize()) {
			count = template.getStackSize();
		}
		
		this.template = template;
		this.count = count;
	}
	
	public UUID getId() {
		return id;
	}

	public int getCount() {
		return count;
	}
	
	public void setCount(int count) {
		if(count > template.getStackSize()) {
			count = template.getStackSize();
		}
		this.count = count;
	}
	
	public void modifyCount(int amount) {
		count += amount;
		if(count > template.getStackSize()) { 
			count = template.getStackSize();
		}
		else if(count < 0) {
			count = 0;
		}
	}
	
	public ItemTemplate getTemplate() {
		return template;
	}
	
	public String getName() {
		return template.getName();
	}
	
	public String getIcon() {
		return template.getIcon();
	}
	
	public String getDescription() {
		return template.getDescription();
	}
	
	public ItemRarity getItemRarity() {
		return template.getRarity();
	}
	
	/**
	 * Tests whether or not another item instance can be stacked with this one.
	 * @param other
	 * @return
	 */
	public boolean isStackable(ItemInstance other) {
		// Items with stack size of 1 are never stackable.
		if(template.getStackSize() == 1) {
			return false;
		}
		
		// Items must have the same template to be considered stackable.
		if(!template.equals(other.template)) {
			return false;
		}
		
		// Items must have same item data to be considered stackable.
//		if(!itemData.equals(other.itemData)) {
//			return false;
//		}
		
		// All checks passed. Is stackable.
		return true;
	}
	
}
