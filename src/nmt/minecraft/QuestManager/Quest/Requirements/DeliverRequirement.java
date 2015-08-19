package nmt.minecraft.QuestManager.Quest.Requirements;

import nmt.minecraft.QuestManager.QuestManagerPlugin;
import nmt.minecraft.QuestManager.Configuration.State.RequirementState;
import nmt.minecraft.QuestManager.Configuration.State.StatekeepingRequirement;
import nmt.minecraft.QuestManager.Player.Participant;
import nmt.minecraft.QuestManager.Player.QuestPlayer;
import nmt.minecraft.QuestManager.Quest.Goal;
import nmt.minecraft.QuestManager.Quest.Requirements.Factory.RequirementFactory;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Requirement specification that checks for an itemstack and removes it when it's there
 * @author Skyler
 *
 */
public class DeliverRequirement extends Requirement implements Listener,  StatekeepingRequirement {
	
	public static class DeliverFactory extends RequirementFactory<DeliverRequirement> {
		
		public DeliverRequirement fromConfig(Goal goal, ConfigurationSection config) {
			DeliverRequirement req = new DeliverRequirement(goal);
			req.participants = goal.getQuest().getParticipants();
			try {
				req.fromConfig(config);
			} catch (InvalidConfigurationException e) {
				e.printStackTrace();
			}
			return req;
		}
	}
	
	private Material itemType;
	
	private int itemCount;
	
	private DeliverRequirement(Goal goal) {
		super(goal);
		Bukkit.getPluginManager().registerEvents(this, QuestManagerPlugin.questManagerPlugin);
	}
	
	public DeliverRequirement(Participant participants, Goal goal, Material itemType) {
		this(participants, goal, "", itemType, 1);
	}
	
	public DeliverRequirement(Participant participants, Goal goal, String description, Material itemType) {
		this(participants, goal, description, itemType, 1);
	}
	
	public DeliverRequirement(Participant participants, Goal goal, String description, Material itemType, int itemCount) {
		super(goal, description);
		state = false;
		this.itemType = itemType;
		this.itemCount = itemCount;
		this.participants = participants;
		
	}

	/**
	 * @return the itemType
	 */
	public Material getItemType() {
		return itemType;
	}

	/**
	 * @return the itemCount
	 */
	public int getItemCount() {
		return itemCount;
	}
	
	@EventHandler
	public void onInventoryChange(PlayerPickupItemEvent e) {
		if (this.participants == null) {
			return;
		}
		if (!e.isCancelled() && e.getItem().getItemStack().getType() == itemType) {
			
			for (QuestPlayer qp : participants.getParticipants()) {
				if (qp.getPlayer().getUniqueId().equals(e.getPlayer().getUniqueId())) {
					//adjust for that stupid 'hasn't happened yet' error
					int count = e.getItem().getItemStack().getAmount();
					e.getPlayer().getInventory().addItem(e.getItem().getItemStack());
					update();
					
					int pos = e.getPlayer().getInventory().first(itemType);
					ItemStack item = e.getPlayer().getInventory().getItem(pos);
					item.setAmount(item.getAmount() - count);
					e.getPlayer().getInventory().setItem(pos, item);
					
					return;
				}
			}
			
		}
	}
	
	@EventHandler
	public void onInventoryChange(PlayerDropItemEvent e) {
		if (this.participants == null) {
			return;
		}
		if (!e.isCancelled() && e.getItemDrop().getItemStack().getType() == itemType) {
			
			for (QuestPlayer qp : participants.getParticipants()) {
				if (qp.getPlayer().getUniqueId().equals(e.getPlayer().getUniqueId())) {
					//adjust for that stupid 'hasn't happened yet' error
					int count = e.getItemDrop().getItemStack().getAmount();
					e.getPlayer().getInventory().addItem(e.getItemDrop().getItemStack());
					update();
					
					int pos = e.getPlayer().getInventory().first(itemType);
					ItemStack item = e.getPlayer().getInventory().getItem(pos);
					item.setAmount(item.getAmount() - count);
					e.getPlayer().getInventory().setItem(pos, item);
					
					return;
				}
			}
			
		}
	}
	
	/**
	 * Checks all involved {@link nmt.minecraft.QuestManager.Player.Participant Participant(s)}
	 * to check if the required item & quantity requirements are satisfied.<br />
	 * <b>Note:</b> This does not check if the above quantity-requirement is met <i>across</i>
	 * all members, but instead of any single member has the required number of items.<br />
	 * TODO fix the above noted problem
	 */
	@Override
	protected void update() {
		sync();
		for (QuestPlayer player : participants.getParticipants()) {
			if (player.getPlayer().isOnline())
			if (player.getPlayer().getPlayer().getInventory().containsAtLeast(new ItemStack(itemType), itemCount)) {
				if (!state) {
					//if we just achieved it, update the quest!
					this.state = true;
					player.getPlayer().getPlayer().getInventory().remove(
							new ItemStack(itemType, itemCount));
					HandlerList.unregisterAll(this);
					updateQuest();
				}
				return;
			}
		}
		
		state = false;
	}

	@Override
	public void fromConfig(ConfigurationSection config) throws InvalidConfigurationException {
		//we need to load information about what we need to possess and how much
		//our config is 
		//  type: "delr"
		//  itemYype: (Material. ENUM CONSTANT NAME)
		//  count: [int]
		
		if (!config.contains("type") || !config.getString("type").equals("pr")) {
			throw new InvalidConfigurationException("\n  ---Invalid type! Expected 'pr' but got " + config.getString("type", "null"));
		}
		
		this.itemType = Material.valueOf(
				config.getString("itemType", "AIR"));
		
		this.itemCount = config.getInt("count", 1);
		
	}

	@Override
	public RequirementState getState() {
		/*
		 * state info is just whether this has been filled or not
		 * state: true/false
		 */
		YamlConfiguration config = new YamlConfiguration();
		config.set("state", state);
		
		RequirementState image = new RequirementState(config);
		
		return image;
	}

	@Override
	public void loadState(RequirementState state)
			throws InvalidConfigurationException {
		ConfigurationSection config = state.getConfig();
		if (!config.contains("state")) {
			throw new InvalidConfigurationException();
		}
		
		this.state = config.getBoolean("state");
		
	}

	@Override
	public void stop() {
		;
	}
	
	
	
}
