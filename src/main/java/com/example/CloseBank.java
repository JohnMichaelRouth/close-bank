package com.example;

import net.runelite.client.game.SpriteOverride;

public enum CloseBank implements SpriteOverride
{
	CLOSE_BUTTON(-3195, "delete.png");

	private final int spriteId;
	private final String fileName;

	CloseBank(int spriteId, String fileName)
	{
		this.spriteId = spriteId;
		this.fileName = fileName;
	}

	@Override
	public int getSpriteId()
	{
		return spriteId;
	}

	@Override
	public String getFileName()
	{
		return fileName;
	}
}

