package com.example;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("closebank")
public interface CloseBankConfig extends Config
{
	@ConfigItem(
		keyName = "enableCloseButton",
		name = "Enable Close Button",
		description = "Show a close button in the bottom right of the bank interface"
	)
	default boolean enableCloseButton()
	{
		return true;
	}
}
