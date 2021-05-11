/*
 * Copyright � 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods;

import net.wurstclient.events.listeners.UpdateListener;

@Mod.Info(
	description = "Exploits a bug in NoCheat+ that allows you to glitch\n"
		+ "through blocks.",
	name = "Phase",
	tags = "Phaze",
	help = "Mods/Phase")
@Mod.Bypasses
public final class PhaseMod extends Mod implements UpdateListener
{
	@Override
	public void onEnable()
	{
		wurst.events.add(UpdateListener.class, this);
	}
	
	@Override
	public void onUpdate()
	{
		mc.player.noClip = true;
		mc.player.fallDistance = 0;
		mc.player.onGround = true;
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(UpdateListener.class, this);
		mc.player.noClip = false;
	}
}
