package com.example.interopdemo.platform.services;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.function.Supplier;

/**
 * Abstraction over the loader-specific parts of registration. The common code only
 * talks to this interface; Fabric and NeoForge each provide an implementation that
 * is resolved through {@link java.util.ServiceLoader}.
 *
 * Every method returns a {@link Supplier} so common code can reference registered
 * objects without caring whether the loader registered them eagerly (Fabric) or
 * lazily through events (NeoForge DeferredRegister).
 */
public interface IPlatformHelper {

    String getPlatformName();

    boolean isModLoaded(String modId);

    boolean isDevelopmentEnvironment();

    default String getEnvironmentName() {
        return isDevelopmentEnvironment() ? "development" : "production";
    }

    /** Registers an item under <code>modid:name</code>. */
    <I extends Item> Supplier<I> registerItem(String name, Supplier<I> factory);

    /** Registers a block under <code>modid:name</code>. */
    <B extends Block> Supplier<B> registerBlock(String name, Supplier<B> factory);

    /** Registers a BlockItem sharing the id of the given block. */
    Supplier<BlockItem> registerBlockItem(String name, Supplier<? extends Block> block);

    /** Registers a creative mode tab under <code>modid:name</code>. */
    Supplier<CreativeModeTab> registerCreativeModeTab(String name, Supplier<CreativeModeTab> factory);
}
