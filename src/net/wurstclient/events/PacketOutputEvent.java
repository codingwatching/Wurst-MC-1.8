/*
 * Copyright � 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.events;

import java.util.ArrayList;

import net.minecraft.network.Packet;
import net.wurstclient.events.listeners.PacketOutputListener;

public class PacketOutputEvent extends CancellableEvent<PacketOutputListener>
{
	private Packet packet;
	
	public PacketOutputEvent(Packet packet)
	{
		this.packet = packet;
	}
	
	public Packet getPacket()
	{
		return packet;
	}
	
	public void setPacket(Packet packet)
	{
		this.packet = packet;
	}
	
	@Override
	public void fire(ArrayList<PacketOutputListener> listeners)
	{
		for(int i = 0; i < listeners.size(); i++)
		{
			listeners.get(i).onSentPacket(this);
			if(isCancelled())
				break;
		}
	}
	
	@Override
	public Class<PacketOutputListener> getListenerType()
	{
		return PacketOutputListener.class;
	}
}
