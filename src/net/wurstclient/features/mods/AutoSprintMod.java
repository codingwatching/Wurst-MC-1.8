/*
 * Copyright � 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods;

import net.wurstclient.events.listeners.UpdateListener;

@Mod.Info(description = "Makes you sprint whenever you walk.",
	name = "AutoSprint",
	tags = "auto sprint",
	help = "Mods/AutoSprint")
@Mod.Bypasses
public final class AutoSprintMod extends Mod implements UpdateListener
{
	@Override
	public void onEnable()
	{
		wurst.events.add(UpdateListener.class, this);
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(UpdateListener.class, this);
	}
	
	@Override
	public void onUpdate()
	{
		if(!mc.player.isCollidedHorizontally && mc.player.moveForward > 0
			&& !mc.player.isSneaking())
			mc.player.setSprinting(true);
	}
}
