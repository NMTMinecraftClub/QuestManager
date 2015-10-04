package nmt.minecraft.QuestManager.Quest;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import nmt.minecraft.QuestManager.QuestManagerPlugin;
import nmt.minecraft.QuestManager.Configuration.QuestConfiguration;
import nmt.minecraft.QuestManager.Configuration.State.QuestState;
import nmt.minecraft.QuestManager.Fanciful.FancyMessage;
import nmt.minecraft.QuestManager.Player.Participant;
import nmt.minecraft.QuestManager.Player.Party;
import nmt.minecraft.QuestManager.Player.PartyDisbandEvent;
import nmt.minecraft.QuestManager.Player.QuestPlayer;
import nmt.minecraft.QuestManager.Player.Utils.CompassTrackable;
import nmt.minecraft.QuestManager.Quest.History.History;
import nmt.minecraft.QuestManager.Quest.History.HistoryEvent;
import nmt.minecraft.QuestManager.Quest.Requirements.Requirement;
import nmt.minecraft.QuestManager.Quest.Requirements.RequirementUpdateEvent;
import nmt.minecraft.QuestManager.UI.ChatMenu;
import nmt.minecraft.QuestManager.UI.Menu.SimpleChatMenu;

/**
 * Quest Interface!<br />
 * 
 * 
 * 
 * @breakdown
 * quests run and stop. They save their state and load their state. They 
 * subscribe to events and have {@link Requirement Requirements}. They are completed or failed.
 * They have rewards, disperse rewards, and collect tolls. They do whatever
 * the heck they want.
 * 
 * Specifically the quest interface specifies that quests can be started,
 * stopped, and halted. Quests must also keep track of involved players and
 * any parts of the quest involved (future work?). <br />
 * 
 * @TODO maybe split this into 'involved quests' and 'casual quests', where 'involved quests'
 * would not require any teleports out of dungeons, etc and 'involved quests' take you to
 * a special location you cannot otherwise access? If so, this should be made abstract  again
 * and the methods addPlayer and removePlayer should be made abstract and defined in subclasses,
 * where casualQuests would just remove them from the list (like this class does) and 
 * involvedQuests would teleport them out too
 * 
 * @author Skyler
 *
 */
public class Quest implements Listener {
	
	private static int NEXTID;
	
	private int ID;
	
	private boolean running;
//	
//	private Set<QuestPlayer> players;
	
	private Participant participant;
	
	private List<Goal> goals;	
	
	private int goalIndex;
	
	private int fame;
	
	private List<ItemStack> itemRewards;
	
	private String titleReward;
	
	private int moneyReward;
	
	private History history;
	
	private boolean ready;
	
	private QuestConfiguration template;
	
	//private Set<NPC> npcs;
	
	/**
	 * Whether or not this quest should be triggered on and then never evaluated again,
	 * or if it can go between completed and not completed depending on its requirements.<br />
	 * In other words, does this quest be ready to turn in and never can be un-ready after.
	 * <p>
	 * As a specific example, consider a quest to deliver 10 apples. This quest can be ready
	 * to turn in by the player obtaining 10 apples. If the player drops some of the apples,
	 * however, the quest is no longer ready to be turned in. In this case, keepState is false.
	 */
	private boolean keepState;
	
	public static void resetIDs() {
		NEXTID = 0;
	}
	
	private static int nextID() {
		return NEXTID++;
	}
	
	public Quest(QuestConfiguration template, Participant participant) {
		this.template = template;
		
		this.running = false;
		this.goals = new LinkedList<Goal>();
		this.goalIndex = 0;
		
		this.history = new History();
		ready = false;
		
		this.participant = participant;
		
		//put this into the instancing for different order for journal, compass
//		if (participant != null)
//		for (QuestPlayer qp : participant.getParticipants()) {
//			qp.addQuest(this);
//		}
		
		
		itemRewards = new LinkedList<ItemStack>();
		
		this.ID = Quest.nextID();
		
		Bukkit.getPluginManager().registerEvents(this, QuestManagerPlugin.questManagerPlugin);
	}
	
	/**
	 * @throws InvalidConfigurationException 
	 * Loads quest/objective/requirement state from the provided file
	 * @param state
	 * @throws  
	 */
	public void loadState(QuestState state) throws InvalidConfigurationException {
		
		if (!template.getName().equals(state.getName())) {
			QuestManagerPlugin.questManagerPlugin.getLogger()
				.warning("Attempting to load state information from a mismatched quest!");
			QuestManagerPlugin.questManagerPlugin.getLogger()
			.info("[" + template.getName() + "] <-/-> [" + state.getName() + "]");
		
		}

		this.participant = state.getParticipant();
		if (this.participant != null) {
			for (QuestPlayer qp : participant.getParticipants()) {
				qp.addQuest(this);
			}
		}
		
		//quickly stop the requirements that were started because we were created, instanced, etc
		goals.get(goalIndex).stop();
		
		this.goalIndex = state.getGoalIndex();

		Goal goal;
		if (goalIndex >= goals.size()) {
			goal = goals.get(goals.size() - 1);
		} else {
			goal = goals.get(goalIndex);
		}
		goal.loadState(state.getGoalState());
		
		for (Requirement req : goal.getRequirements()) {
			req.activate();
		}
		
		history = state.getHistory();
		
		
	}

	public QuestState getState() {
		//we need to definitely save goal state information (and requirement state). We also
		//and... that's kind of it actually
		QuestState state = new QuestState();
		state.setName(template.getName());
		state.setGoalIndex(goalIndex);
		
		if (goals.isEmpty()) {
			return state;
		}
		
		Goal goal;
		if (goalIndex >= goals.size()) {
			goal = goals.get(goals.size() - 1);
		} else {
			goal = goals.get(goalIndex);
		}
		state.setGoalState(goal.getState());
				
		state.setParticipant(getParticipants());
		
		if (history != null && !history.events().isEmpty()) {
			state.setHistory(history);
		}
		
		return state;
	}
	
	/**
	 * Requests information about whether the quest is currently running or is
	 * stopped/halted
	 * @return Whether or not the quest is running
	 */
	public boolean isRunning() {
		return running;
	}
	
	/**
	 * Returns whether or not the quest is ready to turn in.<br />
	 * This method causes its goals to be re-evaluated to guarantee the returned result
	 * is accurate at the time the method is called
	 * @return
	 */
	public boolean isReady() {
		update();
		return ready;
	}
	
	/**
	 * Completes the quest, dispensing rewards to involved players.
	 * @param force Should this method execute even if the quest has incomplete objectives?
	 */
	public void completeQuest(boolean force) {
		
		if (!force && !isReady()) {
			return;
		}
		
		//go through and give each of the players involved their rewards
		for (QuestPlayer qp : getParticipants().getParticipants()) {
			if (qp.getPlayer().isOnline()) {
				Player player = qp.getPlayer().getPlayer();
				
				//item rewards
				ItemStack[] items = itemRewards.toArray(new ItemStack[0]);
				Map<Integer, ItemStack> returned = player.getInventory().addItem(items);
				
				if (!returned.isEmpty()) {
					//couldn't fit all of the items, so drop them on the ground
					player.sendMessage("Unable to fit all rewards! All rewards"
							+ " that couldn't fit are at your feet.");
					for (ItemStack item : returned.values()) {
						player.getWorld().dropItem(
								player.getEyeLocation(), item);
					}
				}
				
				//cash reward
				//TODO how can we implement cash rewards without tying it to an economy?
				
				//fame reward
				qp.addFame(fame);
				if (moneyReward > 0) {
					qp.addMoney(moneyReward);
				}
				
				
				qp.completeQuest(this);
				
				qp.updateQuestBook(true);
				
			    ChatMenu menu = new SimpleChatMenu(
						new FancyMessage("")
						  .then("You've just completed the quest: ")
						  	.color(ChatColor.DARK_PURPLE)
						  	.style(ChatColor.BOLD)
						  .then(template.getName())
						    .color(ChatColor.LIGHT_PURPLE)
						  .then("\nYou received ")
						    .color(ChatColor.DARK_PURPLE)
						  .then(fame + " fame")
						  	.color(ChatColor.GOLD)
						  .then(itemRewards.isEmpty() ? "!" : 
							  " and some item rewards!")
							.color(ChatColor.DARK_PURPLE)
								
								
						);
			    
			    menu.show(player);

				if (titleReward != null && !titleReward.trim().isEmpty()) {
					qp.addTitle(titleReward);
				}
				
			    QuestManagerPlugin.questManagerPlugin.getManager().removeQuest(this);
			    
			    halt();
			}
		}
		
	}
	
	/**
	 * Stops the quest softly, optionally performing state-saving procedures
	 * and displaying messages to the involved players. Quests should also 
	 * deliver players back to an area where they are free to roam and return
	 * to homeworld portals (or the equivalent) when they stop.
	 */
	public void stop() {
		
		HandlerList.unregisterAll(this);
		
		if (!(participant instanceof Party)) {
			//get config location!
			File saveLoc = new File(QuestManagerPlugin.questManagerPlugin.getManager()
					.getSaveLocation(), template.getName() + "_" + ID + ".yml");
			
			QuestState state = getState();

			QuestManagerPlugin.questManagerPlugin.getLogger().info("Saving quest state: " + 
					saveLoc.getAbsolutePath());
			try {
				state.save(saveLoc);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if (!goals.isEmpty()) {	
			Goal goal = goals.get(Math.min(goals.size() - 1, goalIndex));
			goal.stop();
		}
		
		if (participant.getParticipants().isEmpty()) {
			return;
		}
		
		//do player stuff
//		for (QuestPlayer player : players) {
//			removePlayer(player);
//		}
		
		//remove NPCs
//		if (!npcs.isEmpty()) 
//		for (NPC npc : npcs) {
//			npc.getEntity().remove();
//		}
		
	}
	
	/**
	 * <i>Immediately</i> stops the quest, returning players to a free-roaming
	 * state. Quests are not to perform save-state procedures when
	 * halted. <br/>
	 * <b>Quests must immediately stop execution when asked to halt.<b>
	 */
	public void halt() {
		
		HandlerList.unregisterAll(this);
		
		if (participant.getParticipants().isEmpty()) {
			return;
		}
		
//		//just remove players
//		for (QuestPlayer player : players) {
//			removePlayer(player);
//		}
		
		//remove NPCs
//		if (!npcs.isEmpty()) 
//		for (NPC npc : npcs) {
//			npc.getEntity().remove();
//		}
				
		//stop goals
		if (!goals.isEmpty()) {	
			Goal goal = goals.get(Math.min(goals.size() - 1, goalIndex));
			goal.stop();
		}
		
	}
	
	public int getID() {
		return this.ID;
	}
	
	public QuestConfiguration getTemplate() {
		return template;
	}
	
	/**
	 * Returns the name of the quest, including text formatters and colors.
	 * @return The name of the quest
	 * @see {@link org.bukkit.ChatColor ChatColor}
	 */
	public String getName() {
		return template.getName();
	}
	
	/**
	 * Returns a list of enlisted goals
	 * @return
	 */
	public List<Goal> getGoals() {
		return goals;
	}
	
	public Goal getCurrentGoal() {
		return goals.get(Math.min(goalIndex, goals.size() - 1));
	}
	
	/**
	 * Appends the provided goal to the current list of goals
	 * @param goal
	 */
	public void addGoal(Goal goal) {
		goals.add(goal);
	}
	
	/**
	 * Returns a multilined description of the quest and its current objective.<br />
	 * This method does not support JSON and the fancyness that comes with it. 
	 * @see getJSONDescription()
	 * @return
	 */
	public String getDescription() {
		String builder = ChatColor.GOLD + template.getName();
		Goal goal;
		
		if (goalIndex >= goals.size()) {
			goal = goals.get(goals.size() - 1); //get the last one
		} else {
			goal = goals.get(goalIndex);
		}
		
		builder += "\n" + ChatColor.DARK_BLUE + template.getDescription();
		
		builder += "\n" + ChatColor.BLACK + "Party: ";
		if (template.getUseParty()) {
			builder += ChatColor.DARK_GREEN;
		} else {
			builder += ChatColor.GRAY;
		}
		
		builder += "Uses  ";
		
		if (template.getRequireParty()) {
			builder += ChatColor.DARK_GREEN;
		} else {
			builder += ChatColor.GRAY;
		}
		
		builder += "Requires\n" + ChatColor.BLACK;
		
		builder += "Objective:\n";
		
		for (Requirement req : goal.getRequirements()) {
			builder += req.isCompleted() ? ChatColor.GREEN + "  " : ChatColor.DARK_RED + "  ";
			builder += req instanceof CompassTrackable ? "@" : "-";
			builder += req.getDescription() + "\n";
		}
		
		if (isReady()) {
			builder += ChatColor.DARK_PURPLE + "\n  =" + template.getEndHint();
		}
		
		
		return builder;
	}
	
	public String getJSONDescription() {
		FancyMessage builder = new FancyMessage(template.getName())
				.color(ChatColor.GOLD)
				.tooltip(ChatColor.DARK_BLUE + "Click to set this quest", ChatColor.DARK_BLUE + "as your focus")
				.command("/qhistory " + this.ID)
			.then("\n" + template.getDescription() + "\n")
				.color(ChatColor.DARK_BLUE)
			.then("Party: ")
				.color(ChatColor.BLACK)
			.then("Uses  ")
				.color(template.getUseParty() ? ChatColor.DARK_GREEN : ChatColor.GRAY)
			.then("Requires\n")
				.color(template.getRequireParty() ? ChatColor.DARK_GREEN : ChatColor.GRAY)
			.then("Objective:\n")
				.color(ChatColor.BLACK);
		Goal goal;
		
		if (goalIndex >= goals.size()) {
			goal = goals.get(goals.size() - 1); //get the last one
		} else {
			goal = goals.get(goalIndex);
		}
		for (Requirement req : goal.getRequirements()) {
			builder.then((req.isCompleted() ? "  " : "  ")+ (req instanceof CompassTrackable ? "@" : "-") 
					+ req.getDescription() + "\n")
				.color(req.isCompleted() ? ChatColor.GREEN : ChatColor.DARK_RED);
		}
		
		if (isReady()) {
			builder.then("\n  =" + template.getEndHint())
				.color(ChatColor.DARK_PURPLE);
		}
		
		
		return builder.toJSONString();		
	}
	
	/**
	 * Returns the current history for reading or changing
	 * @return
	 */
	public History getHistory() {
		return history;
	}
	
	/**
	 * Adds an event to this quests history and updates all participants journals silently
	 * @param event
	 */
	public void addHistoryEvent(HistoryEvent event) {
		if (history == null) {
			return;
		}
		history.addHistoryEvent(event);
		
		if (participant != null || !participant.getParticipants().isEmpty())
		for (QuestPlayer qp : participant.getParticipants()) {
			qp.updateQuestLog(true);
		}
	}
		
	public boolean getUseParty() {
		return template.getUseParty();
	}

	public boolean getRequireParty() {
		return template.getRequireParty();
	}

	/**
	 * @return the fame
	 */
	public int getFame() {
		return fame;
	}

	/**
	 * @param fame the fame to set
	 */
	public void setFame(int fame) {
		this.fame = fame;
	}


	/**
	 * @return the itemRewards
	 */
	public List<ItemStack> getItemRewards() {
		return itemRewards;
	}

	/**
	 * @param itemRewards the itemRewards to set
	 */
	public void setItemRewards(List<ItemStack> itemRewards) {
		this.itemRewards = itemRewards;
	}
	
	public void addItemReward(ItemStack reward) {
		itemRewards.add(reward);
	}

	public String getTitleReward() {
		return titleReward;
	}

	public void setTitleReward(String titleReward) {
		this.titleReward = titleReward;
	}

	public int getMoneyReward() {
		return moneyReward;
	}

	public void setMoneyReward(int moneyReward) {
		this.moneyReward = moneyReward;
	}

	@EventHandler
	public void onRequirementUpdate(RequirementUpdateEvent e) {
		if (e.getRequirement() == null || e.getRequirement().getGoal().getQuest().equals(this)) {
			if (keepState && ready) {
				return;
			}
			
			
			update();

			for (QuestPlayer p : participant.getParticipants()) {
				p.updateQuestBook(false);
			}
		}
		System.out.println();
	}
	
	@EventHandler
	public void onPartyDisband(PartyDisbandEvent e) {
		if (e.getParty().getIDString().equals(participant.getIDString())) {
			if (template.getRequireParty()) {
				System.out.println("gonna quit!");
				//stop the quest!
				for (QuestPlayer qp : e.getParty().getParticipants()) {
					qp.removeQuest(this);
					if (qp.getPlayer().isOnline()) {
						qp.getPlayer().getPlayer().sendMessage(ChatColor.YELLOW + "The quest " 
					+ ChatColor.DARK_PURPLE + template.getName() + ChatColor.YELLOW
					+ " has been failed because the party disbanded!");
					}
				}
				
				QuestManagerPlugin.questManagerPlugin.getManager().removeQuest(this);
				halt();
			}
		}
	}
	
	/**
	 * Updates the quest information, including contained goals and requirements.
	 */
	protected void update() {
		
		//check if keepState is active and the quest is already ready
		if (keepState && ready) {
			return;
		}
		
		//if there are no goals, default to ready to turn in
		if (goals.isEmpty()) {
			ready = true;
			return;
		}
		
		Goal goal;
		if (goalIndex >= goals.size()) {
			goal = goals.get(goals.size() - 1);
		} else {
			goal = goals.get(goalIndex);
		}
			//as soon as a single goal isn't ready, the quest is not ready
//		if (!goal.isComplete()) {
//			ready = false;
//			return;
//		}
		
		if (goal.isComplete()) {
			nextGoal();			
		}
		
		//set compass to the first objective
		//TODO
	}
	
	/**
	 * Loads the next goal and starts its requirements for listening
	 */
	private void nextGoal() {
		goalIndex++;
		
		//did we just move a goal? Should the old one be deactivated?
		if (goals.size() > goalIndex) {
			//yes, because we are still under size. This means we don't stop on the last one
			goals.get(goalIndex - 1).stop();
		}
		
		if (goals.size() <= goalIndex) {
			this.ready = true;
			if (goals.size() == goalIndex)
				tellParticipants("The quest " + ChatColor.GOLD + getName() + ChatColor.RESET + " is ready to turn in!");
			return;
		}
		
		Goal goal = goals.get(goalIndex);
		for (Requirement req : goal.getRequirements()) {
			req.activate();
		}
		
		tellParticipants("You've completed your current objective for the quest " + ChatColor.GOLD + this.getName() + ChatColor.RESET);
	}
	
	private void tellParticipants(String message) {
		if (participant == null || participant.getParticipants().isEmpty()) {
			return;
		}
		
		for (QuestPlayer qp : participant.getParticipants()) {
			if (qp.getPlayer().isOnline()) {
				qp.getPlayer().getPlayer().sendMessage(message);
			}
		}
	}
	
	public Participant getParticipants() {
		return participant;
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Quest)) {
			return false;
		}
		
		Quest other = (Quest) o;
		
		if (other.ID == ID && other.getName().equals(this.getName())) {
			return true;
		}
		
		return false;
	}
	
	@Override
	public String toString() {
		return template.getName();
	}
}
