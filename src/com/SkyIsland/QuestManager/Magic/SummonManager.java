package com.SkyIsland.QuestManager.Magic;

import java.util.LinkedList;
import java.util.List;

public final class SummonManager {
	
	private List<Summon> summons;
	
	public SummonManager() {
		this.summons = new LinkedList<>();
	}
	
	public void registerSummon(Summon summon) {
		summons.add(summon);
	}
	
	public void unregisterSummon(Summon summon) {
		summons.remove(summon);
	}
	
	/**
	 * Goes through all summons and removes them, also clearing this manager's list
	 */
	public void removeSummons() {
		
	}
}
