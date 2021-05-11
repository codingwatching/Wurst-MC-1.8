/*
 * Copyright � 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods;

import org.lwjgl.opengl.GL11;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.wurstclient.events.listeners.RenderListener;
import net.wurstclient.features.Feature;
import net.wurstclient.utils.RenderUtils;

@Mod.Info(description = "Allows you to see fake blocks in Prophunt.",
	name = "ProphuntESP",
	tags = "prophunt esp",
	help = "Mods/ProphuntESP")
@Mod.Bypasses
public final class ProphuntEspMod extends Mod implements RenderListener
{
	private static final AxisAlignedBB FAKE_BLOCK_BOX =
		new AxisAlignedBB(-0.5, 0, -0.5, 0.5, 1, 0.5);
	
	@Override
	public Feature[] getSeeAlso()
	{
		return new Feature[]{wurst.mods.playerEspMod, wurst.mods.mobEspMod,
			wurst.mods.tracersMod};
	}
	
	@Override
	public void onEnable()
	{
		wurst.events.add(RenderListener.class, this);
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(RenderListener.class, this);
	}
	
	@Override
	public void onRender(float partialTicks)
	{
		// GL settings
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glEnable(GL11.GL_LINE_SMOOTH);
		GL11.glLineWidth(2);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		
		GL11.glPushMatrix();
		GL11.glTranslated(-mc.getRenderManager().renderPosX,
			-mc.getRenderManager().renderPosY,
			-mc.getRenderManager().renderPosZ);
		
		// set color
		float alpha = 0.5F + 0.25F * MathHelper
			.sin(System.currentTimeMillis() % 1000 / 500F * (float)Math.PI);
		GL11.glColor4f(1, 0, 0, alpha);
		
		// draw boxes
		for(Entity entity : mc.world.loadedEntityList)
		{
			if(!(entity instanceof EntityLiving))
				continue;
			
			if(!entity.isInvisible())
				continue;
			
			if(mc.player.getDistanceSqToEntity(entity) < 0.25)
				continue;
			
			GL11.glPushMatrix();
			GL11.glTranslated(entity.posX, entity.posY, entity.posZ);
			
			RenderUtils.drawOutlinedBox(FAKE_BLOCK_BOX);
			RenderUtils.drawSolidBox(FAKE_BLOCK_BOX);
			
			GL11.glPopMatrix();
		}
		
		GL11.glPopMatrix();
		
		// GL resets
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_LINE_SMOOTH);
	}
}
