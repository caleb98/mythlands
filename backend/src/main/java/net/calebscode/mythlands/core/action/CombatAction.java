package net.calebscode.mythlands.core.action;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.MapKeyColumn;

@Entity
public class CombatAction {
	
	@Id
	private String id;
	
	@ElementCollection
	@CollectionTable(name = "combat_action_data")
	@MapKeyColumn(name = "data_key")
	private Map<String, String> actionData;
	
	@Column(nullable = false)
	private String functionName;
	
	@SuppressWarnings("unused")
	private CombatAction() {
		this(null, null, new HashMap<>());
	}
	
	public CombatAction(String id, String functionName, Map<String, String> data) {
		this.id = id;
		this.functionName = functionName;
		actionData = data;
	}

	public Map<String, String> getActionData() {
		return actionData;
	}

	public void setActionData(Map<String, String> actionData) {
		this.actionData = actionData;
	}

	public String getFunctionName() {
		return functionName;
	}

	public void setFunctionName(String functionName) {
		this.functionName = functionName;
	}

	public String getId() {
		return id;
	}
	
}
