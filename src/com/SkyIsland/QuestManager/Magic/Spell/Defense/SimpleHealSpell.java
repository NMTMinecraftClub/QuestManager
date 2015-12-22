package com.SkyIsland.QuestManager.Magic.Spell.Defense;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;

import com.SkyIsland.QuestManager.Effects.ChargeEffect;
import com.SkyIsland.QuestManager.Effects.QuestEffect;
import com.SkyIsland.QuestManager.Magic.MagicUser;
import com.SkyIsland.QuestManager.Magic.Spell.SelfSpell;

public class SimpleHealSpell extends SelfSpell {
	
	private double amount;
	
	private Effect castEffect;
	
	private Sound castSound;
	
	public SimpleHealSpell(int cost, String name, String description, double amount) {
		super(cost, name, description);
		this.amount = amount;
		castEffect = null;
		castSound = null;
	}
	
	public void setCastEffect(Effect castEffect) {
		this.castEffect = castEffect;
	}

	public void setCastSound(Sound castSound) {
		this.castSound = castSound;
	}

	@Override
	public void cast(MagicUser caster) {
		
		QuestEffect ef = new ChargeEffect(Effect.WITCH_MAGIC);
		ef.play(caster.getEntity(), null);
		
		if (caster.getEntity() instanceof LivingEntity) {
			LivingEntity e = (LivingEntity) caster.getEntity();
			EntityRegainHealthEvent event = new EntityRegainHealthEvent(e, amount, RegainReason.MAGIC);
			Bukkit.getPluginManager().callEvent(event);
			
			if (event.isCancelled()) {
				return;
			}
			
			e.setHealth(Math.min(e.getMaxHealth(), 
			e.getHealth() + amount));
			
			if (castEffect != null) {
				e.getWorld().playEffect(e.getEyeLocation(), castEffect, 0);
			}
			if (castSound != null) {
				e.getWorld().playSound(e.getLocation(), castSound, 1, 1);
			}
		}
	}
	
}
