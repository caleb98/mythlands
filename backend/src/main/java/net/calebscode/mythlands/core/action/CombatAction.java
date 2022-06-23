package net.calebscode.mythlands.core.action;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.MapKeyColumn;

import org.springframework.beans.factory.annotation.Autowired;

import net.calebscode.mythlands.core.Boss;

@Entity
public class CombatAction {

	@Autowired
	private static CombatActionFunctionManager functionManager;
	
	@Id
	private String id;
	
	@ElementCollection
	@CollectionTable(name = "combat_action_data")
	@MapKeyColumn(name = "action_id")
	private Map<String, String> actionData;
	
	@Column(nullable = false)
	private String functionName;
	
	@SuppressWarnings("unused")
	private CombatAction() {
		this(null, null);
	}
	
	public CombatAction(String id, String functionName) {
		this.id = id;
		this.functionName = functionName;
		actionData = new HashMap<>();
	}
	
	public void execute(int heroId, Boss boss) {
		functionManager.getFunction(functionName).execute(heroId, boss, actionData);
	}
	
}
