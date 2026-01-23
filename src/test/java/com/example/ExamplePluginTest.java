package com.example;

import com.closeBank.CloseBankPlugin;
import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class ExamplePluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(CloseBankPlugin.class);
		RuneLite.main(args);
	}
}