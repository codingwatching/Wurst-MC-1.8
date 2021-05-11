/*
 * Copyright � 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraft.client.gui.GuiIngameMenu;
import net.wurstclient.navigator.gui.NavigatorScreen;

@Mod.Info(
	description = "Allows you to walk while viewing a menu (e.g. the inventory\n"
		+ "menu).",
	name = "MenuWalk",
	tags = "InventoryWalk, menu walk, inventory walk",
	help = "Mods/MenuWalk")
@Mod.Bypasses
public final class MenuWalkMod extends Mod
{
	public boolean shouldAllowWalking()
	{
		// check if mod is active
		if(!isActive())
			return false;
		
		// check if there is a player to move
		if(mc.player == null)
			return false;
		
		// check if player is viewing chat
		if(mc.currentScreen instanceof GuiChat
			|| mc.currentScreen instanceof GuiIngameMenu
			|| mc.currentScreen instanceof GuiGameOver
			|| mc.currentScreen instanceof NavigatorScreen)
			return false;
		
		// check if inventory key is pressed
		if(Keyboard.isKeyDown(mc.gameSettings.keyBindInventory.getKeyCode()))
			return false;
		
		return true;
	}
}
