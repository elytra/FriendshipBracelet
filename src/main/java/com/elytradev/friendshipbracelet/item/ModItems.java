package com.elytradev.friendshipbracelet.item;

import net.minecraft.item.Item;
import net.minecraftforge.registries.IForgeRegistry;

public class ModItems {

    public static ItemBase FRIENDSHIP_BRACELET = new ItemFriendshipBracelet();
    public static ItemBase[] allItems = {
            FRIENDSHIP_BRACELET
    };

    public static void register(IForgeRegistry<Item> registry) {
        registry.registerAll(allItems);
    }

    public static void registerModels() {
        for (int i = 0; i < allItems.length ; i++) {
            allItems[i].registerItemModel();
        }
    }

    public static void registerOreDict() {
        for (int i = 0; i < allItems.length ; i++) {
            allItems[i].initOreDict();
        }
    }
}
