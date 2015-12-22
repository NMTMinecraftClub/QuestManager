package com.SkyIsland.QuestManager.Magic.Spell;

import org.bukkit.Effect;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;

import com.SkyIsland.QuestManager.Effects.ChargeEffect;
import com.SkyIsland.QuestManager.Effects.QuestEffect;
import com.SkyIsland.QuestManager.Magic.MagicUser;
import com.SkyIsland.QuestManager.Magic.Spell.Effect.SpellEffect;

public class SimpleSelfSpell extends SelfSpell {
	
	private Effect castEffect;
	
	private Sound castSound;
	
	/**
	 * Creates a simple spell made to be cast on the self.<br />
	 * This spell will have no effects until added using 
	 * {@link #addSpellEffect(com.SkyIsland.QuestManager.Magic.Spell.Effect.SpellEffect)}
	 * @param cost
	 * @param name
	 * @param description
	 */
	public SimpleSelfSpell(int cost, String name, String description) {
		super(cost, name, description);
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
		
		Entity e = caster.getEntity();
		
		QuestEffect ef = new ChargeEffect(Effect.WITCH_MAGIC);
		ef.play(e, null);
		
//		if (caster.getEntity() instanceof LivingEntity) {
//			LivingEntity e = (LivingEntity) caster.getEntity();
//			EntityRegainHealthEvent event = new EntityRegainHealthEvent(e, amount, RegainReason.MAGIC);
//			Bukkit.getPluginManager().callEvent(event);
//			
//			if (event.isCancelled()) {
//				return;
//			}
//			
//			e.setHealth(Math.min(e.getMaxHealth(), 
//			e.getHealth() + amount));
			
		for (SpellEffect effect : getSpellEffects()) {
			effect.apply(e, e);
		}
		
		if (castEffect != null) {
			e.getWorld().playEffect(e.getLocation().clone().add(0,1.5,0), castEffect, 0);
		}
		if (castSound != null) {
			e.getWorld().playSound(e.getLocation().clone().add(0,1.5,0), castSound, 1, 1);
		}
	}
	
}
