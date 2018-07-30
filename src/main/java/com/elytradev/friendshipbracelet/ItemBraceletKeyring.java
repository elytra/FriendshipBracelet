package com.elytradev.friendshipbracelet;

import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import baubles.api.IBauble;
import baubles.api.cap.IBaublesItemHandler;
import com.elytradev.concrete.inventory.ConcreteItemStorage;
import com.elytradev.concrete.inventory.ValidatedItemHandlerView;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.*;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class ItemBraceletKeyring extends Item implements IBauble {

    public String name;
    public NBTTagCompound tag;

    public ItemBraceletKeyring() {
        this.name = "bracelet_keyring";
        setTranslationKey(name);
        setRegistryName(name);
        this.maxStackSize = 1;
        this.tag = new NBTTagCompound();
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.BOW;
    }

    @Override
    public BaubleType getBaubleType(ItemStack itemStack) {
        return BaubleType.TRINKET;
    }

    @Override
    public boolean canEquip(ItemStack stack, EntityLivingBase player) {
        return true;
    }

    @SubscribeEvent
    public void addCapability(AttachCapabilitiesEvent e) {
        if (e.getObject() instanceof ItemStack) {
            ItemStack stack = (ItemStack)e.getObject();
            if (stack.getItem() == ItemFriendshipBracelet.BRACELET_KEYRING) {
                ConcreteItemStorage inv = new ConcreteItemStorage(6)
                        .withValidators((it) -> (it.getItem() == ItemFriendshipBracelet.FRIENDSHIP_BRACELET),
                                (it) -> (it.getItem() == ItemFriendshipBracelet.FRIENDSHIP_BRACELET),
                                (it) -> (it.getItem() == ItemFriendshipBracelet.FRIENDSHIP_BRACELET),
                                (it) -> (it.getItem() == ItemFriendshipBracelet.FRIENDSHIP_BRACELET),
                                (it) -> (it.getItem() == ItemFriendshipBracelet.FRIENDSHIP_BRACELET),
                                (it) -> (it.getItem() == ItemFriendshipBracelet.FRIENDSHIP_BRACELET))
                        .withName(ItemFriendshipBracelet.BRACELET_KEYRING.getTranslationKey()+".name");

                inv.listen(() -> {
                    if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
                    stack.getTagCompound().setTag("Inventory",inv.serializeNBT());
                });

                e.addCapability(new ResourceLocation("friendshipbracelet", "bracelet_holder"), new ICapabilityProvider() {
                    @Override
                    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
                        return (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
                    }

                    @Override
                    @SuppressWarnings("unchecked")
                    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
                        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
                            if (facing == null) {
                                if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
                                inv.deserializeNBT(stack.getTagCompound().getCompoundTag("Inventory"));
                                return (T) inv;
                            }
                            else return (T) new ValidatedItemHandlerView(inv);
                        } else {
                            return null;
                        }
                    }

                });
            }
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack item = player.getHeldItem(hand);
        if(!world.isRemote) {
            if (player.isSneaking()) {
                player.openGui(FriendshipBracelet.instance, 0, world, 0, 0, 0);
            } else {
                ItemStack stack = player.getHeldItem(hand);
                if (stack.getItem() != ItemFriendshipBracelet.BRACELET_KEYRING) return new ActionResult<>(EnumActionResult.FAIL, item);
                ConcreteItemStorage inv = (ConcreteItemStorage)stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
                if (inv == null) return new ActionResult<>(EnumActionResult.FAIL, item);
                ItemStack bracelet = inv.getStackInSlot(0);
                if (bracelet.isEmpty() || !bracelet.hasTagCompound()) return new ActionResult<>(EnumActionResult.FAIL, item);
                if (!bracelet.getTagCompound().hasKey("PlayerIDMost")) return new ActionResult<>(EnumActionResult.FAIL, item);
                UUID id = bracelet.getTagCompound().getUniqueId("PlayerID");
                if (id.equals(player.getPersistentID())) return new ActionResult<>(EnumActionResult.FAIL, item);
                MinecraftServer server = world.getMinecraftServer();
                if (server.getPlayerList().getPlayerByUUID(id) == null) {
                    player.sendStatusMessage(new TextComponentTranslation("msg.fb.notOnline"), true);
                    return new ActionResult<>(EnumActionResult.FAIL, item);
                } else {
                    player.setActiveHand(hand);
                    return new ActionResult<>(EnumActionResult.PASS, item);
                }
            }
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, item);
    }

    @Override
    public int getMaxItemUseDuration(ItemStack itemStack) {
        return 100;
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World world, EntityLivingBase entityLiving) {
        if (stack.getItem() != ItemFriendshipBracelet.BRACELET_KEYRING) return stack;
        ConcreteItemStorage inv = (ConcreteItemStorage)stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
        if (inv == null) return stack;
        ItemStack bracelet = inv.getStackInSlot(0);
        EntityPlayer player = (EntityPlayer)entityLiving;
        UUID id = bracelet.getTagCompound().getUniqueId("PlayerID");
        MinecraftServer server = world.getMinecraftServer();
        if (!world.isRemote) {
            EntityPlayer to = server.getPlayerList().getPlayerByUUID(id);
            if (isAcceptingTeleports(to, player)) {
                player.playSound(SoundEvents.ENTITY_ENDERMEN_TELEPORT, 1f, 0.8f);
                to.playSound(SoundEvents.ENTITY_ENDERMEN_TELEPORT, 0.75f, 0.8f);
                to.playSound(SoundEvents.ENTITY_PLAYER_LEVELUP, 0.75f, 0.9f);
                player.attemptTeleport(to.posX, to.posY, to.posZ);
                to.sendStatusMessage(new TextComponentTranslation("msg.fb.teleportTo", player.getName()), true);
                player.getCooldownTracker().setCooldown(this, 300);
                player.getCooldownTracker().setCooldown(ItemFriendshipBracelet.FRIENDSHIP_BRACELET, 300);
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
                if (baubles.getStackInSlot(i).getItem().equals(ItemFriendshipBracelet.FRIENDSHIP_BRACELET)) return true;
                else if (baubles.getStackInSlot(i).getItem().equals(ItemFriendshipBracelet.BRACELET_KEYRING)) {
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

    public void registerItemModel() {
        FriendshipBracelet.proxy.registerItemRenderer(this, 0, name);
    }

    @Override
    public ItemBraceletKeyring setCreativeTab(CreativeTabs tab) {
        super.setCreativeTab(CreativeTabs.TRANSPORTATION);
        return this;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("Inventory")) {
            IItemHandler inv = stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
            if (inv==null || !(inv instanceof ConcreteItemStorage))  {
                tooltip.add(I18n.format("tooltip.fb.keyring_blank"));
                return;
            }
            ItemStack bracelet = inv.getStackInSlot(0);
            if (!bracelet.isEmpty() && bracelet.hasTagCompound()) {
                tooltip.add(I18n.format("tooltip.fb.keyring", bracelet.getDisplayName()));
            } else {
                tooltip.add(I18n.format("tooltip.fb.keyring_blank"));
            }
        }
        if (!GuiScreen.isShiftKeyDown()) {
            tooltip.add(I18n.format("preview.fb.keyring"));
        } else {
            tooltip.add(I18n.format("tooltip.fb.keyring.0"));
            tooltip.add(I18n.format("tooltip.fb.keyring.1"));
        }
    }

}
