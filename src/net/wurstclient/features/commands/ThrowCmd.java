/*
 * Copyright � 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.commands;

import net.wurstclient.files.ConfigFiles;
import net.wurstclient.utils.ChatUtils;
import net.wurstclient.utils.MiscUtils;

@Cmd.Info(description = "Changes the amount of Throw or toggles it.",
	name = "throw",
	syntax = {"[amount <amount>]"})
public final class ThrowCmd extends Cmd
{
	@Override
	public void execute(String[] args) throws CmdError
	{
		if(args.length == 0)
		{
			wurst.mods.throwMod.toggle();
			ChatUtils.message("Throw turned "
				+ (wurst.mods.throwMod.isEnabled() == true ? "on" : "off")
				+ ".");
		}else if(args.length == 2 && args[0].equalsIgnoreCase("amount")
			&& MiscUtils.isInteger(args[1]))
		{
			if(Integer.valueOf(args[1]) < 1)
			{
				ChatUtils.error("Throw amount must be at least 1.");
				return;
			}
			wurst.options.throwAmount = Integer.valueOf(args[1]);
			ConfigFiles.OPTIONS.save();
			ChatUtils.message("Throw amount set to " + args[1] + ".");
		}else
			syntaxError();
	}
}
