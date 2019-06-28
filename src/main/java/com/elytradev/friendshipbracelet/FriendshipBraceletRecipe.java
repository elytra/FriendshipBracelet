package com.elytradev.friendshipbracelet;

import com.google.gson.JsonObject;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.util.*;

import java.util.Iterator;

public class FriendshipBraceletRecipe extends ShapedRecipe {
    private int[] colorLocations;

    public FriendshipBraceletRecipe(Identifier id, DefaultedList<Ingredient> inputs, ItemStack output, int[] colorLocations) {
        super(id, "", 3, 3, inputs, output);
        this.colorLocations = colorLocations;
    }

    @Override
    public ItemStack craft(CraftingInventory craftingInventory) {
        ItemStack output = this.getOutput().copy();
        CompoundTag tag = output.getOrCreateTag();
        String dye0 = getStackColor(craftingInventory.getInvStack(colorLocations[0]));
        String dye1 = getStackColor(craftingInventory.getInvStack(colorLocations[1]));
        String dye2 = getStackColor(craftingInventory.getInvStack(colorLocations[2]));
        tag.putString("StringColorLeft", dye0);
        tag.putString("StringColorMiddle", dye1);
        tag.putString("StringColorRight", dye2);
        return output;
    }

    public int[] getColorLocations() {
        return colorLocations;
    }

    String getStackColor(ItemStack stack) {
        if (stack.getItem() instanceof DyeItem) {
            return ((DyeItem)stack.getItem()).getColor().asString();
        }
        return DyeColor.WHITE.asString();
    }

    public static class Serializer implements RecipeSerializer<FriendshipBraceletRecipe> {
        public Serializer() {
        }

        public FriendshipBraceletRecipe read(Identifier id, JsonObject json) {
            ShapedRecipe oldRecipe = new ShapedRecipe.Serializer().method_8164(id, json);
            int[] locations = new int[3];
            locations[0] = JsonHelper.getInt(json, "color_left");
            locations[1] = JsonHelper.getInt(json, "color_middle");
            locations[2] = JsonHelper.getInt(json, "color_right");
            return new FriendshipBraceletRecipe(id, oldRecipe.getPreviewInputs(), oldRecipe.getOutput(), locations);
        }

        public FriendshipBraceletRecipe read(Identifier id, PacketByteBuf buf) {
            DefaultedList<Ingredient> inputs = DefaultedList.create(9, Ingredient.EMPTY);

            for(int i = 0; i < inputs.size(); ++i) {
                inputs.set(i, Ingredient.fromPacket(buf));
            }

            ItemStack output = buf.readItemStack();
            int[] locations = new int[3];
            for (int i = 0; i < locations.length; i++) {
                locations[i] = buf.readInt();
            }
            return new FriendshipBraceletRecipe(id, inputs, output, locations);
        }

        public void write(PacketByteBuf buf, FriendshipBraceletRecipe recipe) {
            Iterator itr = recipe.getPreviewInputs().iterator();

            while(itr.hasNext()) {
                Ingredient ingredient = (Ingredient)itr.next();
                ingredient.write(buf);
            }

            buf.writeItemStack(recipe.getOutput());
            for (int i = 0; i < recipe.getColorLocations().length; i++) {
                buf.writeInt(recipe.getColorLocations()[i]);
            }
        }
    }
}
