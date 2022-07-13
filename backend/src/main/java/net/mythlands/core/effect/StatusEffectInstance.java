package net.mythlands.core.effect;

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

import net.mythlands.core.MythlandsCharacter;

@Entity
@Table(name = "status_effect_instance")
public class StatusEffectInstance {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@ManyToOne
	@JoinColumn(name = "effect_template_id")
	private StatusEffectTemplate template;
	
	@ElementCollection
	@CollectionTable(name = "status_effect_instance_data")
	@MapKeyColumn(name = "data_key")
	@Column(name = "data_value")
	private Map<String, String> data;
	
	@ManyToOne
	@JoinColumn(name = "active_character_id")
	private MythlandsCharacter character;
	
	@Column private long startTime;
	@Column private long finishTime;
	
	@SuppressWarnings("unused")
	private StatusEffectInstance() {}
	
	public StatusEffectInstance(StatusEffectTemplate template, long startTime, long finishTime) {
		this(template, new HashMap<>(), startTime, finishTime);
	}
	
	public StatusEffectInstance(StatusEffectTemplate template, Map<String, String> data, long startTime, long finishTime) {
		this.template = template;
		this.data = data;
		this.startTime = startTime;
		this.finishTime = finishTime;
	}
	
	public int getId() {
		return id;
	}
	
	public StatusEffectTemplate getTemplate() {
		return template;
	}
	
	public MythlandsCharacter getCharacter() {
		return character;
	}
	
	public void setCharacter(MythlandsCharacter hero) {
		this.character = hero;
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
	
	public long getStartTime() {
		return startTime;
	}
	
	public long getFinishTime() {
		return finishTime;
	}
	
	public void setFinishTime(long finishTime) {
		this.finishTime = finishTime;
	}
	
}
