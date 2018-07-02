package com.elytradev.friendshipbracelet;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

public class FriendshipBraceletRecipe extends ShapedOreRecipe {

    public FriendshipBraceletRecipe(ResourceLocation group, @Nonnull ItemStack result, Object... recipe) {
        this(group, result, CraftingHelper.parseShaped(recipe));
    }
    private FriendshipBraceletRecipe(ResourceLocation group, @Nonnull ItemStack result, CraftingHelper.ShapedPrimer primer) {
        super(group, result, primer);
    }

    @Override
    @Nonnull
    public ItemStack getCraftingResult(@Nonnull InventoryCrafting var1) {
        ItemStack result = output.copy();
        int dye0 = getStackColor(var1.getStackInSlot(0));
        int dye1 = getStackColor(var1.getStackInSlot(1));
        int dye2 = getStackColor(var1.getStackInSlot(2));
        NBTTagCompound tags = new NBTTagCompound();
        tags.setInteger("StringColor0", dye0);
        tags.setInteger("StringColor1", dye1);
        tags.setInteger("StringColor2", dye2);
        result.setTagCompound(tags);
        return result;
    }

    //    Code from Vazkii's mod Botania. Modified to remove mana pearls, but nothing else.
    private static final List<String> DYES = Arrays.asList("dyeWhite", "dyeOrange", "dyeMagenta", "dyeLightBlue", "dyeYellow", "dyeLime", "dyePink", "dyeGray", "dyeLightGray", "dyeCyan", "dyePurple", "dyeBlue", "dyeBrown", "dyeGreen", "dyeRed", "dyeBlack");

    int getStackColor(ItemStack stack) {
        int[] ids = OreDictionary.getOreIDs(stack);
        for(int i : ids) {
            int index = DYES.indexOf(OreDictionary.getOreName(i));
            if(index >= 0)
                return index;
        }

        return -1;
    }
}
