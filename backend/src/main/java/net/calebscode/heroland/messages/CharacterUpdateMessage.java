package net.calebscode.heroland.messages;

import com.google.gson.JsonArray;

public class CharacterUpdateMessage {

	public final JsonArray updates;
	
	public CharacterUpdateMessage(JsonArray updates) {
		this.updates = updates;
	}
	
}
