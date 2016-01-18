package com.SkyIsland.QuestManager.Enemy;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.CommandBlock;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.inventory.ItemStack;

import com.SkyIsland.QuestManager.QuestManagerPlugin;

/**
 * Enemy type with equipment customization in addition to NormalMob stuff
 * @author Skyler
 *
 */
public class StandardEnemy extends NormalEnemy {
	
	/**
	 * Registers this class as configuration serializable with all defined 
	 * {@link aliases aliases}
	 */
	public static void registerWithAliases() {
		for (aliases alias : aliases.values()) {
			ConfigurationSerialization.registerClass(StandardEnemy.class, alias.getAlias());
		}
	}
	
	/**
	 * Registers this class as configuration serializable with only the default alias
	 */
	public static void registerWithoutAliases() {
		ConfigurationSerialization.registerClass(StandardEnemy.class);
	}
	

	private enum aliases {
		DEFAULT(StandardEnemy.class.getName()),
		SIMPLE("StandardEnemy");
		
		private String alias;
		
		private aliases(String alias) {
			this.alias = alias;
		}
		
		public String getAlias() {
			return alias;
		}
	}
	
	private ItemStack head, chest, legs, boots, hands;
	
	public StandardEnemy(String name, String type, double hp, double attack) {
		this(name, type, hp, attack, null, null, null, null, null);
	}
	
	public StandardEnemy(String name, String type, double hp, double attack,
			ItemStack head, ItemStack chest, ItemStack legs, ItemStack boots, ItemStack hands) {
		super(name, type, hp, attack);
		this.head = head;
		this.chest = chest;
		this.legs = legs;
		this.boots = boots;
		this.hands = hands;
		
	}
	
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<>();
		
		map.put("type", type);
		map.put("name", name);
		map.put("hp", hp);
		map.put("attack", attack);
		map.put("head", head);
		map.put("chest", chest);
		map.put("legs", legs);
		map.put("boots", boots);
		map.put("hands", hands);
		
		return map;
	}
	
	public static StandardEnemy valueOf(Map<String, Object> map) {
		
		String type = (String) map.get("type");
		
		String name = (String) map.get("name");
		Double hp = (Double) map.get("hp");
		Double attack = (Double) map.get("attack");
		ItemStack head, chest, legs, boots, hands;
		head = chest = legs = boots = hands = null;
		
		if (map.containsKey("head")) {
			head = new ItemStack(Material.valueOf((String) map.get("head")));
		}
		
		if (map.containsKey("chest")) {
			chest = new ItemStack(Material.valueOf((String) map.get("chest")));
		}
		
		if (map.containsKey("legs")) {
			legs = new ItemStack(Material.valueOf((String) map.get("legs")));
		}
		
		if (map.containsKey("boots")) {
			boots = new ItemStack(Material.valueOf((String) map.get("boots")));
		}
		
		if (map.containsKey("hands")) {
			hands = new ItemStack(Material.valueOf((String) map.get("hands")));
		}
		
		return new StandardEnemy(name, type, hp, attack, head, chest, legs, boots, hands);
	}
	
	@Override
	public void spawn(Location loc) {
		
		String cmd = "summon "
				+ this.type + " " + loc.getX() + " " + loc.getY() + " " + loc.getZ() + " "
				+ "{CustomName:" + name + ",CustomNameVisible:1,Attributes:["
				+ "{Name:generic.maxHealth,Base:" + hp + "},"
				+ "{Name:generic.attackDamage,Base:" + attack + "}],"
				+ "Equipment:[" + equipmentString(hands) + "," + equipmentString(boots) + "," 
				+ equipmentString(legs) + "," + equipmentString(chest) + "," + equipmentString(head) 
				+ "],DropChances:[0.0F,0.0F,0.0F,0.0F,0.0F]}";
		
		CommandBlock sender = QuestManagerPlugin.questManagerPlugin.getManager().getAnchor(loc.getWorld().getName());
		Location ol = sender.getLocation().clone().add(0,1,0);
		sender.setCommand(cmd);
		ol.getBlock().setType(Material.REDSTONE_BLOCK);
		ol.getBlock().getState().update(true);
		sender.update(true);
		ol.getBlock().setType(Material.STONE);
		
	}
	
	@SuppressWarnings("deprecation")
	protected String equipmentString(ItemStack equip) {
		if (equip == null || equip.getType() == Material.AIR) {
			return "{}";
		}
		
		return "{Count:1,id:" + equip.getTypeId() + "}";
	}
	
}
