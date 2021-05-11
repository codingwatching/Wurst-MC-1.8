/*
 * Copyright � 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods;

import net.minecraft.item.Item;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.wurstclient.events.LeftClickEvent;
import net.wurstclient.events.listeners.LeftClickListener;
import net.wurstclient.events.listeners.UpdateListener;
import net.wurstclient.features.Feature;
import net.wurstclient.utils.InventoryUtils;

@Mod.Info(
	description = "Automatically uses the best weapon in your hotbar to attack entities.\n"
		+ "Tip: This works with Killaura.",
	name = "AutoSword",
	tags = "auto sword",
	help = "Mods/AutoSword")
@Mod.Bypasses(ghostMode = false,
	latestNCP = false,
	olderNCP = false,
	antiCheat = false)
public final class AutoSwordMod extends Mod
	implements LeftClickListener, UpdateListener
{
	private int oldSlot = -1;
	private int timer;
	
	@Override
	public Feature[] getSeeAlso()
	{
		return new Feature[]{wurst.mods.autoToolMod, wurst.mods.killauraMod};
	}
	
	@Override
	public void onEnable()
	{
		wurst.events.add(LeftClickListener.class, this);
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(LeftClickListener.class, this);
		
		// reset slot
		if(oldSlot != -1)
		{
			mc.player.inventory.currentItem = oldSlot;
			oldSlot = -1;
		}
	}
	
	@Override
	public void onUpdate()
	{
		// update timer
		if(timer > 0)
		{
			timer--;
			return;
		}
		
		// reset slot
		if(oldSlot != -1)
		{
			mc.player.inventory.currentItem = oldSlot;
			oldSlot = -1;
		}
		wurst.events.remove(UpdateListener.class, this);
	}
	
	@Override
	public void onLeftClick(LeftClickEvent event)
	{
		// check hitResult
		if(mc.objectMouseOver == null || mc.objectMouseOver.entityHit == null)
			return;
		
		setSlot();
	}
	
	public void setSlot()
	{
		// check if active
		if(!isActive())
			return;
		
		// wait for AutoEat
		if(wurst.mods.autoEatMod.isEating())
			return;
		
		// find best weapon
		float bestDamage = 0;
		int bestSlot = -1;
		for(int i = 0; i < 9; i++)
		{
			// skip empty slots
			if(InventoryUtils.isSlotEmpty(i))
				continue;
			
			Item item = mc.player.inventory.getStackInSlot(i).getItem();
			
			// get damage
			float damage = 0;
			if(item instanceof ItemSword)
				damage = ((ItemSword)item).attackDamage;
			else if(item instanceof ItemTool)
				damage = ((ItemTool)item).damageVsEntity;
			
			// compare with previous best weapon
			if(damage > bestDamage)
			{
				bestDamage = damage;
				bestSlot = i;
			}
		}
		
		// check if any weapon was found
		if(bestSlot == -1)
			return;
		
		// save old slot
		if(oldSlot == -1)
			oldSlot = mc.player.inventory.currentItem;
		
		// set slot
		mc.player.inventory.currentItem = bestSlot;
		
		// start timer
		timer = 4;
		wurst.events.add(UpdateListener.class, this);
	}
}
