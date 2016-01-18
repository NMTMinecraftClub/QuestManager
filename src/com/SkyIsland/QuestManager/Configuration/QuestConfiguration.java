package com.SkyIsland.QuestManager.Configuration;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import com.SkyIsland.QuestManager.QuestManagerPlugin;
import com.SkyIsland.QuestManager.Configuration.Utils.LocationState;
import com.SkyIsland.QuestManager.NPC.NPC;
import com.SkyIsland.QuestManager.NPC.SimpleQuestStartNPC;
import com.SkyIsland.QuestManager.Player.Participant;
import com.SkyIsland.QuestManager.Player.QuestPlayer;
import com.SkyIsland.QuestManager.Quest.Goal;
import com.SkyIsland.QuestManager.Quest.Quest;
import com.SkyIsland.QuestManager.Quest.Requirements.Requirement;
import com.SkyIsland.QuestManager.UI.Menu.Message.Message;

/**
 * Wrapper for quest configuration
 * @author Skyler
 *
 */
public class QuestConfiguration {
	
	public static enum EndType {
		SAME,
		OTHERNPC,
		NOTURNIN;
	}
	
	public static final double configVersion = 1.00;
	
	private YamlConfiguration config;
		
	public QuestConfiguration(YamlConfiguration config) throws InvalidConfigurationException {
		
		this.config = config;
		
		if (!config.contains("configversion")) {
			QuestManagerPlugin.questManagerPlugin.getLogger().warning("Invalid quest "
					+ "configuration!");
			
			//just null out the config
			config = null;
			
			throw new InvalidConfigurationException();
		}
		
		//check config has all the fields we need, for safety
		checkConfig();
		
	}
	
	
	/**
	 * Checks the held configuration file for missing/corrupted/outdated fields, and corrects
	 * them when possible. <br />
	 * This is an internal method with straight implementation details.
	 * @see QuestConfigurationField
	 */
	private void checkConfig() {
		
		if (config.getDouble("configversion", 0.0) - configVersion > .001) {
			String name = config.getString(QuestConfigurationField.NAME.getKey(), "NO NAME");
			QuestManagerPlugin.questManagerPlugin.getLogger().warning("The quest [" + name + "] has an invalid version!\n"
					+ "QuestManager Configuration Version: " + configVersion + " doesn't match quest's: " 
					+ config.getDouble("configversion", 0.0));
			
		}
		
		//Check each field and put in defaults if they aren't there (niave approach)
		for (QuestConfigurationField field : QuestConfigurationField.values()) {
			if (!config.contains(field.getKey())) {
				QuestManagerPlugin.questManagerPlugin.getLogger().warning("[" + getName() + "] "
						+ "Failed to find field information: " + field.name());
				QuestManagerPlugin.questManagerPlugin.getLogger().info("Adding default value...");
				config.set(field.getKey(), field.getDefault());
			}
		}
	}
	
	/**
	 * Returns the stored quest name
	 * @return The name of the quest, or it's registered {@link QuestConfigurationField default}
	 */
	public String getName() {
		return config.getString(QuestConfigurationField.NAME.getKey(), (String) QuestConfigurationField.NAME.getDefault());
	}
	
	/**
	 * Gets the quest description
	 * @return
	 */
	public String getDescription() {
		return config.getString(QuestConfigurationField.DESCRIPTION.getKey(), (String) QuestConfigurationField.DESCRIPTION.getDefault());
	}
	
	/**
	 * Returns the end hint for this quest.<br /.
	 * This usually denotes which NPC to turn it into, in a slightly more custimized fashion than just subbing in the name
	 * @return
	 */
	public String getEndHint() {
		return config.getString(QuestConfigurationField.ENDHINT.getKey(), (String) QuestConfigurationField.ENDHINT.getDefault());
	}
	
	/**
	 * Gets whether or not the embedded quest has {@link com.SkyIsland.QuestManager.Quest.Quest#keepState save-state} enabled
	 * @return
	 */
	public boolean getSaveState() {
		return config.getBoolean(QuestConfigurationField.SAVESTATE.getKey(), 
				(boolean) QuestConfigurationField.SAVESTATE.getDefault());
	}
	
	public boolean isRepeatable() {
		return config.getBoolean(QuestConfigurationField.REPEATABLE.getKey(), 
				(boolean) QuestConfigurationField.REPEATABLE.getDefault());
	}
	
	/**
	 * Is this quest a session quest? Session quests can only have one instantiation at a time,
	 * or one session at a time.
	 * @return
	 */
	public boolean isSession() {
		return config.getBoolean(QuestConfigurationField.SESSION.getKey(), 
				(boolean) QuestConfigurationField.SESSION.getDefault());
	}
	
	@SuppressWarnings("unchecked")
	public List<String> getRequiredQuests() {
		if (!config.contains(QuestConfigurationField.PREREQS.getKey())) {
			return (List<String>) QuestConfigurationField.PREREQS.getDefault();
		}
		
		return config.getStringList(QuestConfigurationField.PREREQS.getKey());
				
	}
	
	public boolean getUseParty() {
		return config.getBoolean(
				QuestConfigurationField.USEPARTY.getKey(),
				(Boolean) QuestConfigurationField.USEPARTY.getDefault());
	}
	
	public boolean getRequireParty() {
		return config.getBoolean(
				QuestConfigurationField.REQUIREPARTY.getKey(),
				(Boolean) QuestConfigurationField.REQUIREPARTY.getDefault());
	}
	
	public Location getStartingLocation() {
		Object o = config.get(QuestConfigurationField.STARTLOC.getKey());
		if (o instanceof LocationState) {
			LocationState ls = (LocationState) o;
			return ls.getLocation();
		} else {
			return null;
		}
	}
	
	public Location getExitLocation() {
		Object o = config.get(QuestConfigurationField.EXIT.getKey());
		if (o instanceof LocationState) {
			LocationState ls = (LocationState) o;
			return ls.getLocation();
		} else {
			return null;
		}
	}
	
	public boolean getFailOnDeath() {
		return config.getBoolean(
				QuestConfigurationField.FAILONDEATH.getKey(),
				(Boolean) QuestConfigurationField.FAILONDEATH.getDefault());
	}
	
	public Collection<NPC> getAuxNPCs() {
		
		List<NPC> npcs = new LinkedList<NPC>();
		
		//get list of NPCs and get them created
		if (config.contains(QuestConfigurationField.NPCS.getKey())) {
			ConfigurationSection npcSection = config.getConfigurationSection(
					QuestConfigurationField.NPCS.getKey());
			
			NPC npc;
			if (!(npcSection == null) && !npcSection.getKeys(false).isEmpty()) {
				for (String key : npcSection.getKeys(false)) {
					npc = (NPC) npcSection.get(key);
					npc.setQuestName(this.getName());
					npcs.add(npc);
				}
			}
		}
		
		return npcs;
	}
	
	/**
	 * Reads and instantiates a new starting npc for this quest.<br />
	 * It's common practice to only call this method a single time, as you only need one copy
	 * of 'earl' who people talk to to give the quest.
	 * @return The new NPC instance
	 */
	public NPC GetStartingNPCInstance() {
		//load up starting NPC information
		SimpleQuestStartNPC startingNPC = null;
		if (!config.contains(QuestConfigurationField.START.getKey())) {
			QuestManagerPlugin.questManagerPlugin.getLogger().info(
					  "Quest has no starting npc specified: " + getName());
		} else {
			startingNPC = (SimpleQuestStartNPC) config.get(QuestConfigurationField.START.getKey());
			startingNPC.setQuestTemplate(this);
			startingNPC.setQuestName(getName());
			
			if (config.contains(QuestConfigurationField.END.getKey())) {
				
				if (config.getString(QuestConfigurationField.END.getKey() + ".type",
						(String) QuestConfigurationField.END.getDefault()).equals("same")) {
					
					Message msg = (Message) config.get(QuestConfigurationField.END.getKey() + ".value");
					
					if (msg == null) {
						QuestManagerPlugin.questManagerPlugin.getLogger().info(
								  "Quest has no end action value specified: " + getName());
					} else {
						startingNPC.markAsEnd(msg);
					}
				} else {
					//it's an NPC they're specifying?
					
				}
			} else {
				QuestManagerPlugin.questManagerPlugin.getLogger().info(
						  "Quest has no end action specified: " + getName());
			}
		}
		
		return startingNPC;
	}
	
	public EndType getEndType() {
		try {
			return EndType.valueOf((String) config.getString(QuestConfigurationField.END.getKey()
				+ ".type").toUpperCase());
		} catch (Exception e) {
			return EndType.SAME;
		}
	}
	
	/**
	 * Returns the complete {@link com.SkyIsland.QuestManager.Quest.Quest Quest} this configuration represents.<br />
	 * Subsequent calls to this method return new instances of the represented quest. It is
	 * up to the caller to keep track of returned quests and optimize performance when simply
	 * needing a reference to previously-instantiated Quests
	 * @return A new quest instance
	 * @throws InvalidConfigurationException, SessionConflictException 
	 */
	public Quest instanceQuest(Participant participant) throws InvalidConfigurationException,
		SessionConflictException {
				
		if (!config.contains(QuestConfigurationField.GOALS.getKey())) {
			return null;
		}
		
		if (isSession())
		for (Quest q : QuestManagerPlugin.questManagerPlugin.getManager().getRunningQuests())
		if (q.getName().equals(getName())){
			//can't instantiate it, cause one's already going
			throw new SessionConflictException();
		}
		
		ConfigurationSection questSection = config.getConfigurationSection(
				QuestConfigurationField.GOALS.getKey());
		
		List<ConfigurationSection> goalList = new LinkedList<ConfigurationSection>();
		for (String key : questSection.getKeys(false)) {
			goalList.add(questSection.getConfigurationSection(key));
		}
			
		Quest quest = new Quest(this, participant);
		
		for (ConfigurationSection section : goalList) {
			Goal goal = Goal.fromConfig(quest, section);
			quest.addGoal(goal);
		}
		
		//activate first goal
		for (Requirement req : quest.getGoals().get(0).getRequirements()) {
			req.activate();
		}
		
		//get fame and reward info
		quest.setFame(config.getInt(QuestConfigurationField.FAME.getKey()));
		quest.setTitleReward(config.getString(QuestConfigurationField.TITLEREWARD.getKey()));
		quest.setSpellReward(config.getString(QuestConfigurationField.SPELLREWARD.getKey()));
		quest.setMoneyReward(config.getInt(QuestConfigurationField.MONEYREWARD.getKey()));
		
		@SuppressWarnings("unchecked")
		List<ItemStack> rewards = (List<ItemStack>) config.getList(QuestConfigurationField.REWARDS.getKey());

		
		if (rewards != null && !rewards.isEmpty())
		for (ItemStack item : rewards) {
			quest.addItemReward(item);
		}
		
		if (participant != null)
			for (QuestPlayer qp : participant.getParticipants()) {
				qp.addQuest(quest);
			}
		
		
		
		return quest;
	}
	
}
