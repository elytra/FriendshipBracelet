package com.elytradev.friendshipbracelet;

import com.elytradev.concrete.inventory.ConcreteItemStorage;
import com.elytradev.concrete.inventory.ValidatedItemHandlerView;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nullable;

public class ItemBraceletHolder extends Item {

    public String name;
    public NBTTagCompound tag;

    public ItemBraceletHolder() {
        this.name = "bracelet_holder";
        setUnlocalizedName(name);
        setRegistryName(name);
        this.maxStackSize = 1;
        this.tag = new NBTTagCompound();
    }

    @SubscribeEvent
    public void addCapability(AttachCapabilitiesEvent e) {
        if (e.getObject() instanceof ItemBraceletHolder) {
            ConcreteItemStorage inv = new ConcreteItemStorage(6)
                    .withValidators((it) -> (it.getItem() == ItemFriendshipBracelet.FRIENDSHIP_BRACELET),
                            (it) -> (it.getItem() == ItemFriendshipBracelet.FRIENDSHIP_BRACELET),
                            (it) -> (it.getItem() == ItemFriendshipBracelet.FRIENDSHIP_BRACELET),
                            (it) -> (it.getItem() == ItemFriendshipBracelet.FRIENDSHIP_BRACELET),
                            (it) -> (it.getItem() == ItemFriendshipBracelet.FRIENDSHIP_BRACELET),
                            (it) -> (it.getItem() == ItemFriendshipBracelet.FRIENDSHIP_BRACELET))
                    .withName("Bracelet Holder");

            e.addCapability(new ResourceLocation("friendshipbracelet", "bracelet_holder"), new ICapabilityProvider() {
                @Override
                public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
                    return (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
                }

                @Override
                @SuppressWarnings("unchecked")
                public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
                    if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
                        return (T) new ValidatedItemHandlerView(inv);
                    } else {
                        return null;
                    }
                }
            });
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack item = player.getHeldItem(hand);
        if(!world.isRemote && !player.isSneaking()) {
            player.openGui(FriendshipBracelet.instance, 0, world, 0, 0, 0);
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, item);
    }

    public void registerItemModel() {
        FriendshipBracelet.proxy.registerItemRenderer(this, 0, name);
    }

    @Override
    public ItemBraceletHolder setCreativeTab(CreativeTabs tab) {
        super.setCreativeTab(CreativeTabs.TRANSPORTATION);
        return this;
    }

//    private void markDirty() {
//        tag.setTag("Inventory", inv.serializeNBT());
//    }

}
