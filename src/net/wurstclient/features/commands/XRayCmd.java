/*
 * Copyright � 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.commands;

import java.util.Iterator;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.wurstclient.features.mods.XRayMod;
import net.wurstclient.files.ConfigFiles;
import net.wurstclient.utils.ChatUtils;
import net.wurstclient.utils.MiscUtils;

@Cmd.Info(description = "Manages or toggles X-Ray.",
	name = "xray",
	syntax = {"add (id <block_id>|name <block_name>)",
		"remove (id <block_id>|name <block_name>)", "list [<page>]"})
public final class XRayCmd extends Cmd
{
	@Override
	public void execute(String[] args) throws CmdError
	{
		if(args.length == 0)
			syntaxError();
		else if(args[0].equalsIgnoreCase("list"))
		{
			if(args.length == 1)
			{
				execute(new String[]{"list", "1"});
				return;
			}
			int pages = (int)Math.ceil(XRayMod.xrayBlocks.size() / 8D);
			if(MiscUtils.isInteger(args[1]))
			{
				int page = Integer.valueOf(args[1]);
				if(page > pages || page < 1)
					syntaxError("Invalid page: " + page);
				ChatUtils.message(
					"Current X-Ray blocks: " + XRayMod.xrayBlocks.size());
				ChatUtils.message(
					"X-Ray blocks list (page " + page + "/" + pages + "):");
				Iterator<Block> itr = XRayMod.xrayBlocks.iterator();
				for(int i = 0; itr.hasNext(); i++)
				{
					Block block = itr.next();
					if(i >= (page - 1) * 8 && i < (page - 1) * 8 + 8)
						ChatUtils
							.message(new ItemStack(Item.getItemFromBlock(block))
								.getDisplayName());
				}
			}else
				syntaxError();
		}else if(args.length < 2)
			syntaxError();
		else if(args[0].equalsIgnoreCase("add"))
		{
			if(args[1].equalsIgnoreCase("id") && MiscUtils.isInteger(args[2]))
			{
				if(XRayMod.xrayBlocks
					.contains(Block.getBlockById(Integer.valueOf(args[2]))))
				{
					ChatUtils.error("\"" + args[2]
						+ "\" is already in your X-Ray blocks list.");
					return;
				}
				XRayMod.xrayBlocks
					.add(Block.getBlockById(Integer.valueOf(args[2])));
				ConfigFiles.XRAY.save();
				ChatUtils.message("Added block " + args[2] + ".");
				mc.renderGlobal.loadRenderers();
			}else if(args[1].equalsIgnoreCase("name"))
			{
				int newID =
					Block.getIdFromBlock(Block.getBlockFromName(args[2]));
				if(newID == -1)
				{
					ChatUtils.message(
						"The block \"" + args[1] + "\" could not be found.");
					return;
				}
				XRayMod.xrayBlocks.add(Block.getBlockById(newID));
				ConfigFiles.XRAY.save();
				ChatUtils.message(
					"Added block " + newID + " (\"" + args[2] + "\").");
				mc.renderGlobal.loadRenderers();
			}else
				syntaxError();
		}else if(args[0].equalsIgnoreCase("remove"))
		{
			if(args[1].equalsIgnoreCase("id") && MiscUtils.isInteger(args[2]))
			{
				for(int i = 0; i < XRayMod.xrayBlocks.size(); i++)
					if(Integer
						.toString(
							Block.getIdFromBlock(XRayMod.xrayBlocks.get(i)))
						.toLowerCase().equals(args[2].toLowerCase()))
					{
						XRayMod.xrayBlocks.remove(i);
						ConfigFiles.XRAY.save();
						ChatUtils.message("Removed block " + args[2] + ".");
						mc.renderGlobal.loadRenderers();
						return;
					}
				ChatUtils.error(
					"Block " + args[2] + " is not in your X-Ray blocks list.");
			}else if(args[1].equalsIgnoreCase("name"))
			{
				int newID =
					Block.getIdFromBlock(Block.getBlockFromName(args[2]));
				if(newID == -1)
				{
					ChatUtils.message(
						"The block \"" + args[2] + "\" could not be found.");
					return;
				}
				for(int i = 0; i < XRayMod.xrayBlocks.size(); i++)
					if(Block.getIdFromBlock(XRayMod.xrayBlocks.get(i)) == newID)
					{
						XRayMod.xrayBlocks.remove(i);
						ConfigFiles.XRAY.save();
						ChatUtils.message("Removed block " + newID + " (\""
							+ args[2] + "\").");
						mc.renderGlobal.loadRenderers();
						return;
					}
				ChatUtils.error("Block " + newID + " (\"" + args[2]
					+ "\") is not in your X-Ray blocks list.");
			}else
				syntaxError();
		}else
			syntaxError();
	}
}
