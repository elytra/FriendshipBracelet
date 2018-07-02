package com.elytradev.friendshipbracelet;

import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import baubles.api.IBauble;
import baubles.api.cap.IBaublesItemHandler;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.UUID;

public class ItemFriendshipBracelet extends Item implements IBauble {

    public String name;

    public ItemFriendshipBracelet() {
        this.name = "friendship_bracelet";
        setUnlocalizedName(name);
        setRegistryName(name);
        this.maxStackSize = 1;
    }

    public void registerItemModel() {
        FriendshipBracelet.proxy.registerItemRenderer(this, 0, name);
    }

    @Override
    public ItemFriendshipBracelet setCreativeTab(CreativeTabs tab) {
        super.setCreativeTab(CreativeTabs.TRANSPORTATION);
        return this;
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.BOW;
    }

    @Override
    public boolean canEquip(ItemStack stack, EntityLivingBase player) {
        if (stack.getTagCompound() == null) return false;
        return player.getPersistentID().equals(stack.getTagCompound().getUniqueId("PlayerID"));
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack item = player.getHeldItem(hand);

        MinecraftServer server = world.getMinecraftServer();

        if (!world.isRemote) {
            if (item.getTagCompound() == null) {
                item.setTagCompound(new NBTTagCompound());
                item.getTagCompound().setUniqueId("PlayerID", player.getPersistentID());
                TextComponentTranslation bracelet = new TextComponentTranslation("item.friendship_bracelet.rename");
                String name = "§r"+player.getName()+bracelet.getUnformattedComponentText();
                item.setStackDisplayName(name);
                return new ActionResult<>(EnumActionResult.FAIL, item);
            }
            UUID id = item.getTagCompound().getUniqueId("PlayerID");
            if (id.equals(player.getPersistentID())) {
                equipBauble(world, player, hand);
                return new ActionResult<>(EnumActionResult.FAIL, item);
            } else if (server.getPlayerList().getPlayerByUUID(id) == null) {
                player.sendStatusMessage(new TextComponentTranslation("msg.fb.notOnline"), true);
                return new ActionResult<>(EnumActionResult.FAIL, item);
            } else {
                player.setActiveHand(hand);
                return new ActionResult<>(EnumActionResult.PASS, item);
            }
        }

        return new ActionResult<>(EnumActionResult.PASS, item);
    }

    @Override
    public int getMaxItemUseDuration(ItemStack itemStack) {
        return 100;
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World world, EntityLivingBase entityLiving) {
        EntityPlayer player = (EntityPlayer)entityLiving;
        UUID id = stack.getTagCompound().getUniqueId("PlayerID");
        MinecraftServer server = world.getMinecraftServer();
        if (!world.isRemote) {
            EntityPlayer to = server.getPlayerList().getPlayerByUUID(id);
            if (isAcceptingTeleports(to)) {
                player.attemptTeleport(to.posX, to.posY, to.posZ);
                player.playSound(SoundEvents.BLOCK_PORTAL_TRAVEL, 1f, 1f);
                player.getCooldownTracker().setCooldown(this, 300);
            }
            else {
                player.sendStatusMessage(new TextComponentTranslation("msg.fb.notAccepting"), true);
            }
        }
        return stack;
    }

    @Override
    public BaubleType getBaubleType(ItemStack itemStack) {
        return BaubleType.RING;
    }

    private boolean isAcceptingTeleports(EntityPlayer player) {
        IBaublesItemHandler baubles = BaublesApi.getBaublesHandler(player);
        for(int i = 0; i < baubles.getSlots(); i++) {
            ItemStack stackInSlot = baubles.getStackInSlot(i);
            if (!stackInSlot.isEmpty()) {
                FBLog.info(i);
                return baubles.getStackInSlot(i).getItem().equals(this.FRIENDSHIP_BRACELET);
            }
        }
        return false;
    }

    private void equipBauble(World world, EntityPlayer player, EnumHand hand) {
//        Code from Vazkii's mod Botania. Taken under open-source license and modified to remove improper returns and botania-exclusive functions.
        ItemStack stack = player.getHeldItem(hand);

        ItemStack toEquip = stack.copy();
        toEquip.setCount(1);

        if(canEquip(toEquip, player)) {
            if(world.isRemote) return ;

            IBaublesItemHandler baubles = BaublesApi.getBaublesHandler(player);
            for(int i = 0; i < baubles.getSlots(); i++) {
                if(baubles.isItemValidForSlot(i, toEquip, player)) {
                    ItemStack stackInSlot = baubles.getStackInSlot(i);
                    if(stackInSlot.isEmpty() || ((IBauble) stackInSlot.getItem()).canUnequip(stackInSlot, player)) {
                        // If toEquip and stackInSlot are stacks with equal value but not identity, ItemStackHandler.setStackInSlot actually does nothing >.>
                        // Prevent it from trying to be overly smart by going through empty first
                        baubles.setStackInSlot(i, ItemStack.EMPTY);

                        baubles.setStackInSlot(i, toEquip);
                        ((IBauble) toEquip.getItem()).onEquipped(toEquip, player);

                        stack.shrink(1);

                        if(!stackInSlot.isEmpty()) {
                            ((IBauble) stackInSlot.getItem()).onUnequipped(stackInSlot, player);

                            if(stack.isEmpty()) return; else {
                                ItemHandlerHelper.giveItemToPlayer(player, stackInSlot);
                            }
                        }
                        player.playSound(SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, 1f, 1f);
                        return;
                    }
                }
            }
        }
    }

    public static ItemFriendshipBracelet FRIENDSHIP_BRACELET = new ItemFriendshipBracelet();

    public static void register(IForgeRegistry<Item> registry) {
        registry.register(FRIENDSHIP_BRACELET);
    }

    public static void registerModels() {
        FRIENDSHIP_BRACELET.registerItemModel();
    }
}