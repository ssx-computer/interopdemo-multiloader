package com.example.interopdemo.registry;

import com.example.interopdemo.CommonClass;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

/** Items and the creative tab, registered through the platform helper. */
public class ModItems {

    private ModItems() {
    }

    /** A simple example item. */
    public static final Supplier<Item> EXAMPLE_ITEM =
            CommonClass.PLATFORM.registerItem("example_item", () -> new Item(new Item.Properties()));

    /** Creative-mode tab that holds everything this demo registers. */
    public static final Supplier<CreativeModeTab> MAIN_TAB = CommonClass.PLATFORM.registerCreativeModeTab("main",
            () -> CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
                    .title(net.minecraft.network.chat.Component.translatable("itemGroup.interopdemo"))
                    .icon(() -> new ItemStack(EXAMPLE_ITEM.get()))
                    .displayItems((parameters, output) -> {
                        output.accept(EXAMPLE_ITEM.get());
                        // the block's item is registered in ModBlocks; reached via the block supplier
                        BlockItem blockItem = ModBlocks.EXAMPLE_BLOCK_ITEM.get();
                        if (blockItem != null) {
                            output.accept(blockItem);
                        }
                    })
                    .build());

    public static void init() {
        // Touching the class is enough: static fields register on first use.
    }
}
