package com.elytradev.friendshipbracelet;

import net.fabricmc.fabric.api.util.NbtType;
import net.mcft.copy.wearables.api.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.*;
import net.minecraft.world.World;

import java.util.Iterator;
import java.util.UUID;
import java.util.stream.Stream;

public class FriendshipBraceletItem extends Item implements IWearablesItem {

    public FriendshipBraceletItem() {
        super(new Item.Settings().group(ItemGroup.TRANSPORTATION));
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);

        MinecraftServer server = world.getServer();

        if (!world.isClient) {
            CompoundTag tag = stack.getOrCreateTag();
            if (!tag.containsKey("PlayerIDMost")) {
                tag.putUuid("PlayerID", player.getUuid());
                TranslatableText name = new TranslatableText("item.friendship_bracelet.rename", player.getDisplayName());
                stack.setCustomName(name);
                return new TypedActionResult<>(ActionResult.FAIL, stack);
            }
            UUID id = tag.getUuid("PlayerID");
            if (id.equals(player.getUuid())) {
                equipWearable(world, player, hand);
                return new TypedActionResult<>(ActionResult.FAIL, stack);
            } else if (server.getPlayerManager().getPlayer(id) == null) {
                player.addChatMessage(new TranslatableText("msg.fb.notOnline"), true);
                return new TypedActionResult<>(ActionResult.FAIL, stack);
            } else {
                player.setCurrentHand(hand);
                return new TypedActionResult<>(ActionResult.PASS, stack);
            }
        }

        return new TypedActionResult<>(ActionResult.PASS, stack);
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity entity) {
        PlayerEntity player = (PlayerEntity)entity;
        UUID id = stack.getOrCreateTag().getUuid("PlayerID");
        MinecraftServer server = world.getServer();
        if (!world.isClient) {
            PlayerEntity to = server.getPlayerManager().getPlayer(id);
            if (isAcceptingTeleports(to, player)) {
                player.playSound(SoundEvents.ENTITY_ENDERMAN_TELEPORT, 0.3f, 0.8f);
                to.playSound(SoundEvents.ENTITY_ENDERMAN_TELEPORT, 0.3f, 0.8f);
                player.teleport(to.getPos().x, to.getPos().y, to.getPos().z, true);
                to.addChatMessage(new TranslatableText("msg.fb.teleportTo", player.getName()), true);
                player.getItemCooldownManager().set(this, 300);
                player.getItemCooldownManager().set(FriendshipBracelet.BRACELET_KEYRING, 300);
            }
            else {
                player.addChatMessage(new TranslatableText("msg.fb.notAccepting"), true);
            }
        }
        return stack;
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 100;
    }

    public static boolean isAcceptingTeleports(PlayerEntity to, PlayerEntity from) {
        Stream<IWearablesSlot> slots = IWearablesEntity.from(to).getEquippedWearables();
        for (Iterator<IWearablesSlot> it = slots.iterator(); it.hasNext(); ) {
            IWearablesSlot slot = it.next();
            if (!slot.isValid() || slot.get().isEmpty()) continue;
            ItemStack stack = slot.get();
            if (stack.getItem().equals(FriendshipBracelet.FRIENDSHIP_BRACELET)) return true;
            else if (stack.getItem().equals(FriendshipBracelet.BRACELET_KEYRING)) {
                if (BraceletKeyringItem.hasKeyringMatch(from, stack)) return true;
            }
        }
        return false;
    }

    @Override
    public boolean canEquip(IWearablesSlot slot, ItemStack stack) {
        if (!slot.getSlotType().matches(FriendshipBracelet.RING)) return false;
        CompoundTag tag = stack.getOrCreateTag();
        if (!tag.containsKey("PlayerIDMost")) return false;
        return slot.getEntity().getUuid().equals(tag.getUuid("PlayerID"));
    }

    public void equipWearable(World world, PlayerEntity player, Hand hand) {
        if (world.isClient) return;
        ItemStack stack = player.getStackInHand(hand);
        Stream<IWearablesSlot> slots = IWearablesEntity.from(player).getEquippedWearables();
        for (Iterator<IWearablesSlot> it = slots.iterator(); it.hasNext(); ) {
            IWearablesSlot slot = it.next();
            if (canEquip(slot, stack)) {
                slot.set(stack.copy());
                stack.decrement(1);
                player.playSound(SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, 1f, 1f);
                return;
            }
        }
    }

    @Override
    public void onCraft(ItemStack stack, World world, PlayerEntity player) {
        super.onCraft(stack, world, player);
        stack.getOrCreateTag().putUuid("PlayerID", player.getUuid());
        TranslatableText name = new TranslatableText("item.friendship_bracelet.rename", player.getDisplayName());
        stack.setCustomName(name);
    }
}