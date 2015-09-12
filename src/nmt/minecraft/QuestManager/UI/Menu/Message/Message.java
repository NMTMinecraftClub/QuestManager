package nmt.minecraft.QuestManager.UI.Menu.Message;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

import nmt.minecraft.QuestManager.Fanciful.FancyMessage;

/**
 * Holds a messaged used with a menu.
 * @author Skyler
 *
 */
public abstract class Message implements ConfigurationSerializable {
	
	public abstract void setSourceLabel(FancyMessage label);
	
	public abstract FancyMessage getFormattedMessage();
	
}
