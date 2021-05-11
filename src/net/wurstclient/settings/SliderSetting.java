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

import net.minecraft.util.MathHelper;
import net.wurstclient.navigator.PossibleKeybind;
import net.wurstclient.navigator.gui.NavigatorFeatureScreen;

public class SliderSetting implements Setting, SliderLock
{
	private final String name;
	private double value;
	
	private final double minimum;
	private final double maximum;
	
	private double usableMin;
	private double usableMax;
	
	private final double increment;
	private final ValueDisplay display;
	
	private SliderLock lock;
	private boolean disabled;
	
	private int y;
	
	public SliderSetting(String name, double value, double minimum,
		double maximum, double increment, ValueDisplay display)
	{
		this.name = name;
		this.value = value;
		
		this.minimum = minimum;
		this.maximum = maximum;
		
		usableMin = minimum;
		usableMax = maximum;
		
		this.increment = increment;
		this.display = display;
	}
	
	@Override
	public final String getName()
	{
		return name;
	}
	
	@Override
	public final void addToFeatureScreen(NavigatorFeatureScreen featureScreen)
	{
		featureScreen.addText("\n" + name + ":");
		y = 60 + featureScreen.getTextHeight();
		featureScreen.addText("\n");
		
		featureScreen.addSlider(this);
	}
	
	@Override
	public ArrayList<PossibleKeybind> getPossibleKeybinds(String featureName)
	{
		ArrayList<PossibleKeybind> binds = new ArrayList<>();
		
		String fullName = featureName + " " + name;
		String cmd = ".setslider " + featureName.toLowerCase() + " "
			+ name.toLowerCase().replace(" ", "_") + " ";
		
		binds.add(new PossibleKeybind(cmd + "more", "Increase " + fullName));
		binds.add(new PossibleKeybind(cmd + "less", "Decrease " + fullName));
		
		return binds;
	}
	
	@Override
	public final double getValue()
	{
		return MathHelper.clamp(isLocked() ? lock.getValue() : value, usableMin,
			usableMax);
	}
	
	public final float getValueF()
	{
		return (float)getValue();
	}
	
	public final int getValueI()
	{
		return (int)getValue();
	}
	
	public final void setValue(double value)
	{
		if(disabled || isLocked())
			return;
		
		value = (int)(value / increment) * increment;
		value = MathHelper.clamp(value, usableMin, usableMax);
		this.value = value;
		
		update();
	}
	
	public final void increaseValue()
	{
		setValue(getValue() + increment);
	}
	
	public final void decreaseValue()
	{
		setValue(getValue() - increment);
	}
	
	public final void lock(SliderLock lock)
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
	
	public final String getValueString()
	{
		return display.getValueString(getValue());
	}
	
	public final double getMinimum()
	{
		return minimum;
	}
	
	public final double getMaximum()
	{
		return maximum;
	}
	
	public final double getRange()
	{
		return maximum - minimum;
	}
	
	public final double getIncrement()
	{
		return increment;
	}
	
	public final double getUsableMin()
	{
		return usableMin;
	}
	
	public final void setUsableMin(double usableMin)
	{
		if(usableMin < minimum)
			throw new IllegalArgumentException("usableMin < minimum");
		
		this.usableMin = usableMin;
		update();
	}
	
	public final void resetUsableMin()
	{
		usableMin = minimum;
		update();
	}
	
	public final double getUsableMax()
	{
		return usableMax;
	}
	
	public final void setUsableMax(double usableMax)
	{
		if(usableMax > maximum)
			throw new IllegalArgumentException("usableMax > maximum");
		
		this.usableMax = usableMax;
		update();
	}
	
	public final void resetUsableMax()
	{
		usableMax = maximum;
		update();
	}
	
	public final boolean isLimited()
	{
		return usableMax != maximum || usableMin != minimum;
	}
	
	public final boolean isDisabled()
	{
		return disabled;
	}
	
	public final void setDisabled(boolean disabled)
	{
		this.disabled = disabled;
	}
	
	public final int getY()
	{
		return y;
	}
	
	public final float getPercentage()
	{
		return (float)((getValue() - minimum) / getRange());
	}
	
	@Override
	public final void load(JsonObject json)
	{
		double newValue = json.get(name).getAsDouble();
		
		if(newValue > maximum || newValue < minimum)
			return;
		
		setValue(newValue);
	}
	
	@Override
	public final void save(JsonObject json)
	{
		json.addProperty(name, getValue());
	}
	
	@Override
	public void update()
	{
		
	}
	
	public static interface ValueDisplay
	{
		public static final ValueDisplay DECIMAL =
			(v) -> Math.round(v * 1e6) / 1e6 + "";
		public static final ValueDisplay INTEGER = (v) -> (int)v + "";
		public static final ValueDisplay PERCENTAGE =
			(v) -> (int)(Math.round(v * 1e8) / 1e6) + "%";
		public static final ValueDisplay DEGREES = (v) -> (int)v + "�";
		public static final ValueDisplay NONE = (v) -> "";
		
		public String getValueString(double value);
	}
}
