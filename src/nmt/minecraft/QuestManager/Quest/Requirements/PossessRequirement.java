package nmt.minecraft.QuestManager.Quest.Requirements;

import nmt.minecraft.QuestManager.QuestManagerPlugin;
import nmt.minecraft.QuestManager.Player.Participant;
import nmt.minecraft.QuestManager.Player.QuestPlayer;
import nmt.minecraft.QuestManager.Quest.Goal;
import nmt.minecraft.QuestManager.Quest.Requirement;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Requirement specification that requires the user to have some quantity of a specific item
 * @author Skyler
 *
 */
public class PossessRequirement extends Requirement implements Listener {
	
	private Material itemType;
	
	private int itemCount;
	
	private Participant participants;
	
	public PossessRequirement(Participant participants, Goal goal, Material itemType) {
		this(participants, goal, "", itemType, 1);
	}
	
	public PossessRequirement(Participant participants, Goal goal, String description, Material itemType) {
		this(participants, goal, description, itemType, 1);
	}
	
	public PossessRequirement(Participant participants, Goal goal, String description, Material itemType, int itemCount) {
		super(goal, description);
		state = false;
		this.itemType = itemType;
		this.itemCount = itemCount;
		this.participants = participants;
		
		Bukkit.getPluginManager().registerEvents(this, QuestManagerPlugin.questManagerPlugin);
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
	public void onInventoryChange(InventoryEvent e) {
		update();
		updateQuest();
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
		
		for (QuestPlayer player : participants.getParticipants()) {
			if (player.getPlayer().getInventory().contains(new ItemStack(itemType, itemCount))) {
				this.state = true;
				return;
			}
		}
		
		state = false;
	}

	@Override
	public void fromConfig(YamlConfiguration config) throws InvalidConfigurationException {
		//we need to load information about what we need to possess and how much
		//our config is 
		//  type: "pr"
		//  itemYype: (Material. ENUM CONSTANT NAME)
		//  count: [int]
		
		if (!config.contains("type") || !config.getString("type").equals("pr")) {
			throw new InvalidConfigurationException();
		}
		
		this.itemType = Material.valueOf(
				config.getString("itemType", "AIR"));
		
		this.itemCount = config.getInt("count", 1);
		
	}
	
	
	
}
