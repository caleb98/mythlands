package net.calebscode.mythlands.messages.out;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class CharacterUpdateMessage {

	public final JsonArray updates;
	
	public CharacterUpdateMessage(JsonArray updates) {
		this.updates = updates;
	}
	
	public CharacterUpdateMessage(JsonObject... updateInfos) {
		updates = new JsonArray();
		add(updateInfos);
	}
	
	public void add(JsonObject... updateInfos) {
		for(JsonObject update : updateInfos) {
			updates.add(update);
		}
	}
	
}
