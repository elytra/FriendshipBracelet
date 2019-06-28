package com.elytradev.friendshipbracelet;

import com.elytradev.friendshipbracelet.container.BraceletKeyringController;
import com.elytradev.friendshipbracelet.container.BraceletKeyringScreen;
import com.elytradev.friendshipbracelet.container.InventoryWrapper;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.render.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.screen.ScreenProviderRegistry;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;

public class FriendshipBraceletClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ScreenProviderRegistry.INSTANCE.registerFactory(new Identifier(FriendshipBracelet.MOD_ID, "keyring"),
				(syncId, identifier, player, buf) -> new BraceletKeyringScreen(new BraceletKeyringController(syncId, player.inventory, new InventoryWrapper(buf.readItemStack())), player));
		ColorProviderRegistry.ITEM.register((stack, layer) -> {
			CompoundTag tag = stack.getOrCreateTag();
			switch(layer) {
				case 0:
					if (tag.containsKey("StringColorLeft", NbtType.STRING)) {
						return DyeColor.valueOf(tag.getString("StringColorLeft")).getMaterialColor().color;
					}
					break;
				case 1:
					if (tag.containsKey("StringColorMiddle", NbtType.STRING)) {
						return DyeColor.valueOf(tag.getString("StringColorMiddle")).getMaterialColor().color;
					}
					break;
				case 2:
					if (tag.containsKey("StringColorRight", NbtType.STRING)) {
						return DyeColor.valueOf(tag.getString("StringColorRight")).getMaterialColor().color;
					}
					break;
				default:
					return 0xFFFFFF;
			}
			return 0xFFFFFF;
		});
	}
}
