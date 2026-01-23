package com.example;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Range;

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

	@Range(
		min = -10,
		max = 10
	)
	@ConfigItem(
		keyName = "buttonSizeAdjustment",
		name = "Button Size",
		description = "Adjust the size of the close button (0 = default)"
	)
	default int buttonSizeAdjustment()
	{
		return 0;
	}

	@Range(
		min = -20,
		max = 20
	)
	@ConfigItem(
		keyName = "horizontalPaddingAdjustment",
		name = "Right Padding",
		description = "Adjust padding from the right edge of the bank (0 = default)"
	)
	default int horizontalPaddingAdjustment()
	{
		return 0;
	}

	@Range(
		min = -20,
		max = 20
	)
	@ConfigItem(
		keyName = "verticalPaddingAdjustment",
		name = "Bottom Padding",
		description = "Adjust padding from the bottom of the bank (0 = default)"
	)
	default int verticalPaddingAdjustment()
	{
		return 0;
	}
}
