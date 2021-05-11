/*
 * Copyright � 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.commands;

import net.minecraft.item.ItemStack;
import net.wurstclient.utils.ChatUtils;

@Cmd.Info(
	description = "Renames the item in your hand. Use $ for colors, use $$ for $.",
	name = "rename",
	syntax = {"<new_name>"})
public final class RenameCmd extends Cmd
{
	@Override
	public void execute(String[] args) throws CmdError
	{
		if(!mc.player.capabilities.isCreativeMode)
			error("Creative mode only.");
		if(args.length == 0)
			syntaxError();
		String message = args[0];
		for(int i = 1; i < args.length; i++)
			message += " " + args[i];
		message = message.replace("$", "�").replace("��", "$");
		ItemStack item = mc.player.inventory.getCurrentItem();
		if(item == null)
			error("There is no item in your hand.");
		item.setStackDisplayName(message);
		ChatUtils.message("Renamed item to \"" + message + "�r\".");
	}
}
