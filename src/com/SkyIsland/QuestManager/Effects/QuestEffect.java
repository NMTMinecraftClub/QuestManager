package com.SkyIsland.QuestManager.Effects;

import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * An effect used by the QuestManager to signal something to the player.
 * @author Skyler
 *
 */
public abstract class QuestEffect {

	public abstract void play(Player player, Location location);
	
}
