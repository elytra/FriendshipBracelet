package com.elytradev.friendshipbracelet.container;

import com.elytradev.friendshipbracelet.FriendshipBracelet;
import io.github.cottonmc.cotton.gui.CottonScreenController;
import io.github.cottonmc.cotton.gui.widget.WItemSlot;
import io.github.cottonmc.cotton.gui.widget.WPanel;
import io.github.cottonmc.cotton.gui.widget.WPlainPanel;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.Identifier;

public class BraceletKeyringController extends CottonScreenController {

    private Identifier ringLoc = new Identifier(FriendshipBracelet.MOD_ID, "textures/items/ring_selection.png");

    public BraceletKeyringController(int syncId, PlayerInventory inv, InventoryWrapper wrapper) {
        super(null, syncId, inv, wrapper, null);
        WPlainPanel panel = new WPlainPanel();
        setRootPanel(panel);
        WPanel playerInv = this.createPlayerInventoryPanel();
        WImage selection = new WImage(ringLoc);
        WItemSlot slot0 = WItemSlot.of(wrapper, 0, 1, 1);
        WItemSlot slot1 = WItemSlot.of(wrapper, 1, 1, 1);
        WItemSlot slot2 = WItemSlot.of(wrapper, 2, 1, 1);
        WItemSlot slot3 = WItemSlot.of(wrapper, 3, 1, 1);
        WItemSlot slot4 = WItemSlot.of(wrapper, 4, 1, 1);
        WItemSlot slot5 = WItemSlot.of(wrapper, 5, 1, 1);

        panel.add(playerInv, 0, 114);
        panel.add(selection, 68, 13, 24, 24);
        panel.add(slot0, 72, 17);
        panel.add(slot1, 108, 35);
        panel.add(slot2, 108, 71);
        panel.add(slot3, 72, 89);
        panel.add(slot4, 36, 71);
        panel.add(slot5, 36, 35);
        panel.validate(this);
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