package net.mythlands.event;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class CharacterUpdateEvent {

	public final String username;
	public final JsonArray updates;
	
	public CharacterUpdateEvent(String username, JsonObject... updates) {
		this.username = username;
		this.updates = new JsonArray();
		for(JsonObject update : updates) {
			this.updates.add(update);
		}
	}
	
}
