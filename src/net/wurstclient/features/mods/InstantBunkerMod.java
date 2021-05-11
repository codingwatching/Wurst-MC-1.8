/*
 * Copyright � 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.material.Material;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.wurstclient.events.listeners.RenderListener;
import net.wurstclient.events.listeners.UpdateListener;
import net.wurstclient.features.special_features.YesCheatSpf.BypassLevel;
import net.wurstclient.utils.BlockUtils;
import net.wurstclient.utils.PlayerUtils;
import net.wurstclient.utils.RenderUtils;

@Mod.Info(description = "Instantly builds a small bunker around you.",
	name = "InstantBunker",
	tags = "instant bunker",
	help = "Mods/InstantBunker")
@Mod.Bypasses
public final class InstantBunkerMod extends Mod
	implements UpdateListener, RenderListener
{
	private int[][] template = {{2, 0, 2}, {2, 0, 1}, {2, 0, 0}, {2, 0, -1},
		{2, 0, -2}, {1, 0, 2}, {1, 0, -2}, {0, 0, 2}, {0, 0, -2}, {-1, 0, 2},
		{-1, 0, -2}, {-2, 0, 2}, {-2, 0, 1}, {-2, 0, 0}, {-2, 0, -1},
		{-2, 0, -2}, {2, 1, 2}, {2, 1, 1}, {2, 1, 0}, {2, 1, -1}, {2, 1, -2},
		{1, 1, 2}, {1, 1, -2}, {0, 1, 2}, {0, 1, -2}, {-1, 1, 2}, {-1, 1, -2},
		{-2, 1, 2}, {-2, 1, 1}, {-2, 1, 0}, {-2, 1, -1}, {-2, 1, -2}, {2, 2, 2},
		{2, 2, 1}, {2, 2, 0}, {2, 2, -1}, {2, 2, -2}, {1, 2, 2}, {1, 2, 1},
		{1, 2, 0}, {1, 2, -1}, {1, 2, -2}, {0, 2, 2}, {0, 2, 1}, {0, 2, 0},
		{0, 2, -1}, {0, 2, -2}, {-1, 2, 2}, {-1, 2, 1}, {-1, 2, 0}, {-1, 2, -1},
		{-1, 2, -2}, {-2, 2, 2}, {-2, 2, 1}, {-2, 2, 0}, {-2, 2, -1},
		{-2, 2, -2}};
	
	private int blockIndex;
	private boolean building;
	private final ArrayList<BlockPos> positions = new ArrayList<>();
	
	@Override
	public void onEnable()
	{
		// initialize
		// get start pos and facings
		BlockPos startPos = new BlockPos(mc.player);
		EnumFacing facing = mc.player.getHorizontalFacing();
		EnumFacing facing2 = facing.rotateYCCW();
		
		// set positions
		positions.clear();
		for(int[] pos : template)
			positions.add(startPos.up(pos[1]).offset(facing, pos[2])
				.offset(facing2, pos[0]));
		
		if(wurst.special.yesCheatSpf.getBypassLevel()
			.ordinal() >= BypassLevel.ANTICHEAT.ordinal())
		{
			// initialize building process
			blockIndex = 0;
			building = true;
			mc.rightClickDelayTimer = 4;
		}
		
		wurst.events.add(UpdateListener.class, this);
		wurst.events.add(RenderListener.class, this);
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(UpdateListener.class, this);
		wurst.events.remove(RenderListener.class, this);
		building = false;
	}
	
	@Override
	public void onUpdate()
	{
		// build instantly
		if(!building)
		{
			for(BlockPos pos : positions)
				if(BlockUtils.getMaterial(pos) == Material.AIR)
					BlockUtils.placeBlockSimple(pos);
			PlayerUtils.swingArmClient();
			setEnabled(false);
			return;
		}
		
		// place next block
		if(blockIndex < positions.size() && (mc.rightClickDelayTimer == 0
			|| wurst.mods.fastPlaceMod.isActive()))
		{
			BlockPos pos = positions.get(blockIndex);
			
			if(BlockUtils.getMaterial(pos) == Material.AIR)
				BlockUtils.placeBlockLegit(pos);
			else
			{
				blockIndex++;
				if(blockIndex == positions.size())
				{
					building = false;
					setEnabled(false);
				}
			}
		}
	}
	
	@Override
	public void onRender(float partialTicks)
	{
		if(!building || blockIndex >= positions.size())
			return;
		
		// scale and offset
		double scale = 1D * 7D / 8D;
		double offset = (1D - scale) / 2D;
		
		// GL settings
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glEnable(GL11.GL_LINE_SMOOTH);
		GL11.glLineWidth(2F);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_CULL_FACE);
		
		GL11.glPushMatrix();
		GL11.glTranslated(-mc.getRenderManager().renderPosX,
			-mc.getRenderManager().renderPosY,
			-mc.getRenderManager().renderPosZ);
		
		// green box
		{
			GL11.glDepthMask(false);
			GL11.glColor4f(0F, 1F, 0F, 0.15F);
			BlockPos pos = positions.get(blockIndex);
			
			GL11.glPushMatrix();
			GL11.glTranslated(pos.getX(), pos.getY(), pos.getZ());
			GL11.glTranslated(offset, offset, offset);
			GL11.glScaled(scale, scale, scale);
			
			RenderUtils.drawSolidBox();
			
			GL11.glPopMatrix();
			GL11.glDepthMask(true);
		}
		
		// black outlines
		GL11.glColor4f(0F, 0F, 0F, 0.5F);
		for(int i = blockIndex; i < positions.size(); i++)
		{
			BlockPos pos = positions.get(i);
			
			GL11.glPushMatrix();
			GL11.glTranslated(pos.getX(), pos.getY(), pos.getZ());
			GL11.glTranslated(offset, offset, offset);
			GL11.glScaled(scale, scale, scale);
			
			RenderUtils.drawOutlinedBox();
			
			GL11.glPopMatrix();
		}
		
		GL11.glPopMatrix();
		
		// GL resets
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_LINE_SMOOTH);
	}
}
