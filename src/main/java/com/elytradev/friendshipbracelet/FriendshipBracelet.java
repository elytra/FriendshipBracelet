package com.elytradev.friendshipbracelet;

import com.elytradev.friendshipbracelet.container.BraceletKeyringController;
import com.elytradev.friendshipbracelet.container.InventoryWrapper;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.mcft.copy.wearables.api.WearablesSlotType;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;


public class FriendshipBracelet implements ModInitializer {
    public static final String MOD_ID = "friendshipbracelet";

    public static final Item FRIENDSHIP_BRACELET = register("friendship_bracelet", new FriendshipBraceletItem());
    public static final Item BRACELET_KEYRING = register("bracelet_keyring", new BraceletKeyringItem());

    public static final WearablesSlotType RING = new WearablesSlotType("arms/fingers");
    public static final WearablesSlotType NECKLACE = new WearablesSlotType("chest/neck");

    public static Item register(String name, Item item) {
        return Registry.register(Registry.ITEM, new Identifier(MOD_ID, name), item);
    }

    @Override
    public void onInitialize() {
        ContainerProviderRegistry.INSTANCE.registerFactory(new Identifier(MOD_ID, "keyring"),
                (syncId, id, player, buf) -> new BraceletKeyringController(syncId, player.inventory, new InventoryWrapper(buf.readItemStack())));
        Registry.register(Registry.RECIPE_SERIALIZER, new Identifier(MOD_ID, "friendship_bracelet"), new FriendshipBraceletRecipe.Serializer());
    }
}
