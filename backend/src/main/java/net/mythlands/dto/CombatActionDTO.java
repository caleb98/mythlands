package net.mythlands.dto;

import java.util.HashMap;
import java.util.Map;

import net.mythlands.core.action.CombatAction;

public class CombatActionDTO {

	public final String id;
	public final Map<String, String> actionData;
	public final String functionName;
	
	public CombatActionDTO(CombatAction action) {
		id = action.getId();
		functionName = action.getFunctionName();
		
		actionData = new HashMap<>();
		action.getActionData().forEach(actionData::put);
	}
	
}