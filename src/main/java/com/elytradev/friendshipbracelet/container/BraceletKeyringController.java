package com.elytradev.friendshipbracelet.container;

import com.elytradev.friendshipbracelet.FriendshipBracelet;
import io.github.cottonmc.cotton.gui.CottonScreenController;
import io.github.cottonmc.cotton.gui.client.BackgroundPainter;
import io.github.cottonmc.cotton.gui.widget.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;

public class BraceletKeyringController extends CottonScreenController {

    private Identifier ringLoc = new Identifier(FriendshipBracelet.MOD_ID, "textures/items/ring_selection.png");
    WItemSlot[] slots = new WItemSlot[6];
    WPlayerInvPanel playerInv;

    public BraceletKeyringController(int syncId, PlayerInventory inv, InventoryWrapper wrapper) {
        super(null, syncId, inv, wrapper, null);
        wrapper.addListener((listened) -> {
            InventoryWrapper wrapped = (InventoryWrapper)listened;
            CompoundTag tag = Inventories.toTag(new CompoundTag(), wrapped.stackList);
//            if (inv.getMainHandStack().getItem() == FriendshipBracelet.FRIENDSHIP_BRACELET) {
//                inv.getMainHandStack().setTag(tag);
//            } else {
            //for now, only accept in offhand, since getting in mainhand isn't really working
                inv.offHand.get(0).setTag(tag);
//            }
        });
        WPlainPanel panel = new WPlainPanel();
        setRootPanel(panel);
        playerInv = new WPlayerInvPanel(this.playerInventory);
        WSprite selection = new WSprite(ringLoc);
        slots[0] = WItemSlot.of(wrapper, 0, 1, 1);
        slots[1] = WItemSlot.of(wrapper, 1, 1, 1);
        slots[2] = WItemSlot.of(wrapper, 2, 1, 1);
        slots[3] = WItemSlot.of(wrapper, 3, 1, 1);
        slots[4] = WItemSlot.of(wrapper, 4, 1, 1);
        slots[5] = WItemSlot.of(wrapper, 5, 1, 1);

        panel.add(playerInv, 0, 114);
        panel.add(selection, 68, 13, 24, 24);
        panel.add(slots[0], 72, 17);
        panel.add(slots[1], 108, 35);
        panel.add(slots[2], 108, 71);
        panel.add(slots[3], 72, 89);
        panel.add(slots[4], 36, 71);
        panel.add(slots[5], 36, 35);
        panel.validate(this);
    }

    @Override
    public void addPainters() {
        super.addPainters();
        playerInv.setBackgroundPainter(BackgroundPainter.SLOT);
        for (WItemSlot slot : slots) {
            slot.setBackgroundPainter(BackgroundPainter.SLOT);
        }
    }

    @Override
    public int getCraftingResultSlotIndex() {
        return -1;
    }

    @Override
    public boolean canUse(PlayerEntity playerEntity) {
        return true;
    }
}