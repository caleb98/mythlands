package net.calebscode.heroland.messages;

public class BossDiedMessage {

	private String bossName;
	private String killedBy;
	
	public BossDiedMessage(String bossName, String killedBy) {
		this.bossName = bossName;
		this.killedBy = killedBy;
	}
	
	public String getBossName() {
		return bossName;
	}
	
	public String getKilledBy() {
		return killedBy;
	}
	
}
