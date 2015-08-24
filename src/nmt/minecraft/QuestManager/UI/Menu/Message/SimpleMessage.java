package nmt.minecraft.QuestManager.UI.Menu.Message;

import java.util.HashMap;
import java.util.Map;

import nmt.minecraft.QuestManager.Fanciful.FancyMessage;

import org.bukkit.ChatColor;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

/**
 * Wraps arounds a simple, single -use- message.<br />
 * @author Skyler
 *
 */
public class SimpleMessage extends Message {

	/**
	 * Registers this class as configuration serializable with all defined 
	 * {@link aliases aliases}
	 */
	public static void registerWithAliases() {
		for (aliases alias : aliases.values()) {
			ConfigurationSerialization.registerClass(SimpleMessage.class, alias.getAlias());
		}
	}
	
	/**
	 * Registers this class as configuration serializable with only the default alias
	 */
	public static void registerWithoutAliases() {
		ConfigurationSerialization.registerClass(SimpleMessage.class);
	}
	

	private enum aliases {
		DEFAULT(SimpleMessage.class.getName()),
		SIMPLE("SimpleMessage");
		
		private String alias;
		
		private aliases(String alias) {
			this.alias = alias;
		}
		
		public String getAlias() {
			return alias;
		}
	}
	
	
	private FancyMessage message;
	
	private FancyMessage label;
	
	private SimpleMessage() {
		super();
	}
	
	
	public SimpleMessage(FancyMessage msg) {
		this.message = msg;
	}
	
	public SimpleMessage(String msg) {
		this.message = new FancyMessage(msg);
	}
	
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<String, Object>();
		
		
		map.put("text", message);
		
		return map;
	}
	
	public static SimpleMessage valueOf(Map<String, Object> map) {
		Object obj = map.get("text");
		
		SimpleMessage msg = new SimpleMessage();
		
		if (obj instanceof FancyMessage) {
			msg.message = (FancyMessage) obj;
			return msg;
		}
		
		//else just assume it's a string!?
		msg.message = new FancyMessage((String) obj);
		return msg;
	}

	
	@Override
	public void setSourceLabel(FancyMessage label) {
		this.label = label;
	}

	@Override
	public FancyMessage getFormattedMessage() {
		return new FancyMessage("")
		.then(label == null ? 
				new FancyMessage("Unknown")	: label)
			.color(ChatColor.DARK_GRAY)
			.style(ChatColor.BOLD)
		.then(":\n")
		.then(message);	
		
	}
	
	
	
}
