package nmt.minecraft.QuestManager.NPC.Utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.inventory.ItemStack;

import nmt.minecraft.QuestManager.Fanciful.FancyMessage;

public class ServiceTrade implements ConfigurationSerializable {
	

	/**
	 * Registers this class as configuration serializable with all defined 
	 * {@link aliases aliases}
	 */
	public static void registerWithAliases() {
		for (aliases alias : aliases.values()) {
			ConfigurationSerialization.registerClass(ServiceTrade.class, alias.getAlias());
		}
	}
	
	/**
	 * Registers this class as configuration serializable with only the default alias
	 */
	public static void registerWithoutAliases() {
		ConfigurationSerialization.registerClass(ServiceTrade.class);
	}
	

	private enum aliases {
		FULL("nmt.minecraft.QuestManager.NPC.ServiceTrade"),
		DEFAULT(ServiceTrade.class.getName()),
		SHORT("ServiceTrade"),
		INFORMAL("TRADE");
		
		private String alias;
		
		private aliases(String alias) {
			this.alias = alias;
		}
		
		public String getAlias() {
			return alias;
		}
	}
	
	private String name;
	
	private int cost;
	
	private List<ItemStack> requiredItems;
	
	private List<ItemStack> rewardItems;
	
	public ServiceTrade(String name, int cost, List<ItemStack> requiredItems, List<ItemStack> rewardItems) {
		this.cost = cost;
		this.requiredItems = requiredItems;
		this.rewardItems = rewardItems;
	}
	
	public List<ItemStack> getRewards() {
		return rewardItems;
	}
	
	public List<ItemStack> getRequired() {
		return requiredItems;
	}
	
	public int getCost() {
		return cost;
	}
	
	public String getName() {
		return this.name;
	}
	
	@SuppressWarnings("unchecked")
	public static ServiceTrade valueOf(Map<String, Object> map) {
		if (map == null) {
			return null;
		}
		
		/*
		 * name: name of trade for tooltip
		 * cost: money cost
		 * requiredItems: 
		 * 	list of items
		 * rewardItems:
		 * 	list of items
		 */
		
		String name = (String) map.get("name");
		int cost = (int) map.get("cost");
		
		List<ItemStack> rewards = (List<ItemStack>) map.get("rewardItems");
		List<ItemStack> required = (List<ItemStack>) map.get("requiredItems");
		
		return new ServiceTrade(name, cost, required, rewards);
	}
	
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<String, Object>();
		
		map.put("name", name);
		map.put("cost", cost);
		
		map.put("requiredItems", requiredItems);
		map.put("rewardItems", rewardItems);
		
		return map;
	}
	
	/**
	 * Takes the trade and returns a summary couple of lines describing the trade in a standard way
	 * @return
	 */
	public FancyMessage getDescription() {
		FancyMessage msg = new FancyMessage("cost: ")
					.color(ChatColor.WHITE)
				.then(cost + "")
					.color(ChatColor.RED)
				.then("\n\n");
		if (requiredItems != null && !requiredItems.isEmpty()) {
			//print out required items
			Iterator<ItemStack> it = requiredItems.iterator();
			ItemStack item;
			while (it.hasNext()) {
				item = it.next();
				if (item.getAmount() > 1) {
					msg.then(item.getAmount() + " x ")
						.color(ChatColor.GRAY);
				}
				if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
					msg.then(item.getItemMeta().getDisplayName())
						.color(ChatColor.DARK_RED);
				} else {
					msg.then(item.getType().toString())
						.color(ChatColor.DARK_RED);
				}
				
				if (it.hasNext())
					msg.then("  +  ")
						.color(ChatColor.GRAY);
			}
			
			
			
			msg.then("  =  ")
				.color(ChatColor.GRAY);
			
			
		}
		
		if (rewardItems == null || rewardItems.isEmpty()) {
			return msg;
		}
		
		Iterator<ItemStack> it = rewardItems.iterator();
		ItemStack item;
		while (it.hasNext()) {
			item = it.next();
			if (item.getAmount() > 1) {
				msg.then(item.getAmount() + " x ")
					.color(ChatColor.GRAY);
			}
			if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
				msg.then(item.getItemMeta().getDisplayName())
					.color(ChatColor.DARK_GREEN);
			} else {
				msg.then(item.getType().toString())
					.color(ChatColor.DARK_GREEN);
			}
			
			if (it.hasNext())
				msg.then("  &  ")
					.color(ChatColor.GRAY);
		}
		
		return msg;
	}
}
