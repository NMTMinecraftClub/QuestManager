package com.SkyIsland.QuestManager.Magic;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import com.SkyIsland.QuestManager.QuestManagerPlugin;
import com.SkyIsland.QuestManager.Scheduling.Alarm;
import com.SkyIsland.QuestManager.Scheduling.Alarmable;

public class Summon implements Alarmable<Integer>, Listener {
	
	private UUID entityID;
	
	private Entity entity;
	
	private UUID casterID;
	
	public Summon(UUID casterID, Entity entity, int duration) {
		this.entityID = entity.getUniqueId();
		this.entity = entity;
		this.casterID = casterID;
		
		Alarm.getScheduler().schedule(this, 0, duration);
	}
	
	@Override
	public void alarm(Integer key) {
		// kill our summon
		Entity e = getEntity();
		
		if (e == null) {
			QuestManagerPlugin.questManagerPlugin.getLogger().warning("Unable to locate and remove "
				+ "summon!");
		}
		
		e.getLocation().getChunk().load();
		e.remove();
		
		for (int i = 0; i < 10; i++)
			e.getLocation().getWorld().playEffect(e.getLocation(), Effect.SMOKE, 0);
		
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
		getEntity();
		if (entity == null) {
			QuestManagerPlugin.questManagerPlugin.getLogger().warning("Unable to locate and remove "
					+ "summon!");
			return;
		}
		
		entity.remove();
	}
	
	public UUID getCasterID() {
		return casterID;
	}
	
	@EventHandler
	public void onSummonDeath(EntityDeathEvent e) {
		if (e.getEntity().getUniqueId() == entityID) {
			Alarm.getScheduler().unregister(this);
			for (int i = 0; i < 10; i++)
				e.getEntity().getLocation().getWorld().playEffect(e.getEntity().getLocation(), Effect.SMOKE, 0);
			
			QuestManagerPlugin.questManagerPlugin.getSummonManager().unregisterSummon(this);
		}
	}
}
