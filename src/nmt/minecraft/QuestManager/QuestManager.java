package nmt.minecraft.QuestManager;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.scoreboard.Scoreboard;

import nmt.minecraft.QuestManager.Configuration.QuestConfiguration;
import nmt.minecraft.QuestManager.Configuration.State.QuestState;
import nmt.minecraft.QuestManager.NPC.NPC;
import nmt.minecraft.QuestManager.Player.Party;
import nmt.minecraft.QuestManager.Player.QuestPlayer;
import nmt.minecraft.QuestManager.Quest.Quest;

public class QuestManager implements Listener {
	
	private List<Quest> runningQuests;
	
	private List<QuestConfiguration> questTemplates;
	
	private File saveDirectory;
	
	private Scoreboard scoreboard;
	
	private Set<NPC> questNPCs;
	
	/**
	 * Constructs a manager with the given directory information and a config file with
	 * the manager configuration section ready. The config passed is expected to have
	 * one key (the name of the manager) and the value be a list of strings (name of quests)
	 */
	public QuestManager(File questDirectory, File saveDirectory, 
			List<String> questNames) {
		
		runningQuests = new LinkedList<Quest>();
		questTemplates = new LinkedList<QuestConfiguration>();
		questNPCs = new HashSet<NPC>();
		scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();

		Bukkit.getPluginManager().registerEvents(this, QuestManagerPlugin.questManagerPlugin);
		
		//purge villagers, if enabled
		if (QuestManagerPlugin.questManagerPlugin.getPluginConfiguration().getVillagerCleanup()) {
			//go through worlds, kill them!
			World w;
			for (String worldName : QuestManagerPlugin.questManagerPlugin.getPluginConfiguration().getWorlds()) {
				w = Bukkit.getWorld(worldName);
				if (w == null) {
					continue;
				}
				
				for (Entity e : w.getEntities()) 
				if (e.getType().equals(EntityType.VILLAGER)) {
					e.getLocation().getChunk(); //load chunk
					e.remove();
				}
				
				System.out.println("purged " + worldName);
			}
			
			QuestManagerPlugin.questManagerPlugin.getLogger().info("Purged villagers!");
		}
		
		Party.maxSize = QuestManagerPlugin.questManagerPlugin.getPluginConfiguration().getMaxPartySize();
		
		this.saveDirectory = saveDirectory;
		
		if (questNames.isEmpty()) {
			QuestManagerPlugin.questManagerPlugin.getLogger().info(
					"There were no quest templates to load!\n  "
					+ "To add quests, place the proper configuration files in "
					+ questDirectory.getAbsolutePath());
			return;
		}
		
		//lookup and load templates for each quest name given
		for (String questName : questNames) {
			File questConfigFile = new File(questDirectory, questName + ".yml");
			if (!questConfigFile.exists() || questConfigFile.isDirectory()) {
				QuestManagerPlugin.questManagerPlugin.getLogger().warning(
						"Unable to locate quest config file: "
						+ questConfigFile.getAbsolutePath());
				continue;
			}
			
			//found the file, let's load it up!
			YamlConfiguration questConfig = new YamlConfiguration();
			try {
				questConfig.load(questConfigFile);
			} catch (IOException | InvalidConfigurationException e) {
				e.printStackTrace();
				QuestManagerPlugin.questManagerPlugin.getLogger().warning(
						"Unable to load quest from file: " + questConfigFile.getAbsolutePath());
				continue;
			}
			
			QuestConfiguration questTemplate;
			try {
				questTemplate = new QuestConfiguration(questConfig);
			} catch (InvalidConfigurationException e) {
				e.printStackTrace();
				QuestManagerPlugin.questManagerPlugin.getLogger().warning(
						"Error when parsing quest configuration file: " 
						+ questConfigFile.getAbsolutePath());
				continue;
			}
			
			
			questTemplates.add(questTemplate);
			
			//get quest static npcs
			if (!questTemplate.getAuxNPCs().isEmpty())
			for (NPC np : questTemplate.getAuxNPCs()) {
				questNPCs.add(np);
			}
			
			//now instantiate starting NPC associated ot this quest
			NPC npc = questTemplate.GetStartingNPCInstance();
			if (npc != null) {
				questNPCs.add(npc);
			}
			
			
//			try {
//				registerQuest(questTemplate.instanceQuest(this));
//
//			} catch (InvalidConfigurationException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			
		}
		
		//check if there is any state information for this manager
		if (saveDirectory.listFiles().length != 0) {
			QuestManagerPlugin.questManagerPlugin.getLogger().info("Quest Manager fetching state "
					+ "information...");	
			
			
			//files are [name]_[id]
			for (File stateFile : saveDirectory.listFiles()) {
				String questName = stateFile.getName().substring(0, 
						stateFile.getName().indexOf("_"));
				
				QuestConfiguration template = getQuestTemplate(questName);
				Quest quest;
				try {
					quest = template.instanceQuest(null);
					
				} catch (InvalidConfigurationException e) {
					e.printStackTrace();
					
					//remove it?
					if (!QuestManagerPlugin.questManagerPlugin.getPluginConfiguration().getKeepOnError()) {
						stateFile.delete();
					} else {
						QuestManagerPlugin.questManagerPlugin.getLogger().info("Ignoring invalid config.");
					}
					
					continue;
				}
				
				QuestState state = new QuestState();
				YamlConfiguration config = new YamlConfiguration();
				
				
				try {
					config.load(stateFile);
					state.load(config);
				} catch (Exception e) {
					e.printStackTrace();
					continue;
				} 
				
				try {
					quest.loadState(state);
					//if successfull, remove state info so we don't duplicate
					
					QuestManagerPlugin.questManagerPlugin.getLogger().info(
							"Successfully loaded state information for quest!");
					registerQuest(quest);
					
					stateFile.delete();
					
				} catch (InvalidConfigurationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					continue;
				}
				
				
				
						
			}
			

			QuestManagerPlugin.questManagerPlugin.getLogger().info("Quest Manager finished!");	
			
		}
	}
	
	/**
	 * Registers the quest, providing no frequency information.<br />
	 * Quests without frequency information are given equal chances of being
	 * produced when requesting a random quest.
	 */
	public void registerQuest(Quest quest) {
		if (quest == null) {
			System.out.println("error!");
		}
		runningQuests.add(quest);
	}
	
	public void removeQuest(Quest quest) {
		if (quest == null) {
			System.out.println("error!");
		}
		runningQuests.remove(quest);
	}
	
	
	/**
	 * Passes a stop signal to all quest managers, requesting a soft stop.<br />
	 * Soft stops typically save state and perform a padded stopping procedure,
	 * and are not guaranteed to stop all runningQuests.
	 */
	public void stopQuests() {
		
		if (runningQuests != null && !runningQuests.isEmpty()) {
			
			QuestManagerPlugin.questManagerPlugin.getLogger().info(
					"Stopping quests and saving state information for " + runningQuests.size() +
					" quests...");
			
			for (Quest quest : runningQuests) {
				quest.stop();
			}
			
			QuestManagerPlugin.questManagerPlugin.getLogger().info("done!");
		}
		
		//remove starting NPCs
		if (!questNPCs.isEmpty()) {
			for (NPC npc : questNPCs) {
				if (npc.getEntity() != null) {
					npc.getEntity().remove();
				}
			}
		}
	}
	
	/**
	 * Immediately halts all running runningQuests.
	 */
	public void haltQuests() {
		if (runningQuests != null && !runningQuests.isEmpty()) {
			for (Quest quest : runningQuests) {
				quest.halt();
			}
		}
	}
	
	
	public File getSaveLocation() {
		return saveDirectory;
	}

	
	public Scoreboard getScoreboard() {
		return this.scoreboard;
	}
	
	/**
	 * Looks up the matching quest config based on name.
	 * This manager does not look beyond the quests allowed to it
	 * @param questName 
	 * @return the Quest Configuration used as a template, or null if it wasn't found
	 */
	public QuestConfiguration getQuestTemplate(String questName) {
		if (questTemplates.isEmpty()) {
			return null;
		}
		
		for (QuestConfiguration qc : questTemplates) {
			if (qc.getName().equals(questName)) {
				return qc;
			}
		}
		
		return null;
	}
	
	@EventHandler
	public void onItemPickup(PlayerPickupItemEvent e) {
		
		if (e.isCancelled()) {
			return;
		}
		
		ItemStack item = e.getItem().getItemStack();
		
		if (item.getType().equals(Material.WRITTEN_BOOK)) {
			BookMeta meta = (BookMeta) item.getItemMeta();
			
			if (meta.getTitle().equals("Quest Log")) {
				e.getItem().remove();
				e.setCancelled(true);
				
				QuestManagerPlugin.questManagerPlugin.getPlayerManager().getPlayer(
						e.getPlayer().getUniqueId()).addQuestBook();
				
				QuestManagerPlugin.questManagerPlugin.getPlayerManager().getPlayer(
						e.getPlayer().getUniqueId()).updateQuestBook();
			}
		}
		
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerChangedWorldEvent e) {
		if (QuestManagerPlugin.questManagerPlugin.getPluginConfiguration().getWorlds().contains(
				e.getPlayer().getWorld().getName())) {
			//if they're coming to a quest world, make sure we have a player for them
			QuestManagerPlugin.questManagerPlugin.getPlayerManager().getPlayer(
					e.getPlayer().getUniqueId());
		}
	}
	
	@EventHandler
	public void onCraft(CraftItemEvent e) {
		if (QuestManagerPlugin.questManagerPlugin.getPluginConfiguration().getAllowCrafting()) {
			return;
		}
		
		if (e.getWhoClicked() instanceof Player) {
			Player p = (Player) e.getWhoClicked();
			Location loc = p.getLocation();
			
			if (QuestManagerPlugin.questManagerPlugin.getPluginConfiguration()
					.getWorlds().contains(loc.getWorld().getName())) {
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onEntityDeath(EntityDeathEvent e) {
		
		if (!QuestManagerPlugin.questManagerPlugin.getPluginConfiguration().getAdjustXP()) {
			return;
		}
		
		String world = e.getEntity().getWorld().getName();
		
		if (!QuestManagerPlugin.questManagerPlugin.getPluginConfiguration()
					.getWorlds().contains(world)) {
			return;
		}
		
		
		if (e.getEntity().getCustomName() != null)
		if (e.getEntity().getKiller() != null)
		if (e.getEntity().getCustomName().contains("(Lvl ")) {
			//level'ed entity!
			String cache = e.getEntity().getCustomName();
			int pos = cache.indexOf("(Lvl ");
			//advance pos by 5 to get the number
			pos += 5;
			String tail = cache.substring(pos);
			int length = 0;
			for (char c : tail.toCharArray()) {
				if (Character.isDigit(c)) {
					length += 1;
				} else {
					break;
				}
			}
			
			if (length == 0) {
				System.out.println("Error when finding level! Expected a number, got:  " + tail.charAt(0));
				return;
			}
			
			String lvl = tail.substring(0, length);
			int level = Integer.valueOf(lvl);
			level = (level-1) / 3; //1,2,3 are 0, 4,5,6 are 1, etc
			level +=1; 			   //1,2,3 are 1, 5,6,7 are 2, etc
			
			e.setDroppedExp(level);
		}
	}
	
	@EventHandler
	public void onTame(EntityTameEvent e) {
		if (e.isCancelled()) {
			return;
		}
		
		if (QuestManagerPlugin.questManagerPlugin.getPluginConfiguration().getAllowTaming()) {
			return;
		}
		
		String worldname = e.getEntity().getWorld().getName();
		if (!QuestManagerPlugin.questManagerPlugin.getPluginConfiguration().getWorlds().contains(worldname)) {
			return;
		}
		
		if (e.getOwner() instanceof Player) {
			((Player) e.getOwner()).sendMessage(ChatColor.DARK_PURPLE + "Taming is not allowed here!" + ChatColor.RESET);
		}
		e.setCancelled(true);
		
	}
	
	@EventHandler
	public void onChat(AsyncPlayerChatEvent e) {
		if (e.isCancelled()) {
			return;
		}
		
		if (!QuestManagerPlugin.questManagerPlugin.getPluginConfiguration().getChatTitle()) {
			//if no, check worlds
			if (!QuestManagerPlugin.questManagerPlugin.getPluginConfiguration().getWorlds()
					.contains(e.getPlayer().getWorld().getName())) {
				return;
			}
		}
		
		QuestPlayer qp = QuestManagerPlugin.questManagerPlugin.getPlayerManager().getPlayer(e.getPlayer());
		if (qp.getTitle() == null || qp.getTitle().trim().isEmpty()) {
			return;
		}
		
		String msg = "[" + qp.getTitle() + "] " + e.getMessage();
		e.setMessage(msg);
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onInventoryClick(InventoryClickEvent e){
	
		if (e.isCancelled() || !(e.getWhoClicked() instanceof Player)) {
			return;
		}
		
		if (!(e.getInventory() instanceof AnvilInventory)) {
			return;
		}
		
		Player p = (Player) e.getWhoClicked();
		if (!QuestManagerPlugin.questManagerPlugin.getPluginConfiguration().getWorlds().contains(
				p.getWorld().getName())) {
			return;
		}
		AnvilInventory inv = (AnvilInventory) e.getInventory();
		
		int rawSlot = e.getSlot();
		
		if(rawSlot != 2){
			return;
		}
		if (inv.getItem(2) == null) {
			return;
		}
		//trying to finish it. Just compare name from slot 0 to slot 2 and make sure the same
		ItemStack left = inv.getItem(0);
		ItemStack right = inv.getItem(2);
		
		//first check: left has nothing, right should have nothing
		if ( (!left.hasItemMeta() && right.hasItemMeta() && right.getItemMeta().hasDisplayName())) {
			e.setCancelled(true);
			return;
		}
		//second check: the name has changed
		if (left.hasItemMeta() && left.getItemMeta().hasDisplayName()) { //we odn't need to make sure the right does cause it always will
			if (!left.getItemMeta().getDisplayName().equals(right.getItemMeta().getDisplayName())) {
				e.setCancelled(true);
				return;
			}
		}
		//last check: left has meta, no name but right does
		if (left.hasItemMeta() && !left.getItemMeta().hasDisplayName() && right.getItemMeta().hasDisplayName()) {
			e.setCancelled(true);
			return;
		}
	}
	
}
