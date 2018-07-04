package com.elytradev.friendshipbracelet;

import com.elytradev.friendshipbracelet.proxy.CommonProxy;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

@Mod(modid = FriendshipBracelet.modId, name = FriendshipBracelet.name, version = FriendshipBracelet.version, dependencies = "required-after:baubles@[1.5.2,)")
public class FriendshipBracelet {
    public static final String modId = "friendshipbracelet";
    public static final String name  = "Friendship Bracelet";
    public static final String version = "@VERSION@";

    @Mod.Instance(modId)
    public static FriendshipBracelet instance;

    @SidedProxy(serverSide = "com.elytradev.friendshipbracelet.proxy.CommonProxy", clientSide = "com.elytradev.friendshipbracelet.proxy.ClientProxy")
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        FBLog.info(name + " is loading!");

        MinecraftForge.EVENT_BUS.register(proxy);
        MinecraftForge.EVENT_BUS.register(FBRecipes.class);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onItemCraft(PlayerEvent.ItemCraftedEvent e) {
        ItemStack result = e.crafting;
        if (result.getItem().equals(ItemFriendshipBracelet.FRIENDSHIP_BRACELET)) {
            NBTTagCompound tags = (result.hasTagCompound()) ? result.getTagCompound() : new NBTTagCompound();
            tags.setUniqueId("PlayerID", e.player.getPersistentID());
            result.setTagCompound(tags);
            TextComponentTranslation bracelet = new TextComponentTranslation("item.friendship_bracelet.rename");
            String name = "§r"+e.player.getName()+bracelet.getUnformattedComponentText();
            result.setStackDisplayName(name);
        }
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {

    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit();
    }

    @Mod.EventBusSubscriber
    public static class RegistrationHandler {
        @SubscribeEvent
        public static void registerItems(RegistryEvent.Register<Item> event) {
            ItemFriendshipBracelet.register(event.getRegistry());
        }

        @SubscribeEvent
        public static void registerModels(ModelRegistryEvent event) {
            ItemFriendshipBracelet.registerModels();
        }
    }
}
