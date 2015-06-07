package nmt.minecraft.QuestManager.Configuration;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

/**
 * Convenience class for storing equipment configuration.<br />
 * @author Skyler
 *
 */
public class EquipmentConfiguration {

	private ItemStack head;
	
	private ItemStack chest;
	
	private ItemStack legs;
	
	private ItemStack boots;
	
	private ItemStack held;
	
	public EquipmentConfiguration() {
		; //default everything to null;
	}
	
	public EquipmentConfiguration(ItemStack head, ItemStack chest, ItemStack legs, 
			ItemStack boots, ItemStack held) {
		this.head = head;
		this.chest = chest;
		this.legs = legs;
		this.boots = boots;
		this.held = held;
	}
	
	public EquipmentConfiguration(EntityEquipment equips) {
		this.head = equips.getHelmet();
		this.chest = equips.getChestplate();
		this.legs = equips.getLeggings();
		this.boots = equips.getBoots();
		this.held = equips.getItemInHand();
	}
	
	public void save(File file) throws IOException {
		
		YamlConfiguration state = new YamlConfiguration();
		
		//unique identification
		state.set("type", "ecnf");
		
		state.set("head", head);
		state.set("chest", chest);
		state.set("legs", legs);
		state.set("boots", boots);
		state.set("held", held);
		
		state.save(file);
	}
	
	public void load(YamlConfiguration config) throws InvalidConfigurationException {
		
		if (!config.contains("type") || !config.getString("type").equals("ecnf")) {
			throw new InvalidConfigurationException();
		}
		
		head = config.getItemStack("head");
		chest = config.getItemStack("chest");
		legs = config.getItemStack("legs");
		boots = config.getItemStack("boots");
		held = config.getItemStack("held");
		
		
	}
}
