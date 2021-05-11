/*
 * Copyright � 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.files;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import net.minecraft.client.Minecraft;
import net.wurstclient.WurstClient;
import net.wurstclient.utils.JsonUtils;

public class FileManager
{
	public final File autoMaximize =
		new File(WurstFolders.MAIN.toFile(), "automaximize.json");
	
	public void init()
	{
		loadAutoBuildTemplates();
	}
	
	public boolean loadAutoMaximize()
	{
		boolean autoMaximizeEnabled = false;
		if(!autoMaximize.exists())
			saveAutoMaximize(true);
		try
		{
			BufferedReader load =
				new BufferedReader(new FileReader(autoMaximize));
			autoMaximizeEnabled = JsonUtils.gson.fromJson(load, Boolean.class)
				&& !Minecraft.IS_RUNNING_ON_MAC;
			load.close();
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return autoMaximizeEnabled;
	}
	
	public void saveAutoMaximize(boolean autoMaximizeEnabled)
	{
		try
		{
			if(!autoMaximize.getParentFile().exists())
				autoMaximize.getParentFile().mkdirs();
			PrintWriter save = new PrintWriter(new FileWriter(autoMaximize));
			save.println(JsonUtils.prettyGson.toJson(autoMaximizeEnabled));
			save.close();
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void createDefaultAutoBuildTemplates()
	{
		try
		{
			String[] comment =
				{"Copyright � 2014 - 2017 | Wurst-Imperium | All rights reserved.",
					"This Source Code Form is subject to the terms of the Mozilla Public",
					"License, v. 2.0. If a copy of the MPL was not distributed with this",
					"file, You can obtain one at http://mozilla.org/MPL/2.0/."};
			Iterator<Entry<String, int[][]>> itr =
				new DefaultAutoBuildTemplates().entrySet().iterator();
			while(itr.hasNext())
			{
				Entry<String, int[][]> entry = itr.next();
				JsonObject json = new JsonObject();
				json.add("__comment",
					JsonUtils.prettyGson.toJsonTree(comment, String[].class));
				json.add("blocks", JsonUtils.prettyGson
					.toJsonTree(entry.getValue(), int[][].class));
				PrintWriter save = new PrintWriter(
					new FileWriter(new File(WurstFolders.AUTOBUILD.toFile(),
						entry.getKey() + ".json")));
				save.println(JsonUtils.prettyGson.toJson(json));
				save.close();
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void loadAutoBuildTemplates()
	{
		File[] files = WurstFolders.AUTOBUILD.toFile().listFiles();
		
		boolean foundOldTemplates = false;
		TreeMap<String, int[][]> templates = new TreeMap<>();
		for(File file : files)
			try
			{
				// read file
				FileReader reader = new FileReader(file);
				JsonObject json =
					JsonUtils.jsonParser.parse(reader).getAsJsonObject();
				reader.close();
				
				// get blocks
				int[][] blocks =
					JsonUtils.gson.fromJson(json.get("blocks"), int[][].class);
				
				// delete file if old template is found
				if(blocks[0].length == 4)
				{
					foundOldTemplates = true;
					file.delete();
					continue;
				}
				
				// add template
				templates.put(file.getName().substring(0,
					file.getName().lastIndexOf(".json")), blocks);
			}catch(Exception e)
			{
				System.err
					.println("Failed to load template: " + file.getName());
				e.printStackTrace();
			}
			
		// if directory is empty or contains old templates,
		// add default templates and try again
		if(foundOldTemplates
			|| WurstFolders.AUTOBUILD.toFile().listFiles().length == 0)
		{
			createDefaultAutoBuildTemplates();
			loadAutoBuildTemplates();
			return;
		}
		
		if(templates.isEmpty())
			throw new JsonParseException(
				"Couldn't load any AutoBuild templates.");
		
		WurstClient.INSTANCE.mods.autoBuildMod.setTemplates(templates);
	}
}
