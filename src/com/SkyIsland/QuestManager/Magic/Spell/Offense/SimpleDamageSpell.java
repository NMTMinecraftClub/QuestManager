package com.SkyIsland.QuestManager.Magic.Spell.Offense;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

import com.SkyIsland.QuestManager.Magic.MagicUser;
import com.SkyIsland.QuestManager.Magic.Spell.SpellProjectile;
import com.SkyIsland.QuestManager.Magic.Spell.TargetSpell;

public class SimpleDamageSpell extends TargetSpell {

	private double damage;
	
	private double speed;
	
	private int maxDistance;
	
	private Effect projectileEffect;
	
	private Effect contactEffect;
	
	public SimpleDamageSpell(int cost, String name, String description, double damage, double speed,
			int maxDistance, Effect projectileEffect, Effect contactEffect) {
		super(cost, name, description);
		this.damage = damage;
		this.speed = speed;
		this.maxDistance = maxDistance;
		this.projectileEffect = projectileEffect;
		this.contactEffect = contactEffect;
	}

	@Override
	public void cast(MagicUser caster, Vector direction) {
		new SpellProjectile(this, caster, caster.getEntity().getLocation(), 
			caster.getEntity().getLocation().getDirection(), speed, maxDistance, projectileEffect);
	}

	@Override
	protected void onBlockHit(MagicUser caster, Location loc) {
		//Do nothing
	}

	@Override
	protected void onEntityHit(MagicUser caster, LivingEntity target) {
		//do damage
		target.damage(damage, caster.getEntity());
		target.getWorld().playEffect(target.getEyeLocation(), contactEffect, 0);
	}

}
