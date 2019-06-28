package com.elytradev.friendshipbracelet.container;

import io.github.cottonmc.cotton.gui.client.CottonScreen;
import net.minecraft.entity.player.PlayerEntity;

public class BraceletKeyringScreen extends CottonScreen<BraceletKeyringController> {
	public BraceletKeyringScreen(BraceletKeyringController container, PlayerEntity player) {
		super(container, player);
	}
}
