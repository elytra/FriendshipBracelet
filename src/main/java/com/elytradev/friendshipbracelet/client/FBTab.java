package com.elytradev.friendshipbracelet.client;

import com.elytradev.friendshipbracelet.FriendshipBracelet;
import com.elytradev.friendshipbracelet.item.ModItems;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.creativetab.CreativeTabs;

public class FBTab extends CreativeTabs {
    public FBTab() {
        super(FriendshipBracelet.modId);
        //setBackgroundImageName("betterboilers.png");
    }

    @Override
    public ItemStack getTabIconItem() {
        return new ItemStack(ModItems.FRIENDSHIP_BRACELET);
    }
}
