package com.elytradev.friendshipbracelet.proxy;

import com.elytradev.friendshipbracelet.FriendshipBracelet;
import com.elytradev.friendshipbracelet.ItemFriendshipBracelet;
import com.google.common.collect.ImmutableMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;

public class ClientProxy extends CommonProxy {
    
    @Override
    public void registerItemRenderer(Item item, int meta, String id) {
        ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(new ResourceLocation(FriendshipBracelet.modId, id), "inventory"));
    }

    @Override
    public void postInit() {
        Minecraft.getMinecraft().getItemColors().registerItemColorHandler(new IItemColor() {
            @Override
            public int getColorFromItemstack(ItemStack stack, int tintIndex) {
                if (stack.getItem() != ItemFriendshipBracelet.FRIENDSHIP_BRACELET || stack.getTagCompound() == null) return -1;
                String index = "StringColor"+tintIndex;
                return getDyeColor(stack, index);
            }
        }, ItemFriendshipBracelet.FRIENDSHIP_BRACELET);
    }

    private int getDyeColor(ItemStack stack, String nbt) {
        if (!stack.getTagCompound().hasKey(nbt)) return DYE_COLORS.get(0);
        int dye = stack.getTagCompound().getInteger(nbt);

        return DYE_COLORS.get(dye);
    }

    private static final ImmutableMap<Integer, Integer> DYE_COLORS = ImmutableMap.<Integer, Integer>builder()
            .put(0, 0xF9FFFE)
            .put(1, 0xF9801D)
            .put(2, 0xC74EBD)
            .put(3, 0x3AB3DA)
            .put(4, 0xFED83D)
            .put(5, 0x80C71F)
            .put(6, 0xF38BAA)
            .put(7, 0x474F52)
            .put(8, 0x9D9D97)
            .put(9, 0x169C9C)
            .put(10, 0x8932B8)
            .put(11, 0x3C44AA)
            .put(12, 0x835432)
            .put(13, 0x5E7C16)
            .put(14, 0xB02E26)
            .put(15, 0x1D1D21)
            .build();
}
