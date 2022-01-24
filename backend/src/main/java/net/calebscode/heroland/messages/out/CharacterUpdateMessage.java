package net.calebscode.heroland.messages.out;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class CharacterUpdateMessage {

	public final JsonArray updates = new JsonArray();
	
	public CharacterUpdateMessage() {}
	
	public CharacterUpdateMessage(JsonObject... updateInfos) {
		for(JsonObject o : updateInfos) {
			updates.add(o);
		}
	}
	
	public void add(JsonObject updateInfo) {
		updates.add(updateInfo);
	}
	
}
