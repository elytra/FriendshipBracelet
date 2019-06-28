package com.elytradev.friendshipbracelet.container;

import com.elytradev.friendshipbracelet.BraceletKeyringItem;
import com.elytradev.friendshipbracelet.FriendshipBracelet;
import com.google.common.collect.Lists;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.BasicInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.InventoryListener;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.DefaultedList;

import java.util.Iterator;
import java.util.List;

public class InventoryWrapper implements Inventory {
	private final int size;
	public final DefaultedList<ItemStack> stackList;
	private ItemStack underlying;
	public InventoryWrapper(ItemStack keyring) {
		this.size = 6;
		this.underlying = keyring;
		this.stackList = BraceletKeyringItem.getInventory(keyring);
	}

	@Override
	public boolean isValidInvStack(int slot, ItemStack stack) {
		return stack.getItem().equals(FriendshipBracelet.FRIENDSHIP_BRACELET);
	}

	@Override
	public int getInvSize() {
		return size;
	}

	public boolean isInvEmpty() {
		Iterator var1 = this.stackList.iterator();

		ItemStack itemStack_1;
		do {
			if (!var1.hasNext()) {
				return true;
			}

			itemStack_1 = (ItemStack)var1.next();
		} while(itemStack_1.isEmpty());

		return false;
	}

	@Override
	public ItemStack getInvStack(int slot) {
		return slot >= 0 && slot < this.stackList.size() ? this.stackList.get(slot) : ItemStack.EMPTY;
	}

	@Override
	public ItemStack takeInvStack(int slot, int amount) {
		ItemStack stack = Inventories.splitStack(this.stackList, slot, amount);
		if (!stack.isEmpty()) {
			this.markDirty();
		}

		return stack;
	}

	@Override
	public ItemStack removeInvStack(int slot) {
		ItemStack stack = this.stackList.get(slot);
		if (stack.isEmpty()) {
			return ItemStack.EMPTY;
		} else {
			this.stackList.set(slot, ItemStack.EMPTY);
			this.markDirty();
			return stack;
		}
	}

	@Override
	public void setInvStack(int slot, ItemStack stack) {
		this.stackList.set(slot, stack);
		if (!stack.isEmpty() && stack.getCount() > this.getInvMaxStackAmount()) {
			stack.setCount(this.getInvMaxStackAmount());
		}

		this.markDirty();
	}

	@Override
	public void markDirty() {
		underlying.setTag(Inventories.toTag(new CompoundTag(), stackList));
	}

	@Override
	public boolean canPlayerUseInv(PlayerEntity playerEntity) {
		return true;
	}

	public void clear() {
		this.stackList.clear();
		this.markDirty();
	}
}