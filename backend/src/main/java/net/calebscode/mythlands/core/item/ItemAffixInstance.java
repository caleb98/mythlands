package net.calebscode.mythlands.core.item;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "affix_instances")
public class ItemAffixInstance {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@ManyToOne
	private ItemAffixTemplate template;
	
	@ElementCollection
	private Map<String, String> data;
	
	@SuppressWarnings("unused")
	private ItemAffixInstance() {}
	
	public ItemAffixInstance(ItemAffixTemplate template) {
		this(template, new HashMap<>());
	}
	
	public ItemAffixInstance(ItemAffixTemplate template, Map<String, String> data) {
		this.template = template;
		this.data = data;
	}
	
	public ItemAffixTemplate getTemplate() {
		return template;
	}
	
	public Map<String, String> getData() {
		return data;
	}
	
}
