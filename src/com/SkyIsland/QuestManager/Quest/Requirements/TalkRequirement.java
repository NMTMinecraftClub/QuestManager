package com.SkyIsland.QuestManager.Quest.Requirements;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import com.SkyIsland.QuestManager.QuestManagerPlugin;
import com.SkyIsland.QuestManager.NPC.NPC;
import com.SkyIsland.QuestManager.Player.QuestPlayer;
import com.SkyIsland.QuestManager.Player.Utils.CompassTrackable;
import com.SkyIsland.QuestManager.Quest.Goal;
import com.SkyIsland.QuestManager.Quest.Requirements.Factory.RequirementFactory;

/**
 * Requirement that a participant must talk to an npc.
 * @author Skyler
 *
 */
public class TalkRequirement extends Requirement implements Listener, CompassTrackable {
	
	public static class TalkRequirementFactory extends RequirementFactory<TalkRequirement> {
		
		public TalkRequirement fromConfig(Goal goal, ConfigurationSection config) {
			TalkRequirement req = new TalkRequirement(goal);
			try {
				req.fromConfig(config);
			} catch (InvalidConfigurationException e) {
				e.printStackTrace();
			}
			return req;
		}
	}
	
	private NPC npc;
	
	private TalkRequirement(Goal goal) {
		super(goal);
	}
	
	public TalkRequirement(Goal goal, NPC npc) {
		this(goal);
		this.npc = npc;
		this.state = false;
	}

	@Override
	public void activate() {
		Bukkit.getPluginManager().registerEvents(this, QuestManagerPlugin.questManagerPlugin);
	}
	
	

	/**
	 * Catches a player's interaction and sees if it's the one we've been waiting for
	 * @param e
	 */
	@EventHandler(priority= EventPriority.HIGH)
	public void onInteract(PlayerInteractEntityEvent e) {
		
		if (state) {
			HandlerList.unregisterAll(this);
			return;
		}

		if (QuestManagerPlugin.questManagerPlugin.getPluginConfiguration().getWorlds()
				.contains(e.getPlayer().getPlayer().getWorld().getName())) {
			for (QuestPlayer qp : participants.getParticipants()) {
				if (qp.getPlayer().isOnline() && qp.getPlayer().getPlayer().getUniqueId()
						.equals(e.getPlayer().getUniqueId())) {
					//one of our participants
					//actually check interaction now
					if (e.getRightClicked().equals(npc.getEntity())) {
						//cancel and interact
						e.setCancelled(true);
						this.state = true;
						HandlerList.unregisterAll(this);
						updateQuest();
					}
				}
			}
		}
		
	}
	
	/**
	 * Nothing to do
	 */
	@Override
	public void update() {
		;
	}

	@Override
	public void fromConfig(ConfigurationSection config) throws InvalidConfigurationException {
		/*
		 * type: talk
		 * npc: [name]
		 */
		
		if (!config.contains("type") || !config.getString("type").equals("intr")) {
			throw new InvalidConfigurationException("\n  ---Invalid type! Expected '' but got " + config.get("type", "null"));
		}
		
		npc = QuestManagerPlugin.questManagerPlugin.getManager().getNPC(
			(String) config.getString("npc")
				);
		
		this.desc = config.getString("description", config.getString("action", "Right")
				+ " click the area");
	}
	
	public void stop() {
		;
	}
	
	@Override
	public String getDescription() {
		return desc;
	}

	@Override
	public Location getLocation() {
		if (npc == null) {
			return null;
		}
		
		return npc.getEntity().getLocation();
	}
	
}
