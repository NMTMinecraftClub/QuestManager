package nmt.minecraft.QuestManager.UI.Menu.Action;

import nmt.minecraft.QuestManager.QuestManagerPlugin;
import nmt.minecraft.QuestManager.Player.QuestPlayer;
import nmt.minecraft.QuestManager.UI.Menu.InventoryMenu;
import nmt.minecraft.QuestManager.UI.Menu.Inventory.GuiInventory;

public class OpenShopAction implements MenuAction {
	
	private GuiInventory inv;
	
	private QuestPlayer player;
	
	public OpenShopAction(QuestPlayer player, GuiInventory inv) {
		this.inv = inv;
		this.player = player;
	}
	
	@Override
	public void onAction() {
		InventoryMenu menu = new InventoryMenu(player, inv);
		QuestManagerPlugin.questManagerPlugin.getInventoryGuiHandler().showMenu(
				player.getPlayer().getPlayer(), menu);
	}

}
