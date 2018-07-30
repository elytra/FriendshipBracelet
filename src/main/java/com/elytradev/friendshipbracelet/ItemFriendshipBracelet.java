package com.elytradev.friendshipbracelet;

import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import baubles.api.IBauble;
import baubles.api.cap.IBaublesItemHandler;
import com.elytradev.concrete.inventory.ConcreteItemStorage;
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
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.UUID;

public class ItemFriendshipBracelet extends Item implements IBauble {

    public String name;

    public ItemFriendshipBracelet() {
        this.name = "friendship_bracelet";
        setTranslationKey(name);
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
    public BaubleType getBaubleType(ItemStack itemStack) {
        return BaubleType.RING;
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
            if (!item.hasTagCompound() || !item.getTagCompound().hasKey("PlayerIDMost")) {
                if (!item.hasTagCompound()) item.setTagCompound(new NBTTagCompound());
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
            if (isAcceptingTeleports(to, player)) {
                player.playSound(SoundEvents.ENTITY_ENDERMEN_TELEPORT, 0.3f, 0.8f);
                to.playSound(SoundEvents.ENTITY_ENDERMEN_TELEPORT, 0.3f, 0.8f);
                player.attemptTeleport(to.posX, to.posY, to.posZ);
                to.sendStatusMessage(new TextComponentTranslation("msg.fb.teleportTo", player.getName()), true);
                player.getCooldownTracker().setCooldown(this, 300);
                player.getCooldownTracker().setCooldown(BRACELET_KEYRING, 300);
            }
            else {
                player.sendStatusMessage(new TextComponentTranslation("msg.fb.notAccepting"), true);
            }
        }
        return stack;
    }

    private boolean isAcceptingTeleports(EntityPlayer to, EntityPlayer from) {
        IBaublesItemHandler baubles = BaublesApi.getBaublesHandler(to);
        for (int i = 0; i < baubles.getSlots(); i++) {
            ItemStack stackInSlot = baubles.getStackInSlot(i);
            if (!stackInSlot.isEmpty()) {
                if (baubles.getStackInSlot(i).getItem().equals(FRIENDSHIP_BRACELET)) return true;
                else if (baubles.getStackInSlot(i).getItem().equals(BRACELET_KEYRING)) {
                    return hasKeyringMatch(from, stackInSlot);
                }
            }
        }
        return false;
    }

    private boolean hasKeyringMatch(EntityPlayer player, ItemStack keyring) {
        UUID playerID = player.getPersistentID();
        if (!keyring.hasTagCompound()) return false;
        ConcreteItemStorage inv = (ConcreteItemStorage)keyring.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
        if (inv == null) return false;
        for (int i = 0; i < 6; i++) {
            ItemStack bracelet = inv.getStackInSlot(i);
            if (!bracelet.isEmpty() && bracelet.hasTagCompound()) {
                if (!bracelet.getTagCompound().hasKey("PlayerIDMost")) return false;
                UUID braceletID = bracelet.getTagCompound().getUniqueId("PlayerID");
                if (braceletID.equals(playerID)) return true;
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
                    if(stackInSlot.isEmpty()) {
                        // Prevent it from trying to be overly smart by going through empty first
                        baubles.setStackInSlot(i, ItemStack.EMPTY);

                        baubles.setStackInSlot(i, toEquip);
                        ((IBauble) toEquip.getItem()).onEquipped(toEquip, player);

                        stack.shrink(1);
                        player.playSound(SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, 1f, 1f);
                        return;
                    }
                }
            }
        }
    }

    public static ItemFriendshipBracelet FRIENDSHIP_BRACELET = new ItemFriendshipBracelet().setCreativeTab(CreativeTabs.TRANSPORTATION);
    public static ItemBraceletKeyring BRACELET_KEYRING = new ItemBraceletKeyring().setCreativeTab(CreativeTabs.TRANSPORTATION);

    public static void register(IForgeRegistry<Item> registry) {
        registry.register(FRIENDSHIP_BRACELET);
        registry.register(BRACELET_KEYRING);
    }

    public static void registerModels() {
        FRIENDSHIP_BRACELET.registerItemModel();
        BRACELET_KEYRING.registerItemModel();
    }
}