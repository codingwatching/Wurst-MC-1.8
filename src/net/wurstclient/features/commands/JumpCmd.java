/*
 * Copyright � 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.commands;

import net.wurstclient.events.ChatOutputEvent;

@Cmd.Info(description = "Makes you jump once.", name = "jump", syntax = {})
public final class JumpCmd extends Cmd
{
	@Override
	public void execute(String[] args) throws CmdError
	{
		if(args.length != 0)
			syntaxError();
		if(!mc.player.onGround)
			error("Can't jump in mid-air.");
		mc.player.jump();
	}
	
	@Override
	public String getPrimaryAction()
	{
		return "Jump";
	}
	
	@Override
	public void doPrimaryAction()
	{
		wurst.commands.onSentMessage(new ChatOutputEvent(".jump", true));
	}
}
