package com.elytradev.friendshipbracelet.container;

import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import io.github.cottonmc.cotton.gui.widget.WWidget;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;

public class WImage extends WWidget {
	private final Identifier texture;

	public WImage(Identifier id) {
		this.texture = id;
	}


	@Override
	public boolean canResize() {
		return true;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void paintBackground(int x, int y) {
		ScreenDrawing.rect(texture, x, y, getWidth(), getHeight(), 0xFFFFFFFF);
	}
}
