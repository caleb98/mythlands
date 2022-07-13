package net.mythlands.core.effect;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "status_effect_template")
public class StatusEffectTemplate {

	@Id
	private String id;
	
	@Column private String name;
	@Column private String onApplyFunction;
	@Column private String onRemoveFunction;
	@Column private String description;
	@Column private String icon;
	@Column private boolean isBuff;
	@Column private long duration;
	
	@SuppressWarnings("unused")
	private StatusEffectTemplate() {}
	
	public StatusEffectTemplate(String id, String name, String onApplyFunction, String onRemoveFunction, String description, String icon, boolean isBuff, long duration) {
		this.id = id;
		this.name = name;
		this.onApplyFunction = onApplyFunction;
		this.onRemoveFunction = onRemoveFunction;
		this.description = description;
		this.icon = icon;
		this.isBuff = isBuff;
		this.duration = duration;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getOnApplyFunction() {
		return onApplyFunction;
	}

	public void setOnApplyFunction(String onApplyFunction) {
		this.onApplyFunction = onApplyFunction;
	}

	public String getOnRemoveFunction() {
		return onRemoveFunction;
	}

	public void setOnRemoveFunction(String onRemoveFunction) {
		this.onRemoveFunction = onRemoveFunction;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getId() {
		return id;
	}

	public String getIcon() {
		return icon;
	}
	
	public void setIcon(String icon) {
		this.icon = icon;
	}
	
	public boolean isBuff() {
		return isBuff;
	}
	
	public boolean isDebuff() {
		return !isBuff;
	}
	
	public void setIsBuff(boolean isBuff) {
		this.isBuff = isBuff;
	}
	
	public long getDuration() {
		return duration;
	}
	
	public void setDuration(long duration) {
		this.duration = duration;
	}
	
}
