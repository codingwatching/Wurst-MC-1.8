/*
 * Copyright � 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods;

import net.minecraft.entity.Entity;
import net.wurstclient.events.listeners.UpdateListener;
import net.wurstclient.utils.EntityUtils;
import net.wurstclient.utils.PlayerUtils;
import net.wurstclient.utils.RotationUtils;
import net.wurstclient.utils.EntityUtils.TargetSettings;

@Mod.Info(
	description = "A bot that follows the closest entity and protects it from other entities.\n"
		+ "Use .protect <entity> to protect a specific entity instead of the closest one.",
	name = "Protect",
	help = "Mods/Protect")
@Mod.Bypasses(ghostMode = false)
@Mod.DontSaveState
public final class ProtectMod extends Mod implements UpdateListener
{
	private Entity friend;
	private Entity enemy;
	private float range = 6F;
	private double distanceF = 2D;
	private double distanceE = 3D;
	
	private final TargetSettings friendSettingsFind = new TargetSettings()
	{
		@Override
		public boolean targetFriends()
		{
			return true;
		}
		
		@Override
		public float getRange()
		{
			return range;
		}
	};
	
	private final TargetSettings friendSettingsKeep = new TargetSettings()
	{
		@Override
		public boolean targetFriends()
		{
			return true;
		}
		
		@Override
		public boolean targetBehindWalls()
		{
			return true;
		}
		
		@Override
		public boolean targetPlayers()
		{
			return true;
		}
		
		@Override
		public boolean targetAnimals()
		{
			return true;
		}
		
		@Override
		public boolean targetMonsters()
		{
			return true;
		}
		
		@Override
		public boolean targetGolems()
		{
			return true;
		}
		
		@Override
		public boolean targetSleepingPlayers()
		{
			return true;
		}
		
		@Override
		public boolean targetInvisiblePlayers()
		{
			return true;
		}
		
		@Override
		public boolean targetInvisibleMobs()
		{
			return true;
		}
		
		@Override
		public boolean targetTeams()
		{
			return false;
		}
	};
	
	private final TargetSettings enemySettings = new TargetSettings()
	{
		@Override
		public float getRange()
		{
			return range;
		}
	};
	
	@Override
	public String getRenderName()
	{
		if(friend != null)
			return "Protecting " + friend.getName();
		else
			return "Protect";
	}
	
	@Override
	public void onEnable()
	{
		// set friend
		friend = EntityUtils.getClosestEntity(friendSettingsFind);
		
		wurst.events.add(UpdateListener.class, this);
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(UpdateListener.class, this);
		
		if(friend != null)
			mc.gameSettings.keyBindForward.pressed = false;
	}
	
	@Override
	public void onUpdate()
	{
		// check if player died, friend died or friend disappeared
		if(mc.player.getHealth() <= 0
			|| !EntityUtils.isCorrectEntity(friend, friendSettingsKeep))
		{
			friend = null;
			enemy = null;
			setEnabled(false);
			return;
		}
		
		// set enemy
		enemy = EntityUtils.getClosestEntityOtherThan(friend, enemySettings);
		
		// jump if necessary
		if(mc.player.isCollidedHorizontally && mc.player.onGround)
			mc.player.jump();
		
		// swim up if necessary
		if(mc.player.isInWater()
			&& mc.player.posY < (enemy != null ? enemy.posY : friend.posY))
			mc.player.motionY += 0.04;
		
		// update timer
		updateMS();
		
		if(enemy == null)
		{
			// follow friend
			RotationUtils.faceEntityClient(friend);
			mc.gameSettings.keyBindForward.pressed =
				mc.player.getDistanceToEntity(friend) > distanceF;
		}else
		{
			// follow enemy
			RotationUtils.faceEntityClient(enemy);
			mc.gameSettings.keyBindForward.pressed =
				mc.player.getDistanceToEntity(enemy) > distanceE;
			
			// check timer / cooldown
			if(wurst.mods.killauraMod.useCooldown != null
				&& wurst.mods.killauraMod.useCooldown.isChecked()
					? PlayerUtils.getCooldown() < 1
					: !hasTimePassedS(wurst.mods.killauraMod.speed.getValueF()))
				return;
			
			// prepare attack
			EntityUtils.prepareAttack();
			
			// attack enemy
			EntityUtils.attackEntity(enemy);
			
			// reset timer
			updateLastMS();
		}
	}
	
	public void setFriend(Entity friend)
	{
		this.friend = friend;
	}
}
