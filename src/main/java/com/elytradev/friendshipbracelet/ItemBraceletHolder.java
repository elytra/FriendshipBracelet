package com.elytradev.friendshipbracelet;

import com.elytradev.concrete.inventory.ConcreteItemStorage;
import com.elytradev.concrete.inventory.IContainerInventoryHolder;
import com.elytradev.concrete.inventory.ValidatedInventoryView;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemBraceletHolder extends Item implements IContainerInventoryHolder {

    public String name;
    public ConcreteItemStorage inv;
    public NBTTagCompound tag;

    public ItemBraceletHolder() {
        this.name = "bracelet_holder";
        setUnlocalizedName(name);
        setRegistryName(name);
        this.maxStackSize = 1;
        this.tag = new NBTTagCompound();
        this.inv = new ConcreteItemStorage(6)
                .withValidators((it)->(it.getItem() == ItemFriendshipBracelet.FRIENDSHIP_BRACELET),
                        (it)->(it.getItem() == ItemFriendshipBracelet.FRIENDSHIP_BRACELET),
                        (it)->(it.getItem() == ItemFriendshipBracelet.FRIENDSHIP_BRACELET),
                        (it)->(it.getItem() == ItemFriendshipBracelet.FRIENDSHIP_BRACELET),
                        (it)->(it.getItem() == ItemFriendshipBracelet.FRIENDSHIP_BRACELET),
                        (it)->(it.getItem() == ItemFriendshipBracelet.FRIENDSHIP_BRACELET));
        inv.listen(this::markDirty);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack item = player.getHeldItem(hand);
        if (item.hasTagCompound()) inv.deserializeNBT(item.getTagCompound().getCompoundTag("Inventory"));
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

    private void markDirty() {
        tag.setTag("Inventory", inv.serializeNBT());
    }

    @Override
    public IInventory getContainerInventory() {
        return new ValidatedInventoryView(inv);
    }
}
