/*
 * Copyright � 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.commands;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;

import net.wurstclient.events.ChatOutputEvent;
import net.wurstclient.hooks.ServerHook;
import net.wurstclient.utils.ChatUtils;

@Cmd.Info(
	description = "Shows the IP of the server you are currently playing on or copies it to the clipboard.",
	name = "ip",
	syntax = {"[copy]"})
public final class IpCmd extends Cmd
{
	@Override
	public void execute(String[] args) throws CmdError
	{
		if(args.length == 0)
			ChatUtils.message("IP: " + ServerHook.getCurrentServerIP());
		else if(args[0].toLowerCase().equals("copy"))
		{
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
				new StringSelection(ServerHook.getCurrentServerIP()), null);
			ChatUtils.message("IP copied to clipboard.");
		}else
			syntaxError();
	}
	
	@Override
	public String getPrimaryAction()
	{
		return "Get IP";
	}
	
	@Override
	public void doPrimaryAction()
	{
		wurst.commands.onSentMessage(new ChatOutputEvent(".ip", true));
	}
}
