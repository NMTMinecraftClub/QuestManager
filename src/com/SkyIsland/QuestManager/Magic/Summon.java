package com.SkyIsland.QuestManager.Magic;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import com.SkyIsland.QuestManager.QuestManagerPlugin;
import com.SkyIsland.QuestManager.NPC.QuestMonsterNPC;
import com.SkyIsland.QuestManager.Scheduling.Alarm;
import com.SkyIsland.QuestManager.Scheduling.Alarmable;

public class Summon extends QuestMonsterNPC implements Alarmable<Integer>, Listener {
	
	private UUID entityID;
	
	private Entity entity;
	
	private UUID casterID;
	
	public Summon(UUID casterID, Entity entity, int duration) {
		this.entityID = entity.getUniqueId();
		this.entity = entity;
		this.casterID = casterID;
		
		Alarm.getScheduler().schedule(this, 0, duration);
		Bukkit.getPluginManager().registerEvents(this, 
				QuestManagerPlugin.questManagerPlugin);
	}
	
	@Override
	public void alarm(Integer key) {
		// kill our summon
		Entity e = getEntity();
		
		if (e == null) {
			QuestManagerPlugin.questManagerPlugin.getLogger().warning("Unable to locate and remove "
				+ "summon!");
		} else {
			e.getLocation().getChunk().load();
			e.remove();
			
			playDeathEffect(e.getLocation());
		}
		
		QuestManagerPlugin.questManagerPlugin.getSummonManager().unregisterSummon(this);
	}
	
	public Entity getEntity() {
		if (entity != null && entity.isValid() && !entity.isDead() && entity.getUniqueId().equals(entityID)) {
			//still cached
			return entity;
		}
		
		//try and load last chunk the entity was in
		if (entity != null) {
			entity.getLocation().getChunk().load();
			
		} else {
			System.out.println("entity is null: " + "summon!");
		}
		
		//cache has expired (new entity ID, etc) so grab entity
		for (World w : Bukkit.getWorlds())
		for (Entity e : w.getEntities()) {
			if (e.getUniqueId().equals(entityID)) {
				entity = e;
				return e;
			}
		}
		
		//unable to find entity!
		return null;
		
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Summon)) {
			return false;
		}
		
		return ((Summon) o).entityID.equals(entityID); 
	}
	
	public void remove() {
		Alarm.getScheduler().unregister(this);
		
		getEntity();
		if (entity == null) {
			QuestManagerPlugin.questManagerPlugin.getLogger().warning("Unable to locate and remove "
					+ "summon!");
			return;
		}
		
		if (entity.getPassenger() != null) {
			entity.eject();
		}
		if (entity.getVehicle() != null) {
			entity.leaveVehicle();
		}
		
		playDeathEffect(entity.getLocation());
		entity.remove();
	}
	
	public UUID getCasterID() {
		return casterID;
	}
	
	@EventHandler
	public void onEntityDeath(EntityDeathEvent e) {
		if (e.getEntity().getUniqueId().equals(entityID)) {
			//is summon entity
			if (Alarm.getScheduler().unregister(this))
				QuestManagerPlugin.questManagerPlugin.getSummonManager().unregisterSummon(this);
			return;
		}
		
	}
	
	private void playDeathEffect(Location location) {
		for (int i = 0; i < 10; i++) {
			location.getWorld().playEffect(location, Effect.SMOKE, 0);
		}
		
		location.getWorld().playSound(location, Sound.GLASS, 1, 1.35f);
		location.getWorld().playSound(location, Sound.FIREWORK_LARGE_BLAST, 1, 1.35f);
	}
}
