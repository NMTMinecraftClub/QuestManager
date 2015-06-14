package nmt.minecraft.QuestManager.NPC;

import nmt.minecraft.QuestManager.QuestManagerPlugin;

import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public abstract class NPC implements ConfigurationSerializable, Listener {
	
	protected Entity entity;
	
	protected String name;
	
	protected NPC() {
		Bukkit.getPluginManager().registerEvents(this, QuestManagerPlugin.questManagerPlugin);
	}
	
	public Entity getEntity() {
		return entity;
	}
	
	public String getName() {
		return name;
	}
	
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEntityEvent e) {
		if (e.getRightClicked().equals(entity)) {
			e.setCancelled(true);
			this.interact(e.getPlayer());
		}
	}
	
	protected abstract void interact(Player player);
}
