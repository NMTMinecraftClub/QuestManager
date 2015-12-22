package com.SkyIsland.QuestManager.Magic.Spell.Offense;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

import com.SkyIsland.QuestManager.Magic.MagicUser;
import com.SkyIsland.QuestManager.Magic.Spell.SpellProjectile;
import com.SkyIsland.QuestManager.Magic.Spell.TargetSpell;

public class SimpleDamageSpell extends TargetSpell {

	private int damage;
	
	private double speed;
	
	private int maxDistance;
	
	protected SimpleDamageSpell(int cost, String name, String description, int damage, double speed,
			int maxDistance) {
		super(cost, name, description);
		this.damage = damage;
		this.speed = speed;
		this.maxDistance = maxDistance;
	}

	@Override
	public void cast(MagicUser caster, Vector direction) {
		new SpellProjectile(this, caster, caster.getEntity().getLocation(), 
				caster.getEntity().getLocation().getDirection(), speed, maxDistance);
	}

	@Override
	protected void onBlockHit(MagicUser caster, Location loc) {
		//Do nothing
	}

	@Override
	protected void onEntityHit(MagicUser caster, LivingEntity target) {
		//do damage
		target.damage(damage, caster.getEntity());
	}

}
