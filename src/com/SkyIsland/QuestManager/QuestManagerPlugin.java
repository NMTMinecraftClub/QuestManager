package com.SkyIsland.QuestManager;


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

import com.SkyIsland.QuestManager.Configuration.PluginConfiguration;
import com.SkyIsland.QuestManager.Configuration.Utils.Chest;
import com.SkyIsland.QuestManager.Configuration.Utils.LocationState;
import com.SkyIsland.QuestManager.Enemy.DefaultEnemy;
import com.SkyIsland.QuestManager.Enemy.NormalEnemy;
import com.SkyIsland.QuestManager.Enemy.StandardEnemy;
import com.SkyIsland.QuestManager.Fanciful.FancyMessage;
import com.SkyIsland.QuestManager.Fanciful.MessagePart;
import com.SkyIsland.QuestManager.Fanciful.TextualComponent;
import com.SkyIsland.QuestManager.Magic.SummonManager;
import com.SkyIsland.QuestManager.Magic.Spell.SimpleSelfSpell;
import com.SkyIsland.QuestManager.Magic.Spell.SimpleTargetSpell;
import com.SkyIsland.QuestManager.Magic.Spell.SpellManager;
import com.SkyIsland.QuestManager.Magic.Spell.Effect.AreaEffect;
import com.SkyIsland.QuestManager.Magic.Spell.Effect.BlockEffect;
import com.SkyIsland.QuestManager.Magic.Spell.Effect.DamageEffect;
import com.SkyIsland.QuestManager.Magic.Spell.Effect.DamageMPEffect;
import com.SkyIsland.QuestManager.Magic.Spell.Effect.DamageUndeadEffect;
import com.SkyIsland.QuestManager.Magic.Spell.Effect.FireEffect;
import com.SkyIsland.QuestManager.Magic.Spell.Effect.HealEffect;
import com.SkyIsland.QuestManager.Magic.Spell.Effect.StatusEffect;
import com.SkyIsland.QuestManager.Magic.Spell.Effect.SummonTamedEffect;
import com.SkyIsland.QuestManager.Magic.Spell.Effect.SwapEffect;
import com.SkyIsland.QuestManager.NPC.DummyNPC;
import com.SkyIsland.QuestManager.NPC.ForgeNPC;
import com.SkyIsland.QuestManager.NPC.InnNPC;
import com.SkyIsland.QuestManager.NPC.LevelupNPC;
import com.SkyIsland.QuestManager.NPC.MuteNPC;
import com.SkyIsland.QuestManager.NPC.ServiceNPC;
import com.SkyIsland.QuestManager.NPC.ShopNPC;
import com.SkyIsland.QuestManager.NPC.SimpleBioptionNPC;
import com.SkyIsland.QuestManager.NPC.SimpleChatNPC;
import com.SkyIsland.QuestManager.NPC.SimpleQuestStartNPC;
import com.SkyIsland.QuestManager.NPC.TeleportNPC;
import com.SkyIsland.QuestManager.NPC.Utils.ServiceCraft;
import com.SkyIsland.QuestManager.NPC.Utils.ServiceOffer;
import com.SkyIsland.QuestManager.Player.Party;
import com.SkyIsland.QuestManager.Player.QuestPlayer;
import com.SkyIsland.QuestManager.Quest.Quest;
import com.SkyIsland.QuestManager.Quest.Requirements.ArriveRequirement;
import com.SkyIsland.QuestManager.Quest.Requirements.ChestRequirement;
import com.SkyIsland.QuestManager.Quest.Requirements.CountdownRequirement;
import com.SkyIsland.QuestManager.Quest.Requirements.DeliverRequirement;
import com.SkyIsland.QuestManager.Quest.Requirements.InteractRequirement;
import com.SkyIsland.QuestManager.Quest.Requirements.PositionRequirement;
import com.SkyIsland.QuestManager.Quest.Requirements.PossessRequirement;
import com.SkyIsland.QuestManager.Quest.Requirements.SlayRequirement;
import com.SkyIsland.QuestManager.Quest.Requirements.TalkRequirement;
import com.SkyIsland.QuestManager.Quest.Requirements.TimeRequirement;
import com.SkyIsland.QuestManager.Quest.Requirements.VanquishRequirement;
import com.SkyIsland.QuestManager.Region.CuboidRegion;
import com.SkyIsland.QuestManager.Region.RegionManager;
import com.SkyIsland.QuestManager.Region.SphericalRegion;
import com.SkyIsland.QuestManager.UI.ChatGuiHandler;
import com.SkyIsland.QuestManager.UI.InventoryGuiHandler;
import com.SkyIsland.QuestManager.UI.Menu.Action.PartyInviteAction;
import com.SkyIsland.QuestManager.UI.Menu.Inventory.ServiceInventory;
import com.SkyIsland.QuestManager.UI.Menu.Inventory.ShopInventory;
import com.SkyIsland.QuestManager.UI.Menu.Message.BioptionMessage;
import com.SkyIsland.QuestManager.UI.Menu.Message.SimpleMessage;
import com.SkyIsland.QuestManager.UI.Menu.Message.TreeMessage;

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
	
	private RegionManager regionManager;
	
	private SpellManager spellManager;
	
	private SummonManager summonManager;
	
	private QuestManager manager;
	
	private ChatGuiHandler chatGuiHandler;
	
	private InventoryGuiHandler inventoryGuiHandler;
	
	private PluginConfiguration config;
	
	private File saveDirectory;
	
	private File questDirectory;
	
	private File enemyDirectory;
	
	private File spellDirectory;
	
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
		
		enemyDirectory = new File(getDataFolder(), config.getEnemyPath());
		if (!enemyDirectory.exists()) {
			enemyDirectory.mkdirs();
		}
		
		spellDirectory = new File(getDataFolder(), config.getSpellPath());
		if (!spellDirectory.exists()) {
			spellDirectory.mkdirs();
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
		reqManager.registerFactory("INTERACT", 
				new InteractRequirement.InteractFactory());
		reqManager.registerFactory("CHEST", 
				new ChestRequirement.ChestRequirementFactory());
		reqManager.registerFactory("TALK", 
				new TalkRequirement.TalkRequirementFactory());
		
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
		TreeMessage.registerWithAliases();
		ShopInventory.registerWithAliases();
		ServiceInventory.registerWithAliases();
		ServiceCraft.registerWithAliases();
		ServiceOffer.registerWithAliases();
		ServiceNPC.registerWithAliases();
		LevelupNPC.registerWithAliases();
		DummyNPC.registerWithAliases();
		ConfigurationSerialization.registerClass(MessagePart.class);
		ConfigurationSerialization.registerClass(TextualComponent.ArbitraryTextTypeComponent.class);
		ConfigurationSerialization.registerClass(TextualComponent.ComplexTextTypeComponent.class);
		ConfigurationSerialization.registerClass(FancyMessage.class);
		Chest.registerWithAliases();
		CuboidRegion.registerWithAliases();
		SphericalRegion.registerWithAliases();
		DefaultEnemy.registerWithAliases();
		NormalEnemy.registerWithAliases();
		StandardEnemy.registerWithAliases();
		SimpleSelfSpell.registerWithAliases();
		SimpleTargetSpell.registerWithAliases();
		HealEffect.registerWithAliases();
		DamageEffect.registerWithAliases();
		StatusEffect.registerWithAliases();
		BlockEffect.registerWithAliases();
		AreaEffect.registerWithAliases();
		DamageMPEffect.registerWithAliases();
		SwapEffect.registerWithAliases();
		SummonTamedEffect.registerWithAliases();
		FireEffect.registerWithAliases();
		DamageUndeadEffect.registerWithAliases();

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
				saveDirectory);
		
		manager.init();
		
		regionManager = new RegionManager(enemyDirectory, 3);

		spellManager = new SpellManager(spellDirectory);
		
		summonManager = new SummonManager();
		
//		///////////////////////////////////////////////////////////////////////////////
//					
//		Vector v1 = new Vector(-600, 5, -800),
//		v2 = new Vector(-605, 3, -805);
//		Region r = new SphericalRegion(Bukkit.getWorld("QuestWorld"), v1, 4);
//		Enemy e = new DefaultEnemy(EntityType.ZOMBIE);
//		
//		regionManager.registerRegion(r);
//		regionManager.addEnemy(r, e);
//		
//		e = new DefaultEnemy(EntityType.SKELETON);
//		regionManager.addEnemy(r, e);
//		
//		///////////////////////////////////////////////////////////////////////////////		
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
		summonManager.removeSummons();
		
		
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
			qp.addJournal();
			
			
			/////////////////////////
//			SimpleTargetSpell FS = new SimpleTargetSpell(5, "Fireball", "inplace", 5.0, 15);
//			FS.addSpellEffect(new DamageEffect(5.0));
//			FS.setProjectileEffect(Effect.MOBSPAWNER_FLAMES);
//			FS.setContactEffect(Effect.EXPLOSION_LARGE);
//			FS.setCastSound(Sound.GHAST_FIREBALL);
//			
//			FS.cast(qp, qp.getPlayer().getPlayer().getLocation().getDirection());
//			
//			SimpleSelfSpell HS = new SimpleSelfSpell(4, "Heal", "temp");
//			HS.addSpellEffect(new HealEffect(3.0));
//			HS.setCastEffect(Effect.HAPPY_VILLAGER);
//			HS.setCastSound(Sound.ORB_PICKUP);
//			
//			HS.cast(qp);
			
			/////////////////////////
			return true;
		}
		
		if (cmd.getName().equals("qhistory")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage("Only players can use this command!");
				return true;
			}
			if (args.length != 1) {
				return false;
			}
			
			QuestPlayer qp = playerManager.getPlayer((OfflinePlayer) sender);
			int id;
			try {
				id = Integer.parseInt(args[0]);
			} catch (NumberFormatException e) {
				return false;
			}
			
			for (Quest q : qp.getCurrentQuests()) {
				if (q.getID() == id) {
					qp.setFocusQuest(q.getName());
					return true;
				}
			}
			
			return false;
		}
		
		if (cmd.getName().equals("qcomp")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage("Only players can use this command!");
				return true;
			}
			
			
			if (args.length == 0) {
				//no args, just reset compass?
				QuestPlayer qp = playerManager.getPlayer((OfflinePlayer) sender);
				qp.updateCompass(false);
				return true;
			}
			return false;
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
				if (p.getPlayer() == null || p.getPlayer().getName() == null) {
					continue;
				}
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
	
	public RegionManager getEnemyManager() {
		return regionManager;
	}
	
	public SpellManager getSpellManager() {
		return spellManager;
	}
	
	public SummonManager getSummonManager() {
		return summonManager;
	}
}
