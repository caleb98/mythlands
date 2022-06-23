package net.calebscode.mythlands.core.action;

import java.util.HashMap;

import org.springframework.stereotype.Component;

@Component
public final class CombatActionFunctionManager {

	private HashMap<String, CombatActionFunction> functionMap;

	public CombatActionFunctionManager() {
		functionMap = new HashMap<>();
		
		// Register all functions here
	}
	
	public CombatActionFunction getFunction(String functionName) {
		return functionMap.get(functionName);
	}
	
}
