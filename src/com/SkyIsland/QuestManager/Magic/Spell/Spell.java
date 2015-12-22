package com.SkyIsland.QuestManager.Magic.Spell;

import java.util.LinkedList;
import java.util.List;

import com.SkyIsland.QuestManager.Magic.Spell.Effect.SpellEffect;

public abstract class Spell {
	
	private int cost;
	
	private String name;
	
	private String description;
	
	private List<SpellEffect> spellEffects;
	
	protected Spell(int cost, String name, String description) {
		this.cost = cost;
		this.name = name;
		this.description = description;
		this.spellEffects = new LinkedList<SpellEffect>();
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
	
	public void addSpellEffect(SpellEffect effect) {
		this.spellEffects.add(effect);
	}
	
	public List<SpellEffect> getSpellEffects() {
		return spellEffects;
	}
	
}
