package com.elytradev.friendshipbracelet;

import com.elytradev.concrete.inventory.gui.ConcreteContainer;
import com.elytradev.concrete.inventory.gui.widget.*;
import net.minecraft.inventory.IInventory;

public class BraceletHolderContainer extends ConcreteContainer {

    public BraceletHolderContainer(IInventory player, IInventory container) {
        super(player, container);
        WPlainPanel panel = new WPlainPanel();
        setRootPanel(panel);
        WPanel playerInv = this.createPlayerInventoryPanel();
        WItemSlot slot0 = WItemSlot.of(container, 0);
        WItemSlot slot1 = WItemSlot.of(container, 1);
        WItemSlot slot2 = WItemSlot.of(container, 2);
        WItemSlot slot3 = WItemSlot.of(container, 3);
        WItemSlot slot4 = WItemSlot.of(container, 4);
        WItemSlot slot5 = WItemSlot.of(container, 5);

        panel.add(playerInv, 0, 114);
        panel.add(slot0, 72, 17);
        panel.add(slot1, 108, 35);
        panel.add(slot2, 108, 71);
        panel.add(slot3, 72, 89);
        panel.add(slot4, 36, 71);
        panel.add(slot5, 36, 35);
    }

}