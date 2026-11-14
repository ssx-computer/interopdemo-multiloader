package com.example.interopdemo.fabric;

import com.example.interopdemo.Constants;
import com.example.interopdemo.platform.services.IPlatformHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.function.Supplier;

// Fabric implementation of the platform helper: registration happens eagerly
// through the plain (Mojang-mapped) vanilla registries, which Fabric exposes
// exactly like the common project uses them.
public class FabricPlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {
        return "Fabric";
    }

    @Override
    public boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @Override
    public <I extends Item> Supplier<I> registerItem(String name, Supplier<I> factory) {
        I item = factory.get();
        Registry.register(BuiltInRegistries.ITEM, Constants.id(name), item);
        return () -> item;
    }

    @Override
    public <B extends Block> Supplier<B> registerBlock(String name, Supplier<B> factory) {
        B block = factory.get();
        Registry.register(BuiltInRegistries.BLOCK, Constants.id(name), block);
        return () -> block;
    }

    @Override
    public Supplier<BlockItem> registerBlockItem(String name, Supplier<? extends Block> block) {
        BlockItem item = new BlockItem(block.get(), new Item.Properties());
        Registry.register(BuiltInRegistries.ITEM, Constants.id(name), item);
        return () -> item;
    }

    @Override
    public Supplier<CreativeModeTab> registerCreativeModeTab(String name, Supplier<CreativeModeTab> factory) {
        CreativeModeTab tab = factory.get();
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, Constants.id(name), tab);
        return () -> tab;
    }
}
