/*
 * Copyright � 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.commands;

import java.util.Iterator;

import net.wurstclient.files.ConfigFiles;
import net.wurstclient.utils.ChatUtils;
import net.wurstclient.utils.MiscUtils;

@Cmd.Info(description = "Manages your friends list.",
	name = "friends",
	syntax = {"(add | remove) <player>", "list [<page>]"})
public final class FriendsCmd extends Cmd
{
	@Override
	public void execute(String[] args) throws CmdError
	{
		if(args.length == 0)
			syntaxError();
		if(args[0].equalsIgnoreCase("list"))
		{
			if(args.length == 1)
			{
				execute(new String[]{"list", "1"});
				return;
			}
			int pages = (int)Math.ceil(wurst.friends.size() / 8D);
			if(MiscUtils.isInteger(args[1]))
			{
				int page = Integer.valueOf(args[1]);
				if(page > pages || page < 1)
					syntaxError();
				ChatUtils.message("Current friends: " + wurst.friends.size());
				ChatUtils
					.message("Friends list (page " + page + "/" + pages + "):");
				Iterator<String> itr = wurst.friends.iterator();
				for(int i = 0; itr.hasNext(); i++)
				{
					String friend = itr.next();
					if(i >= (page - 1) * 8 && i < (page - 1) * 8 + 8)
						ChatUtils.message(friend);
				}
			}else
				syntaxError();
		}else if(args.length < 2)
			syntaxError();
		else if(args[0].equalsIgnoreCase("add"))
		{
			if(wurst.friends.contains(args[1]))
			{
				ChatUtils.error(
					"\"" + args[1] + "\" is already in your friends list.");
				return;
			}
			wurst.friends.add(args[1]);
			ConfigFiles.FRIENDS.save();
			ChatUtils.message("Added friend \"" + args[1] + "\".");
		}else if(args[0].equalsIgnoreCase("remove"))
		{
			if(wurst.friends.remove(args[1]))
			{
				ConfigFiles.FRIENDS.save();
				ChatUtils.message("Removed friend \"" + args[1] + "\".");
			}else
				ChatUtils
					.error("\"" + args[1] + "\" is not in your friends list.");
		}else
			syntaxError();
	}
}
