package net.mythlands.event;

public class CooldownUpdateEvent {

	public final String username;
	public final double cooldown;
	
	public CooldownUpdateEvent(String username, double cooldown) {
		this.username = username;
		this.cooldown = cooldown;
	}
	
}
