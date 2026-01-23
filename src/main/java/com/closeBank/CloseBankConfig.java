package com.closeBank;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Range;

@ConfigGroup("closebank")
public interface CloseBankConfig extends Config
{
	@ConfigItem(
		keyName = "enableCloseButton",
		name = "Move Close Button",
		description = "Move the bank close button to the bottom right of the bank interface"
	)
	default boolean enableCloseButton()
	{
		return true;
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
		return 5;
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
		return 5;
	}
}
