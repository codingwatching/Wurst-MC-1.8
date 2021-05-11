/*
 * Copyright � 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.settings;

import java.util.ArrayList;

import com.google.gson.JsonObject;

import net.wurstclient.files.ConfigFiles;
import net.wurstclient.navigator.PossibleKeybind;
import net.wurstclient.navigator.gui.NavigatorFeatureScreen;

public class CheckboxSetting implements Setting, CheckboxLock
{
	private final String name;
	private boolean checked;
	private CheckboxLock lock;
	private int y;
	
	public CheckboxSetting(String name, boolean checked)
	{
		this.name = name;
		this.checked = checked;
	}
	
	@Override
	public final String getName()
	{
		return name;
	}
	
	@Override
	public final void addToFeatureScreen(NavigatorFeatureScreen featureScreen)
	{
		y = 60 + featureScreen.getTextHeight() + 4;
		
		featureScreen.addText("\n\n");
		featureScreen.addCheckbox(this);
	}
	
	@Override
	public ArrayList<PossibleKeybind> getPossibleKeybinds(String featureName)
	{
		ArrayList<PossibleKeybind> possibleKeybinds = new ArrayList<>();
		String fullName = featureName + " " + name;
		String command = ".setcheckbox " + featureName.toLowerCase() + " "
			+ name.toLowerCase().replace(" ", "_") + " ";
		
		possibleKeybinds
			.add(new PossibleKeybind(command + "toggle", "Toggle " + fullName));
		possibleKeybinds
			.add(new PossibleKeybind(command + "on", "Enable " + fullName));
		possibleKeybinds
			.add(new PossibleKeybind(command + "off", "Disable " + fullName));
		
		return possibleKeybinds;
	}
	
	@Override
	public final boolean isChecked()
	{
		return isLocked() ? lock.isChecked() : checked;
	}
	
	public final void setChecked(boolean checked)
	{
		if(isLocked())
			return;
		
		this.checked = checked;
		update();
		ConfigFiles.NAVIGATOR.save();
	}
	
	public final void toggle()
	{
		setChecked(!isChecked());
	}
	
	public final void lock(CheckboxLock lock)
	{
		if(lock == this)
			throw new IllegalArgumentException(
				"Infinite loop of locks within locks");
		
		this.lock = lock;
		update();
	}
	
	public final void unlock()
	{
		lock = null;
		update();
	}
	
	public final boolean isLocked()
	{
		return lock != null;
	}
	
	public final int getY()
	{
		return y;
	}
	
	@Override
	public final void save(JsonObject json)
	{
		json.addProperty(name, checked);
	}
	
	@Override
	public final void load(JsonObject json)
	{
		checked = json.get(name).getAsBoolean();
		update();
	}
	
	@Override
	public void update()
	{
		
	}
}
