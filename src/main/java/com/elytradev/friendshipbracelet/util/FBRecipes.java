package com.elytradev.friendshipbracelet.util;

import com.elytradev.friendshipbracelet.item.ModItems;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.registries.IForgeRegistry;

public class FBRecipes {

    @SubscribeEvent
    public static void onRegisterRecipes(RegistryEvent.Register<IRecipe> event) {

        IForgeRegistry<IRecipe> r = event.getRegistry();

//         Crafting bench recipes

        recipe(r, new ShapedOreRecipe(new ResourceLocation("friendshipbracelet:items"), new ItemStack(ModItems.FRIENDSHIP_BRACELET, 1),
                " s ", "sps", "s s",
                'r', new ItemStack(Items.STRING),
                'p', new ItemStack(Items.ENDER_PEARL)
        ));

    }

    public static <T extends IRecipe> T recipe(IForgeRegistry<IRecipe> registry, T t) {
        t.setRegistryName(new ResourceLocation(t.getRecipeOutput().getItem().getRegistryName()+"_"+t.getRecipeOutput().getItemDamage()));
        registry.register(t);
        return t;
    }

}
