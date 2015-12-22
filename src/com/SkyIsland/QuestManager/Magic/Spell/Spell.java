package com.SkyIsland.QuestManager.Magic.Spell;

public abstract class Spell {
	
	private int cost;
	
	private String name;
	
	private String description;
	
	protected Spell(int cost, String name, String description) {
		this.cost = cost;
		this.name = name;
		this.description = description;
	}

	public int getCost() {
		return cost;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}
	
}
