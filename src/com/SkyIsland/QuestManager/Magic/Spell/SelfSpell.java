package com.SkyIsland.QuestManager.Magic.Spell;

import com.SkyIsland.QuestManager.Magic.MagicUser;

public abstract class SelfSpell extends Spell {
	
	protected SelfSpell(int cost, String name, String description) {
		super(cost, name, description);
	}
	
	public abstract void cast(MagicUser caster);
	
}
