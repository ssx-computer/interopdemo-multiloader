package com.example.interopdemo.registry;

import com.example.interopdemo.CommonClass;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

import java.util.function.Supplier;

/** Blocks registered through the platform helper. */
public class ModBlocks {

    private ModBlocks() {
    }

    /** Emits light level 4 to make it obvious the custom block works. */
    public static final Supplier<Block> EXAMPLE_BLOCK =
            CommonClass.PLATFORM.registerBlock("example_block", () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.PLANT)
                    .strength(0.6F)
                    .sound(SoundType.GRASS)
                    .lightLevel(state -> 4)));

    /** The item form of the block, sharing its id. */
    public static final Supplier<BlockItem> EXAMPLE_BLOCK_ITEM =
            CommonClass.PLATFORM.registerBlockItem("example_block", EXAMPLE_BLOCK);

    public static void init() {
        // Touching the class is enough: static fields register on first use.
    }
}
