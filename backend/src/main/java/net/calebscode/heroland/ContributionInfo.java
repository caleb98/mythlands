package net.calebscode.heroland;

public class ContributionInfo {

	private int totalDamage = 0;
	private int numAttacks = 0;
	private boolean dealtKillingBlow = false;
	
	public int getTotalDamage() {
		return totalDamage;
	}
	
	public void setTotalDamage(int totalDamage) {
		this.totalDamage = totalDamage;
	}
	
	public void addTotalDamage(int amount) {
		this.totalDamage += amount;
	}
	
	public int getNumAttacks() {
		return numAttacks;
	}
	
	public void setNumAttacks(int numAttacks) {
		this.numAttacks = numAttacks;
	}
	
	public void incrementAttacks() {
		this.numAttacks++;
	}
	
	public boolean dealtKillingBlow() {
		return dealtKillingBlow;
	}
	
	public void setDealtKillingBlow(boolean dealtKillingBlow) {
		this.dealtKillingBlow = dealtKillingBlow;
	}
	
}
