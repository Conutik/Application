package com.conutik.itemviewer.viewchest;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;

public class ShulkerInventory extends HandledScreen<ShulkerInventory.CIA> {
    public ShulkerInventory(PlayerInventory playerInventory, List<ItemStack> list) {
        super(new CIA(playerInventory, list), playerInventory, Text.literal("Shulker Preview"));
    }

    @Override
    public void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        int i = (this.width - this.backgroundWidth) / 2;
        int j = (this.height - this.backgroundHeight) / 2;
        context.drawTexture(
                new Identifier("minecraft:textures/gui/container/shulker_box.png"),
                i, j, 0, 0, this.backgroundWidth, this.backgroundHeight
        );
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(context, mouseX, mouseY);
    }

    public static class CIA extends ScreenHandler {
        private final List<ItemStack> list;
        private final Inventory ro = new Inventory() {
            @Override
            public void clear() {
            }

            @Override
            public int size() {
                return list.size();
            }

            @Override
            public boolean isEmpty() {
                return list.stream().allMatch(ItemStack::isEmpty);
            }

            @Override
            public ItemStack getStack(int slot) {
                return list.get(slot);
            }

            @Override
            public ItemStack removeStack(int slot, int amount) {
                return ItemStack.EMPTY;
            }

            @Override
            public ItemStack removeStack(int slot) {
                return ItemStack.EMPTY;
            }

            @Override
            public void setStack(int slot, ItemStack stack) {
            }

            @Override
            public void markDirty() {
            }

            @Override
            public boolean canPlayerUse(PlayerEntity player) {
                return false;
            }
        };

        public CIA(PlayerInventory playerInventory, List<ItemStack> list) {
            super(null, -1);
            this.list = list;
            if(!(list.size() == 3 * 9)) return;
            // Shulkerbox
            for (int row = 0; row < 3; row++) {
                for (int col = 0; col < 9; col++) {
                    addSlot(new Slot(ro, col + row * 9, 8 + col * 18, 18 + row * 18));
                }
            }
            // Player inventory:
            for (int row = 0; row < 3; row++) {
                for (int col = 0; col < 9; col++) {
                    addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
                }
            }
            // Hotbar:
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
            }
        }

        @Override
        public ItemStack quickMove(PlayerEntity player, int slot) {
            return ItemStack.EMPTY;
        }

        @Override
        public boolean canUse(PlayerEntity player) {
            return false;
        }
    }
}
