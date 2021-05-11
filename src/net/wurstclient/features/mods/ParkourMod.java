/*
 * Copyright � 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods;

import net.wurstclient.WurstClient;
import net.wurstclient.events.listeners.UpdateListener;

@Mod.Info(
	description = "Makes you jump automatically when reaching the edge of a block.\n"
		+ "Useful for parkours, Jump'n'Runs, etc.",
	name = "Parkour")
@Mod.Bypasses
public final class ParkourMod extends Mod implements UpdateListener
{
	@Override
	public void onEnable()
	{
		WurstClient.INSTANCE.events.add(UpdateListener.class, this);
	}
	
	@Override
	public void onUpdate()
	{
		if(mc.player.onGround && !mc.player.isSneaking()
			&& mc.world
				.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox()
					.offset(0, -0.5, 0).contract(0.001, 0, 0.001))
				.isEmpty())
			mc.player.jump();
	}
	
	@Override
	public void onDisable()
	{
		WurstClient.INSTANCE.events.remove(UpdateListener.class, this);
	}
}
