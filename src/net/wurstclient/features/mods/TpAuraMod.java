/*
 * Copyright � 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods;

import java.util.Random;

import net.minecraft.entity.Entity;
import net.wurstclient.WurstClient;
import net.wurstclient.events.listeners.UpdateListener;
import net.wurstclient.features.Feature;
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.settings.SliderSetting.ValueDisplay;
import net.wurstclient.utils.EntityUtils;
import net.wurstclient.utils.PlayerUtils;
import net.wurstclient.utils.RotationUtils;
import net.wurstclient.utils.EntityUtils.TargetSettings;

@Mod.Info(
	description = "Automatically attacks the closest valid entity while teleporting around it.",
	name = "TP-Aura",
	tags = "TpAura, EnderAura, Ender-Aura, tp aura, ender aura",
	help = "Mods/TP-Aura")
@Mod.Bypasses(ghostMode = false,
	latestNCP = false,
	olderNCP = false,
	antiCheat = false)
public final class TpAuraMod extends Mod implements UpdateListener
{
	private Random random = new Random();
	
	public CheckboxSetting useKillaura =
		new CheckboxSetting("Use Killaura settings", true)
		{
			@Override
			public void update()
			{
				if(isChecked())
				{
					KillauraMod killaura = wurst.mods.killauraMod;
					
					if(useCooldown != null)
						useCooldown.lock(killaura.useCooldown);
					
					speed.lock(killaura.speed);
					range.lock(killaura.range);
					fov.lock(killaura.fov);
					hitThroughWalls.lock(killaura.hitThroughWalls);
				}else
				{
					if(useCooldown != null)
						useCooldown.unlock();
					
					speed.unlock();
					range.unlock();
					fov.unlock();
					hitThroughWalls.unlock();
				}
			}
		};
	public CheckboxSetting useCooldown =
		WurstClient.MINECRAFT_VERSION.equals("1.8") ? null
			: new CheckboxSetting("Use Attack Cooldown as Speed", true)
			{
				@Override
				public void update()
				{
					speed.setDisabled(isChecked());
				}
			};
	public SliderSetting speed =
		new SliderSetting("Speed", 20, 0.1, 20, 0.1, ValueDisplay.DECIMAL);
	public SliderSetting range =
		new SliderSetting("Range", 6, 1, 6, 0.05, ValueDisplay.DECIMAL);
	// TODO: Does it even make sense to have an FOV setting for this?
	public SliderSetting fov =
		new SliderSetting("FOV", 360, 30, 360, 10, ValueDisplay.DEGREES);
	public CheckboxSetting hitThroughWalls =
		new CheckboxSetting("Hit through walls", false);
	
	private TargetSettings targetSettings = new TargetSettings()
	{
		@Override
		public boolean targetBehindWalls()
		{
			return hitThroughWalls.isChecked();
		}
		
		@Override
		public float getRange()
		{
			return range.getValueF();
		}
		
		@Override
		public float getFOV()
		{
			return fov.getValueF();
		}
	};
	
	@Override
	public void initSettings()
	{
		settings.add(useKillaura);
		
		if(useCooldown != null)
			settings.add(useCooldown);
		
		settings.add(speed);
		settings.add(range);
		settings.add(fov);
		settings.add(hitThroughWalls);
	}
	
	@Override
	public Feature[] getSeeAlso()
	{
		return new Feature[]{wurst.special.targetSpf, wurst.mods.killauraMod,
			wurst.mods.killauraLegitMod, wurst.mods.multiAuraMod,
			wurst.mods.clickAuraMod, wurst.mods.triggerBotMod};
	}
	
	@Override
	public void onEnable()
	{
		// disable other killauras
		wurst.mods.killauraMod.setEnabled(false);
		wurst.mods.killauraLegitMod.setEnabled(false);
		wurst.mods.multiAuraMod.setEnabled(false);
		wurst.mods.clickAuraMod.setEnabled(false);
		wurst.mods.triggerBotMod.setEnabled(false);
		
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
		// update timer
		updateMS();
		
		// set entity
		Entity entity = EntityUtils.getClosestEntity(targetSettings);
		if(entity == null)
			return;
		
		// teleport
		mc.player.setPosition(entity.posX + random.nextInt(3) * 2 - 2,
			entity.posY, entity.posZ + random.nextInt(3) * 2 - 2);
		
		// check timer / cooldown
		if(useCooldown != null && useCooldown.isChecked()
			? PlayerUtils.getCooldown() < 1
			: !hasTimePassedS(speed.getValueF()))
			return;
		
		// prepare attack
		EntityUtils.prepareAttack();
		
		// face entity
		RotationUtils.faceEntityPacket(entity);
		
		// attack entity
		EntityUtils.attackEntity(entity);
		
		// reset timer
		updateLastMS();
	}
}
