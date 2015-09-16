package nmt.minecraft.QuestManager;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import nmt.minecraft.QuestManager.Configuration.PluginConfiguration;
import nmt.minecraft.QuestManager.Configuration.Utils.LocationState;
import nmt.minecraft.QuestManager.Fanciful.FancyMessage;
import nmt.minecraft.QuestManager.Fanciful.MessagePart;
import nmt.minecraft.QuestManager.Fanciful.TextualComponent;
import nmt.minecraft.QuestManager.NPC.ForgeNPC;
import nmt.minecraft.QuestManager.NPC.InnNPC;
import nmt.minecraft.QuestManager.NPC.MuteNPC;
import nmt.minecraft.QuestManager.NPC.ShopNPC;
import nmt.minecraft.QuestManager.NPC.SimpleBioptionNPC;
import nmt.minecraft.QuestManager.NPC.SimpleChatNPC;
import nmt.minecraft.QuestManager.NPC.SimpleQuestStartNPC;
import nmt.minecraft.QuestManager.NPC.TeleportNPC;
import nmt.minecraft.QuestManager.Player.Party;
import nmt.minecraft.QuestManager.Player.QuestPlayer;
import nmt.minecraft.QuestManager.Quest.Requirements.ArriveRequirement;
import nmt.minecraft.QuestManager.Quest.Requirements.CountdownRequirement;
import nmt.minecraft.QuestManager.Quest.Requirements.DeliverRequirement;
import nmt.minecraft.QuestManager.Quest.Requirements.PositionRequirement;
import nmt.minecraft.QuestManager.Quest.Requirements.PossessRequirement;
import nmt.minecraft.QuestManager.Quest.Requirements.SlayRequirement;
import nmt.minecraft.QuestManager.Quest.Requirements.TimeRequirement;
import nmt.minecraft.QuestManager.Quest.Requirements.VanquishRequirement;
import nmt.minecraft.QuestManager.UI.ChatGuiHandler;
import nmt.minecraft.QuestManager.UI.InventoryGuiHandler;
import nmt.minecraft.QuestManager.UI.Menu.Action.PartyInviteAction;
import nmt.minecraft.QuestManager.UI.Menu.Inventory.GuiInventory;
import nmt.minecraft.QuestManager.UI.Menu.Message.BioptionMessage;
import nmt.minecraft.QuestManager.UI.Menu.Message.SimpleMessage;

/**
 * Provided API and Command Line interaction between enlisted quest managers and
 * the user. <br />
 * 
 * @author Skyler
 * @todo Figure out where QuestManagers are going to be created. Through a
 * command? If so, how do you specify which quests for which manager? Do you
 * go through every single one and add it? Maybe instead through configs? 
 * What do the configs need? Maybe world name, list of quest names? How do
 * we look up quests by quest name? [next step]
 */
public class QuestManagerPlugin extends JavaPlugin {
	
	public static QuestManagerPlugin questManagerPlugin;
	
	private RequirementManager reqManager;
	
	private PlayerManager playerManager;
	
	private QuestManager manager;
	
	private ChatGuiHandler chatGuiHandler;
	
	private InventoryGuiHandler inventoryGuiHandler;
	
	private PluginConfiguration config;
	
	private File saveDirectory;
	
	private File questDirectory;
	
	private final static String configFileName = "QuestManagerConfig.yml";
	
	private final static String playerConfigFileName = "players.yml";
	
	public static final double version = 1.00;
	
	@Override
	public void onLoad() {
		QuestManagerPlugin.questManagerPlugin = this;
		reqManager = new RequirementManager();
		
		//load up config
		File configFile = new File(getDataFolder(), configFileName);
		
		config = new PluginConfiguration(configFile);		
				
		//perform directory checks
		saveDirectory = new File(getDataFolder(), config.getSavePath());
		if (!saveDirectory.exists()) {
			saveDirectory.mkdirs();
		}
		
		questDirectory = new File(getDataFolder(), config.getQuestPath());
		if (!questDirectory.exists()) {
			questDirectory.mkdirs();
		}
	
		//register our own requirements
		reqManager.registerFactory("ARRIVE", 
				new ArriveRequirement.ArriveFactory());
		reqManager.registerFactory("POSITION", 
				new PositionRequirement.PositionFactory());
		reqManager.registerFactory("POSSESS", 
				new PossessRequirement.PossessFactory());
		reqManager.registerFactory("VANQUISH", 
				new VanquishRequirement.VanquishFactory());
		reqManager.registerFactory("SLAY", 
				new SlayRequirement.SlayFactory());
		reqManager.registerFactory("DELIVER", 
				new DeliverRequirement.DeliverFactory());
		reqManager.registerFactory("TIME", 
				new TimeRequirement.TimeFactory());
		reqManager.registerFactory("COUNTDOWN", 
				new CountdownRequirement.CountdownFactory());
		
	}
	
	@Override
	public void onEnable() {
		//register our Location util!
		LocationState.registerWithAliases();
		QuestPlayer.registerWithAliases();
		Party.registerWithAliases();
		MuteNPC.registerWithAliases();
		SimpleChatNPC.registerWithAliases();
		SimpleBioptionNPC.registerWithAliases();
		SimpleQuestStartNPC.registerWithAliases();
		InnNPC.registerWithAliases();
		ForgeNPC.registerWithAliases();
		ShopNPC.registerWithAliases();
		TeleportNPC.registerWithAliases();
		SimpleMessage.registerWithAliases();
		BioptionMessage.registerWithAliases();
		GuiInventory.registerWithAliases();
		ConfigurationSerialization.registerClass(MessagePart.class);
		ConfigurationSerialization.registerClass(TextualComponent.ArbitraryTextTypeComponent.class);
		ConfigurationSerialization.registerClass(TextualComponent.ComplexTextTypeComponent.class);
		ConfigurationSerialization.registerClass(FancyMessage.class);

		chatGuiHandler = new ChatGuiHandler(this, config.getMenuVerbose());
		inventoryGuiHandler = new InventoryGuiHandler();
		
		
		//preload Player data
				File playerFile = new File(getDataFolder(), playerConfigFileName);
				if (!playerFile.exists()) {
					try {
						YamlConfiguration tmp = new YamlConfiguration();
						tmp.createSection("players");
						tmp.createSection("parties");
						tmp.save(playerFile);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				//get Player data & manager
				YamlConfiguration playerConfig = new YamlConfiguration();
				try {
					playerConfig.load(playerFile);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvalidConfigurationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				playerManager = new PlayerManager(playerConfig);
		
		
		//parse config & instantiate manager
		manager = new QuestManager(
				questDirectory, 
				saveDirectory,
				config.getQuests());
					
		
		
	}
	
	@Override
	public void onDisable() {
		
		//unregister our scheduler
		Bukkit.getScheduler().cancelTasks(this);
		
		for (Party party : playerManager.getParties()) {
			party.disband();
		}
		
		//save user database
		playerManager.save(new File(getDataFolder(), playerConfigFileName));
		stopAllQuests();
		
		
	}
	
	public void onReload() {
		onDisable();
		
		HandlerList.unregisterAll(this);
		
		onLoad();
		onEnable();
	}
	
	
	/**
	 * Attempts to softly stop all running quest managers and quests.<br />
	 * Quest managers (and underlying quests) may not be able to stop softly,
	 * and this method is not guaranteed to stop all quests (<i>especially</i>
	 * immediately).
	 */
	public void stopAllQuests() {
		if (manager == null) {
			return;
		}
	
		manager.stopQuests();
	}
	
	/**
	 * Performs a hard stop to all quests.<br />
	 * Quests that are halted are not expected to perform any sort of save-state
	 * procedure, not halt in a particularly pretty manner. <br />
	 * Halting a quest <i>guarantees</i> that it will stop immediately upon
	 * receipt of the halt notice.
	 */
	public void haltAllQuests() {
		
		if (manager == null) {
			return;
		}
		
		manager.haltQuests();
		
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("QuestManager")) {
			if (args.length == 0) {
				return false;
			}
			
			if (args[0].equals("reload")) {
				if (args.length == 1) {
					getLogger().info("Reloading QuestManager...");
					sender.sendMessage(ChatColor.DARK_BLUE + "Reloading QuestManager..." + ChatColor.RESET);
					onReload();
					getLogger().info("Done");
					sender.sendMessage(ChatColor.DARK_BLUE + "Done" + ChatColor.RESET);
					return true;
				}
				if (args[1].equals("villager") || args[1].equals("villagers")) {
					
					sender.sendMessage(ChatColor.DARK_GRAY + "Resetting villagers..." + ChatColor.RESET);
					getManager().resetNPCs();
					sender.sendMessage(ChatColor.DARK_GRAY + "Done!" + ChatColor.RESET);
					return true;
				}
				
			}
			
		}
		
		if (cmd.getName().equals("questlog")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage("Only players can use this command!");
				return true;
			}
			
			QuestPlayer qp = playerManager.getPlayer((OfflinePlayer) sender);
			
			qp.addQuestBook();
			return true;
		}
		
		if (cmd.getName().equals("party")) {
			
			if (args.length == 0) {
				return false;
			}
			if (!(sender instanceof Player)) {
				sender.sendMessage("This command must be executed by a player!");
				return false;
			}
			
			QuestPlayer qp = playerManager.getPlayer((OfflinePlayer) sender);
			if (qp.getParty() == null) {
				sender.sendMessage("You're not in a party!");
				return true;
			}
			
			String msg = ChatColor.DARK_GREEN + "[Party]" + ChatColor.RESET +  "<" + sender.getName() + "> ";
			for (String part : args) {
				msg += part + " ";
			}
			qp.getParty().tellMembers(msg);
			return true;
		}
		
		if (cmd.getName().equals("leave")) {
			
			if (!(sender instanceof Player)) {
				sender.sendMessage("This command must be executed by a player!");
				return false;
			}
			
			QuestPlayer qp = playerManager.getPlayer((OfflinePlayer) sender);
			
			if (qp.getParty() == null) {
				sender.sendMessage("You are not in a party!");
				return true;
			}
			
			qp.getParty().removePlayer(qp, ChatColor.YELLOW + "You left the party"+ ChatColor.RESET);
			return true;
		}
		
		if (cmd.getName().equals("boot")) {
			if (args.length == 0) {
				return false;
			}
			if (!(sender instanceof Player)) {
				sender.sendMessage("This command must be executed by a player!");
				return false;
			}
			
			QuestPlayer qp = playerManager.getPlayer((OfflinePlayer) sender);
			
			if (qp.getParty() == null) {
				sender.sendMessage("You are not in a party!");
				return true;
			}
			
			Party party = qp.getParty();
			
			if (party.getLeader().getIDString().equals(qp.getIDString())) {
				//can boot people
				QuestPlayer other = null;
				for (QuestPlayer op : party.getMembers()) {
					if (op.getPlayer().getName().equals(args[0])) {
						other = op;
						break;
					}
				}
				
				if (other == null) {
					sender.sendMessage(ChatColor.DARK_RED + "Unable to find the player " + ChatColor.BLUE + args[0]
							+ ChatColor.DARK_RED + " in your party!" + ChatColor.RESET);
					return true;
				}
				
				party.removePlayer(other, ChatColor.DARK_RED + "You've been kicked from the party" + ChatColor.RESET);
				return true;
			} else {
				//not leader, can't boot
				sender.sendMessage(ChatColor.DARK_RED + "You are not the leader of the party, and cannot boot people!" + ChatColor.RESET);
				return true;
			}
			
		}
		
		if (cmd.getName().equals("invite")) {
			if (args.length == 0) {
				return false;
			}
			if (!(sender instanceof Player)) {
				sender.sendMessage("This command must be executed by a player!");
				return false;
			}
			
			QuestPlayer qp = playerManager.getPlayer((OfflinePlayer) sender);
			
			if (qp.getParty() != null) {
				
				//are they the leader?
				Party party = qp.getParty();
				if (!party.getLeader().getIDString().equals(qp.getIDString())) {
					//not the leader, can't invite people
					sender.sendMessage(ChatColor.DARK_RED + "Only the party leader can invite new members!" + ChatColor.RESET);
					return true;
				}
			}
			
			//to get here, either is leader or not in a party
			QuestPlayer other = null;
			for (QuestPlayer p : playerManager.getPlayers()) {
				if (p.getPlayer().getName().equals(args[0])) {
					other = p;
					break;
				}
			}
			
			if (other == null) {
				sender.sendMessage(ChatColor.DARK_RED + "Unable to find the player "
						+ ChatColor.BLUE + args[0] + ChatColor.RESET);
				return true;
			}
			
			(new PartyInviteAction(qp, other)).onAction();
			
			return true;
		}
		
		if (cmd.getName().equals("player")) {
			if (args.length == 0) {
				return false;
			}
			if (!(sender instanceof Player)) {
				sender.sendMessage("This command must be executed by a player!");
				return false;
			}
			
			QuestPlayer qp = playerManager.getPlayer((OfflinePlayer) sender);
			
			if (args[0].equals("title")) {
				qp.showTitleMenu();
				return true;
			}
		}
		
		return false;
	}
	
	public RequirementManager getRequirementManager() {
		return this.reqManager;
	}
	
	public PluginConfiguration getPluginConfiguration() {
		return this.config;
	}
	
	public PlayerManager getPlayerManager() {
		return playerManager;
	}
	
	public ChatGuiHandler getChatGuiHandler() {
		return chatGuiHandler;
	}
	
	public InventoryGuiHandler getInventoryGuiHandler() {
		return inventoryGuiHandler;
	}
	
	public QuestManager getManager() {
		return manager;
	}
	
}
