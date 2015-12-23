package com.SkyIsland.QuestManager.Magic;

import org.bukkit.entity.Entity;

public interface MagicUser {
	
	public Entity getEntity();
	
	public int getMP();
	
	public void addMP(int amount);
	
}
