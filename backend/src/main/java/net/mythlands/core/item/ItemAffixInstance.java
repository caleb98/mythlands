package net.mythlands.core.item;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.Table;

@Entity
@Table(name = "item_affix_instance")
public class ItemAffixInstance {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@ManyToOne
	@JoinColumn(name = "affix_template_id")
	private ItemAffixTemplate template;
	
	@ElementCollection
	@CollectionTable(name = "item_affix_instance_data")
	@MapKeyColumn(name = "data_key")
	@Column(name = "data_value")
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
	
	public int getId() {
		return id;
	}
	
	public ItemAffixTemplate getTemplate() {
		return template;
	}
	
	public Map<String, String> getData() {
		return data;
	}
	
	public String getDescription() {
		String desc = template.getDescription();
		for(String key : data.keySet()) {
			desc = desc.replace("{" + key + "}", data.get(key));
		}
		return desc;
	}
	
}
