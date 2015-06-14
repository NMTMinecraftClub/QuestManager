package nmt.minecraft.QuestManager.UI.Menu.Message;

import nmt.minecraft.QuestManager.Fanciful.FancyMessage;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

/**
 * Holds a messaged used with a menu.
 * @author Skyler
 *
 */
public abstract class Message implements ConfigurationSerializable {
	
	public abstract FancyMessage getFormattedMessage();
	
}
