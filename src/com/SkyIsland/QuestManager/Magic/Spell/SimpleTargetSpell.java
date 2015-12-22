package com.SkyIsland.QuestManager.Magic.Spell;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

import com.SkyIsland.QuestManager.Magic.MagicUser;
import com.SkyIsland.QuestManager.Magic.Spell.Effect.SpellEffect;

public class SimpleTargetSpell extends TargetSpell {
	
	private double speed;
	
	private int maxDistance;
	
	private Effect projectileEffect;
	
	private Effect contactEffect;
	
	private Sound castSound;
	
	private Sound contactSound;
	
	public SimpleTargetSpell(int cost, String name, String description, double speed,
			int maxDistance) {
		super(cost, name, description);
		this.speed = speed;
		this.maxDistance = maxDistance;
		this.projectileEffect = null;
		this.contactEffect = null;
		this.castSound = null;
		this.contactSound = null;
	}

	public void setProjectileEffect(Effect projectileEffect) {
		this.projectileEffect = projectileEffect;
	}



	public void setContactEffect(Effect contactEffect) {
		this.contactEffect = contactEffect;
	}



	public void setCastSound(Sound castSound) {
		this.castSound = castSound;
	}



	public void setContactSound(Sound contactSound) {
		this.contactSound = contactSound;
	}

	@Override
	public void cast(MagicUser caster, Vector direction) {
		new SpellProjectile(this, caster, caster.getEntity().getLocation().clone().add(0,1.5,0), 
			caster.getEntity().getLocation().getDirection(), speed, maxDistance, projectileEffect);

		if (castSound != null) {
			caster.getEntity().getWorld().playSound(caster.getEntity().getLocation(), castSound, 1, 1);
		}
	}

	@Override
	protected void onBlockHit(MagicUser caster, Location loc) {
		for (SpellEffect effect : getSpellEffects()) {
			effect.apply(loc);
		}
	}

	@Override
	protected void onEntityHit(MagicUser caster, LivingEntity target) {
		//do effects
		
		for (SpellEffect effect : getSpellEffects()) {
			effect.apply(target, caster.getEntity());
		}
		
		if (contactEffect != null) {
			target.getWorld().playEffect(target.getEyeLocation(), contactEffect, 0);
		}
		if (contactSound != null) {
			target.getWorld().playSound(target.getEyeLocation(), contactSound, 1, 1);
		}
	}

}
