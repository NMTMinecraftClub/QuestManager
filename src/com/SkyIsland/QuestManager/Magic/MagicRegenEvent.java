package com.SkyIsland.QuestManager.Magic;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MagicRegenEvent extends Event implements Cancellable {

	private static final HandlerList handlers = new HandlerList();
	
	private MagicUser entity;
	
	private int amount;
	
	private boolean cancelled;
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
	
	/**
	 * 
	 * @param entity
	 * @param amount
	 */
	public MagicRegenEvent(MagicUser entity, int amount) {
		this.entity = entity;
		this.amount = amount;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public MagicUser getEntity() {
		return entity;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean arg0) {
		this.cancelled = arg0;
	}
	
	

}
