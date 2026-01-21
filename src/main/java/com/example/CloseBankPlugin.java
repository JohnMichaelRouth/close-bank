package com.example;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.MenuAction;
import net.runelite.api.ScriptID;
import net.runelite.api.WidgetNode;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.events.ScriptPostFired;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.widgets.ComponentID;
import net.runelite.api.widgets.InterfaceID;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetConfig;
import net.runelite.api.widgets.WidgetPositionMode;
import net.runelite.api.widgets.WidgetType;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.SpriteManager;
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
	private SpriteManager spriteManager;

	@Inject
	private ClientThread clientThread;

	private Widget closeButton = null;

	@Override
	protected void startUp() throws Exception
	{
		spriteManager.addSpriteOverrides(CloseBank.values());
	}

	@Override
	protected void shutDown() throws Exception
	{
		spriteManager.removeSpriteOverrides(CloseBank.values());
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
	public void onScriptPostFired(ScriptPostFired event)
	{
		if (event.getScriptId() == ScriptID.BANKMAIN_BUILD)
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
					closeButton.setHidden(false);
					return;
				}
			}
		}

		// Create new close button
		closeButton = parent.createChild(-1, WidgetType.GRAPHIC);

		closeButton.setOriginalHeight(18);
		closeButton.setOriginalWidth(18);
		closeButton.setYPositionMode(WidgetPositionMode.ABSOLUTE_BOTTOM);
		closeButton.setOriginalX(434);
		closeButton.setOriginalY(45);
		closeButton.setSpriteId(CloseBank.CLOSE_BUTTON.getSpriteId());

		// Enable clicking - set click mask to enable action 0
		closeButton.setClickMask(WidgetConfig.transmitAction(0));

		// Set action
		closeButton.setAction(0, "Close Bank");
		closeButton.setHasListener(true);
		closeButton.revalidate();
	}

	@Provides
	CloseBankConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(CloseBankConfig.class);
	}
}
