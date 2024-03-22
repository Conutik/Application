package com.conutik.itemviewer;

import com.conutik.itemviewer.mixin.HandledScreenMixin;
import com.conutik.itemviewer.viewchest.ShulkerInventory;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.block.Block;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.screen.slot.Slot;
import org.lwjgl.glfw.GLFW;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class ItemViewerClient implements ClientModInitializer {

    private static KeyBinding previewKeybind;

    private static Duration lastPress = Duration.ZERO;

    @Override
    public void onInitializeClient() {

        previewKeybind = KeyBindingHelper.registerKeyBinding(
                new KeyBinding(
                        "Preview Inventory",
                        GLFW.GLFW_KEY_GRAVE_ACCENT,
                        "Item Viewer"
                )
        );

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if(!(client.currentScreen instanceof HandledScreenMixin)) return;
            int key = InputUtil.fromTranslationKey(previewKeybind.getBoundKeyTranslationKey()).getCode();
            if(InputUtil.isKeyPressed(client.getWindow().getHandle(), key) && Duration.ofMillis(System.currentTimeMillis()).minus(lastPress).toMillis() > 1000) {
                previewKeybind.setPressed(false);
                Slot focusedSlot = ((HandledScreenMixin) client.currentScreen).getFocusedSlot_Firmament();
                if(focusedSlot == null) return;
                ItemStack itemStack = focusedSlot.getStack();
                if(itemStack == null || !itemStack.getItem().equals(Items.SHULKER_BOX)) return;
                lastPress = Duration.ofMillis(System.currentTimeMillis());
                client.player.closeHandledScreen();
                NbtElement items = getItems(itemStack);
                if(items == null) return;
                List<ItemStack> itemss = populateArray(items);
                MinecraftClient.getInstance().setScreen(new ShulkerInventory(client.player.getInventory(), itemss));
            }
        });
    }

    public static NbtElement getItems(ItemStack items) {
        if(items == null) return null;
        if(items.getItem() instanceof BlockItem) {
            Block block = ((BlockItem) items.getItem()).getBlock();
            if(block instanceof ShulkerBoxBlock) {
                return BlockItem.getBlockEntityNbt(items).get("Items");
            }
            return null;
        }
        return null;
    }

    public static List<ItemStack> populateArray(NbtElement nbtElement) {
        List<ItemStack> list = new ArrayList<>(27);
        for(int i = 0; i < 27; i++) {
            list.add(ItemStack.EMPTY);
        }
        if (nbtElement instanceof NbtCompound nbtCompound) {
            for (String key : nbtCompound.getKeys()) {
                NbtElement element = nbtCompound.get(key);
                assert element != null;
                ItemStack item = ItemStack.fromNbt((NbtCompound) element);
            }
        } else if (nbtElement instanceof NbtList nbtList) {
            for (NbtElement element : nbtList) {
                NbtCompound nbtCompElement = ((NbtCompound) element);
                int slot = nbtCompElement.getByte("Slot");
                ItemStack item = ItemStack.fromNbt(nbtCompElement);
                list.set(slot, item);
            }
        }
        return list;
    }
}
