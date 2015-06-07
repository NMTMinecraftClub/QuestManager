package nmt.minecraft.QuestManager.Quest.Requirements;

import nmt.minecraft.QuestManager.Player.Participant;
import nmt.minecraft.QuestManager.Player.QuestPlayer;
import nmt.minecraft.QuestManager.Quest.Goal;

import org.bukkit.Material;
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
	
	public PossessRequirement(Goal goal, Material itemType) {
		this(goal, "", itemType, 1);
	}
	
	public PossessRequirement(Goal goal, String description, Material itemType) {
		this(goal, description, itemType, 1);
	}
	
	public PossessRequirement(Goal goal, String description, Material itemType, int itemCount) {
		super(goal, description);
		state = false;
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
			}
		}
		
		state = false;
		
		updateQuest();
	}
	
	
	
}
