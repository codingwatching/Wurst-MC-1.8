/*
 * Copyright � 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods;

import net.minecraft.network.play.client.CPacketPlayer;
import net.wurstclient.events.listeners.UpdateListener;
import net.wurstclient.utils.EntityUtils;
import net.wurstclient.utils.EntityUtils.TargetSettings;

@Mod.Info(
	description = "Pushes mobs like crazy.\n" + "They'll literally fly away!\n"
		+ "Can sometimes get you kicked for \"Flying is not enabled\".",
	name = "ForcePush",
	tags = "force push",
	help = "Mods/ForcePush")
@Mod.Bypasses
public final class ForcePushMod extends Mod implements UpdateListener
{
	private TargetSettings targetSettings = new TargetSettings()
	{
		@Override
		public boolean targetBehindWalls()
		{
			return true;
		};
		
		@Override
		public float getRange()
		{
			return 1F;
		}
	};
	
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
		if(mc.player.onGround
			&& EntityUtils.getClosestEntity(targetSettings) != null)
			for(int i = 0; i < 1000; i++)
				mc.player.connection.sendPacket(new CPacketPlayer(true));
	}
}
