/*
 * Copyright � 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.commands;

import net.wurstclient.utils.ChatUtils;

@Cmd.Info(description = "Enables/disables Wurst messages or sends a message.",
	name = "wms",
	syntax = {"(on | off)", "echo <message>"})
public final class WmsCmd extends Cmd
{
	@Override
	public void execute(String[] args) throws CmdError
	{
		if(args.length == 0)
			syntaxError();
		if(args[0].equalsIgnoreCase("on") || args[0].equalsIgnoreCase("off"))
			ChatUtils.setEnabled(args[0].equalsIgnoreCase("on"));
		else if(args[0].equalsIgnoreCase("echo") && args.length == 2)
		{
			String message = args[1];
			for(int i = 2; i < args.length; i++)
				message += " " + args[i];
			ChatUtils.cmd(message);
		}else
			syntaxError();
	}
}
