package com.elytradev.friendshipbracelet;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.fabricmc.fabric.api.util.NbtType;
import net.mcft.copy.wearables.api.IWearablesItem;
import net.mcft.copy.wearables.api.IWearablesSlot;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.*;
import net.minecraft.world.World;
import java.util.List;
import java.util.UUID;

public class BraceletKeyringItem extends Item implements IWearablesItem {

    public BraceletKeyringItem() {
       super(new Item.Settings().group(ItemGroup.TRANSPORTATION));
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }

    @Override
    public boolean canEquip(IWearablesSlot slot, ItemStack stack) {
        return true;
    }

    public static DefaultedList<ItemStack> getInventory(ItemStack keyring) {
        DefaultedList<ItemStack> inv = DefaultedList.create(6, ItemStack.EMPTY);
        CompoundTag tag = keyring.getOrCreateTag();
        if (tag.containsKey("Items", NbtType.LIST)) {
            Inventories.fromTag(tag, inv);
        }
        return inv;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        if(!world.isClient) {
            //only allow opening in off hand to prevent inv desyncs
            if (player.isSneaking() && hand == Hand.OFF_HAND) {
                ContainerProviderRegistry.INSTANCE.openContainer(new Identifier(FriendshipBracelet.MOD_ID, "keyring"), player, buf -> buf.writeItemStack(stack));
            } else {
                if (stack.getItem() != FriendshipBracelet.BRACELET_KEYRING) return new TypedActionResult<>(ActionResult.FAIL, stack);
                DefaultedList<ItemStack> inv = getInventory(stack);
                ItemStack bracelet = inv.get(0);
                if (bracelet.isEmpty()) return new TypedActionResult<>(ActionResult.FAIL, stack);
                CompoundTag braceletTag = bracelet.getOrCreateTag();
                if (!braceletTag.containsKey("PlayerIDMost")) return new TypedActionResult<>(ActionResult.FAIL, stack);
                UUID id = braceletTag.getUuid("PlayerID");
                if (id.equals(player.getUuid())) return new TypedActionResult<>(ActionResult.FAIL, stack);
                MinecraftServer server = world.getServer();
                if (server.getPlayerManager().getPlayer(id) == null) {
                    player.addChatMessage(new TranslatableText("msg.fb.notOnline"), true);
                    return new TypedActionResult<>(ActionResult.FAIL, stack);
                } else {
                    player.setCurrentHand(hand);
                    return new TypedActionResult<>(ActionResult.PASS, stack);
                }
            }
        }
        return new TypedActionResult<>(ActionResult.SUCCESS, stack);
    }
    
    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 100;
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity entity) {
        if (stack.getItem() != FriendshipBracelet.BRACELET_KEYRING) return stack;
        DefaultedList<ItemStack> inv = getInventory(stack);
        ItemStack bracelet = inv.get(0);
        PlayerEntity player = (PlayerEntity)entity;
        CompoundTag tag = bracelet.getOrCreateTag();
        if (!tag.containsKey("PlayerIdMost")) return stack;
        UUID id = tag.getUuid("PlayerID");
        MinecraftServer server = world.getServer();
        if (!world.isClient) {
            PlayerEntity to = server.getPlayerManager().getPlayer(id);
            if (FriendshipBraceletItem.isAcceptingTeleports(to, player)) {
                player.playSound(SoundEvents.ENTITY_ENDERMAN_TELEPORT, 1f, 0.8f);
                to.playSound(SoundEvents.ENTITY_ENDERMAN_TELEPORT, 0.75f, 0.8f);
                to.playSound(SoundEvents.ENTITY_PLAYER_LEVELUP, 0.75f, 0.9f);
                player.teleport(to.getPos().x, to.getPos().y, to.getPos().z, true);
                to.addChatMessage(new TranslatableText("msg.fb.teleportTo", player.getName()), true);
                player.getItemCooldownManager().set(this, 300);
                player.getItemCooldownManager().set(FriendshipBracelet.FRIENDSHIP_BRACELET, 300);
            }
            else {
                player.addChatMessage(new TranslatableText("msg.fb.notAccepting"), true);
            }
        }
        return stack;
    }

    public static boolean hasKeyringMatch(PlayerEntity player, ItemStack keyring) {
        UUID playerID = player.getUuid();
        DefaultedList<ItemStack> inv = getInventory(keyring);
        for (int i = 0; i < 6; i++) {
            ItemStack bracelet = inv.get(i);
            if (!bracelet.isEmpty()) {
                CompoundTag tag = bracelet.getOrCreateTag();
                if (!tag.containsKey("PlayerIDMost")) return false;
                UUID braceletID = tag.getUuid("PlayerID");
                if (braceletID.equals(playerID)) return true;
            }
        }
        return false;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext ctx) {
        DefaultedList<ItemStack> inv = getInventory(stack);
        ItemStack bracelet = inv.get(0);
        if (!bracelet.isEmpty()) {
            tooltip.add(new TranslatableText("tooltip.fb.keyring"));
            tooltip.add(new TranslatableText("tooltip.fb.keyring.name", bracelet.getName()));
        } else {
            tooltip.add(new TranslatableText("tooltip.fb.keyring_blank"));
        }
        if (!Screen.hasShiftDown()) {
            tooltip.add(new TranslatableText("preview.fb.keyring"));
        } else {
            tooltip.add(new TranslatableText("tooltip.fb.keyring.0").formatted(Formatting.GRAY));
            tooltip.add(new TranslatableText("tooltip.fb.keyring.1").formatted(Formatting.GRAY));
            tooltip.add(new TranslatableText("tooltip.fb.keyring.2").formatted(Formatting.GRAY));
            tooltip.add(new TranslatableText("tooltip.fb.keyring.3").formatted(Formatting.GRAY));
            tooltip.add(new TranslatableText("tooltip.fb.keyring.4").formatted(Formatting.GRAY));
        }
    }

}
