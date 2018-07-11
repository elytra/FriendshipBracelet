package com.elytradev.friendshipbracelet;

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

        recipe(r, new FriendshipBraceletRecipe(new ResourceLocation("friendshipbracelet:items"), new ItemStack(ItemFriendshipBracelet.FRIENDSHIP_BRACELET, 1),
                "ddd", "sss", "sps",
                'd', "dye",
                's', "string",
                'p', new ItemStack(Items.ENDER_PEARL)
        ));

        recipe(r, new ShapedOreRecipe(new ResourceLocation("friendshipbracelet:items"), new ItemStack(ItemFriendshipBracelet.BRACELET_HOLDER, 1),
                "sls", "lpl", "lll",
                'l', "leather",
                's', "string",
                'p', new ItemStack(Items.ENDER_PEARL)
        ));

    }

    public static <T extends IRecipe> T recipe(IForgeRegistry<IRecipe> registry, T t) {
        t.setRegistryName(new ResourceLocation(t.getRecipeOutput().getItem().getRegistryName()+"_"+t.getRecipeOutput().getItemDamage()));
        registry.register(t);
        return t;
    }

}
