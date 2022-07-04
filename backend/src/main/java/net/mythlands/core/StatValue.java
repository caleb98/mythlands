package net.mythlands.core;

import javax.persistence.Embeddable;

@Embeddable
public class StatValue {

	/**
	 * The base stat value.
	 */
	private double base;
	
	/**
	 * Any flat value additions to the base stat value.
	 */
	private double additional;
	
	/**
	 * Any percent increase modifiers to the value (base + additional).
	 * Increased modifiers are added together, so two 15% increases 
	 * would result in a total 30% stat increase.
	 */
	private double increase;
	
	/**
	 * Any further multipliers on the increased value.
	 * Multipliers are all multiplied together, so two 15% multipliers
	 * would result in a total 1.15 * 1.15 stat increase.
	 */
	private double multiplier;

	public StatValue() {
		this(0);
	}
	
	public StatValue(int base) {
		this.base = base;
		additional = 0;
		increase = 0;
		multiplier = 1;
	}
	
	public double getBase() {
		return base;
	}
	
	public void setBase(double base) {
		this.base = base;
	}
	
	public void modifyBase(double amount) {
		this.base += amount;
	}
	
	public double getAdditional() {
		return additional;
	}
	
	public void addAdditional(double amount) {
		additional += amount;
	}
	
	public void removeAdditional(double amount) {
		additional -= amount;
	}
	
	public double getIncrease() {
		return increase;
	}
	
	public void addIncrease(double amount) {
		increase += amount;
	}
	
	public void removeIncrease(double amount) {
		increase -= amount;
	}
	
	public double getMultiplier() {
		return multiplier;
	}
	
	public void addMultiplier(double amount) {
		multiplier *= (1 + amount);
	}
	
	public void removeMultiplier(double amount) {
		multiplier /= (1 + amount);
	}
	
	public double getValue() {
		double flat = base + additional;
		return flat * (1 + increase) * multiplier;
	}
	
}

