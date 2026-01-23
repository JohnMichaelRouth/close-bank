package com.example;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.MenuAction;
import net.runelite.api.ScriptID;
import net.runelite.api.SpriteID;
import net.runelite.api.WidgetNode;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.events.ScriptPostFired;
import net.runelite.api.events.ScriptPreFired;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.client.events.ConfigChanged;
import net.runelite.api.widgets.ComponentID;
import net.runelite.api.widgets.InterfaceID;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetConfig;
import net.runelite.api.widgets.WidgetPositionMode;
import net.runelite.api.widgets.WidgetType;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

@Slf4j
@PluginDescriptor(
	name = "Close Bank",
	description = "Adds a close button to the bottom right of the bank interface"
)
public class CloseBankPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private CloseBankConfig config;


	@Inject
	private ClientThread clientThread;

	private Widget closeButton = null;

	@Override
	protected void startUp() throws Exception
	{
	}

	@Override
	protected void shutDown() throws Exception
	{
		if (closeButton != null)
		{
			closeButton.setHidden(true);
		}
	}

	@Subscribe
	public void onWidgetLoaded(WidgetLoaded event)
	{
		if (event.getGroupId() == InterfaceID.BANK)
		{
			closeButton = null;
		}
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if (event.getGroup().equals("closebank"))
		{
			clientThread.invokeLater(this::updateButton);
		}
	}

	@Subscribe
	public void onScriptPostFired(ScriptPostFired event)
	{
		if (event.getScriptId() == ScriptID.BANKMAIN_BUILD)
		{
			clientThread.invokeLater(this::updateButton);
		}
	}

	@Subscribe
	public void onScriptPreFired(ScriptPreFired event)
	{
		// Reposition the button when the bank finishes building to ensure correct positioning in resizable mode
		if (event.getScriptId() == ScriptID.BANKMAIN_FINISHBUILDING)
		{
			clientThread.invokeLater(this::updateButton);
		}
	}

	@Subscribe
	public void onMenuOptionClicked(MenuOptionClicked event)
	{
		if (event.getMenuOption().equals("Close Bank"))
		{
			closeBank();
			event.consume();
		}
	}

	private void closeBank()
	{
		try
		{
			for (WidgetNode node : client.getComponentTable())
			{
				if (node.getId() == InterfaceID.BANK)
				{
					client.closeInterface(node, false);
					return;
				}
			}
		}
		catch (Exception e)
		{
			log.error("Error closing bank", e);
		}
	}

	private void updateButton()
	{
		if (!config.enableCloseButton())
		{
			// If button is disabled, hide it if it exists
			if (closeButton != null)
			{
				closeButton.setHidden(true);
			}
			return;
		}

		Widget parent = client.getWidget(ComponentID.BANK_CONTENT_CONTAINER);
		if (parent == null)
		{
			return;
		}

		// Check if button already exists
		if (closeButton != null)
		{
			for (Widget dynamicChild : parent.getDynamicChildren())
			{
				if (dynamicChild == closeButton)
				{
					// Button exists, ensure it's visible and update sizing
					closeButton.setHidden(false);
					updateButtonDimensions();
					repositionButton();
					return;
				}
			}
			// Button reference exists but is not in parent, clear it
			closeButton = null;
		}

		// Create new close button
		closeButton = parent.createChild(-1, WidgetType.GRAPHIC);

		updateButtonDimensions();

		// Enable clicking - set click mask to enable action 0
		closeButton.setClickMask(WidgetConfig.transmitAction(0));

		// Set action
		closeButton.setAction(0, "Close Bank");
		closeButton.setHasListener(true);

		// Position the button based on parent dimensions
		repositionButton();
	}

	private void updateButtonDimensions()
	{
		if (closeButton == null)
		{
			return;
		}

		// Default dimensions
		int baseWidth = 26;
		int baseHeight = 24;

		// Apply size adjustment (user can increase/decrease in increments)
		int sizeAdjustment = config.buttonSizeAdjustment();
		int width = baseWidth + (sizeAdjustment * 2); // *2 to make adjustments more visible
		int height = baseHeight + (sizeAdjustment * 2);

		// Ensure minimum size
		width = Math.max(width, 16);
		height = Math.max(height, 16);

		closeButton.setOriginalHeight(height);
		closeButton.setOriginalWidth(width);
		closeButton.setYPositionMode(WidgetPositionMode.ABSOLUTE_BOTTOM);
		closeButton.setSpriteId(SpriteID.WINDOW_CLOSE_BUTTON);
	}

	private void repositionButton()
	{
		if (closeButton == null)
		{
			return;
		}

		Widget parent = client.getWidget(ComponentID.BANK_CONTENT_CONTAINER);
		if (parent == null)
		{
			return;
		}

		// Calculate bottom offset by checking for widgets at the bottom
		// The incinerator and potion store may move to the bottom in certain resizable sizes
		int bottomOffset = 39; // Default offset from bottom of BANK_CONTENT_CONTAINER

		Widget incinerator = client.getWidget(net.runelite.api.gameval.InterfaceID.Bankmain.INCINERATOR_TARGET);
		if (incinerator != null && !incinerator.isHidden())
		{
			// Incinerator is visible, calculate offset based on it
			int incTop = incinerator.getOriginalY();
			int incHeight = incinerator.getHeight();
			bottomOffset = Math.max(bottomOffset, incHeight + incTop);
		}

		Widget potionStore = client.getWidget(net.runelite.api.gameval.InterfaceID.Bankmain.POTIONSTORE_BUTTON);
		if (potionStore != null && !potionStore.isSelfHidden())
		{
			// Potion store is visible, calculate offset based on it
			int potTop = potionStore.getOriginalY();
			int potHeight = potionStore.getHeight();
			bottomOffset = Math.max(bottomOffset, potHeight + potTop);
		}

		// Add spacing above the bottom widgets
		int baseVerticalPadding = 2;
		int verticalPadding = baseVerticalPadding + config.verticalPaddingAdjustment();
		int yPositionFromBottom = bottomOffset + verticalPadding;

		// Position button at bottom right
		// X position: right edge with spacing (for ABSOLUTE positioning, calculate from left)
		int baseHorizontalPadding = 20;
		int horizontalPadding = baseHorizontalPadding + config.horizontalPaddingAdjustment();
		int xPos = parent.getWidth() - closeButton.getWidth() - horizontalPadding;
		closeButton.setOriginalX(xPos);

		// Y position: using ABSOLUTE_BOTTOM, this is distance from the bottom
		closeButton.setOriginalY(yPositionFromBottom);

		closeButton.revalidate();
	}

	@Provides
	CloseBankConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(CloseBankConfig.class);
	}
}
