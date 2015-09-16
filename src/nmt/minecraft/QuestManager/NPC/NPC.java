package nmt.minecraft.QuestManager.NPC;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import nmt.minecraft.QuestManager.QuestManagerPlugin;
import nmt.minecraft.QuestManager.Scheduling.Tickable;

public abstract class NPC implements ConfigurationSerializable, Listener, Tickable {
	
	/**
	 * Cache value for saving lookup times for entities
	 */
	private Entity entity;
	
	/**
	 * The actual ID of the entity we're monitoring
	 */
	protected UUID id;
	
	protected String name;
	
	protected NPC() {
		Bukkit.getPluginManager().registerEvents(this, QuestManagerPlugin.questManagerPlugin);
	}
	
	/**
	 * Returns the entity this NPC is attached to.<br />
	 * This method attempts to save cycles by caching the last known entity to
	 * represent our UUID'd creature. If the cache is no longer valid, an entire
	 * sweep of worlds and entities is performed to lookup the entity.
	 * @return The entity attached to our UUID, or NULL if none is found
	 */
	public Entity getEntity() {
		if (entity != null && entity.isValid() && !entity.isDead() && entity.getUniqueId().equals(id)) {
			//still cached
			return entity;
		}
		
		//try and load last chunk the entity was in
		if (entity != null) {
			entity.getLocation().getChunk().load();
			
		} else {
			System.out.println("entity is null: " + name);
		}
		
		//cache has expired (new entity ID, etc) so grab entity
		for (World w : Bukkit.getWorlds())
		for (Entity e : w.getEntities()) {
			if (e.getUniqueId().equals(id)) {
				entity = e;
				return e;
			}
		}
		
		//unable to find entity!
		return null;
		
	}
	
	/**
	 * Register an entity to this NPC. This method also updates the ID of this npc
	 * @param entity
	 */
	public void setEntity(Entity entity) {
		this.entity = entity;
		this.id = entity.getUniqueId();
	}
	
	/**
	 * Specify the ID used for this entity
	 * @param id
	 */
	public void setID(UUID id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEntityEvent e) {
		if (e.getRightClicked().getUniqueId().equals(id)) {
			e.setCancelled(true);
			this.interact(e.getPlayer());
		}
	}
	
	@EventHandler
	public void onEntityHurt(EntityDamageEvent e) {
		if (!e.getEntity().getUniqueId().equals(id)) {
			return;
		}
			
		//grab the damage. if it's gonna kill us, just take no damage? 
		
		Entity ent = getEntity();
		if (!(ent instanceof LivingEntity)) {
			return;
		}
		
		LivingEntity l = (LivingEntity) ent;
		
		if (e.getDamage() >= l.getHealth()){
			e.setDamage(0.0);
		}
	}
	
	public void removeEntity() {
		final Entity e = getEntity();
		
		e.getLocation().getChunk().load();

		Bukkit.getScheduler().runTaskLater(QuestManagerPlugin.questManagerPlugin, 
				new Runnable(){

					@Override
					public void run() {
						e.remove();
					}
				
				}, 1
		);
	}
	
	protected abstract void interact(Player player);
}
