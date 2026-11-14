package com.example.interopdemo.neoforge;

import com.example.interopdemo.Constants;
import com.example.interopdemo.platform.services.IPlatformHelper;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

// NeoForge implementation of the platform helper. NeoForge freezes the vanilla
// registries until the mod bus fires its registry events, so "registration" from
// common code means: add an entry to a DeferredRegister and attach the register
// to the bus in the mod constructor.
public class NeoForgePlatformHelper implements IPlatformHelper {

    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Constants.MOD_ID);
    private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Constants.MOD_ID);
    private static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(net.minecraft.core.registries.Registries.CREATIVE_MODE_TAB, Constants.MOD_ID);

    /** Called from the NeoForge mod constructor, after {@code CommonClass.init()}. */
    static void bootstrap(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
        BLOCKS.register(modEventBus);
        TABS.register(modEventBus);
    }

    @Override
    public String getPlatformName() {
        return "NeoForge";
    }

    @Override
    public boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return !FMLLoader.isProduction();
    }

    @Override
    public <I extends Item> Supplier<I> registerItem(String name, Supplier<I> factory) {
        return ITEMS.register(name, factory);
    }

    @Override
    public <B extends Block> Supplier<B> registerBlock(String name, Supplier<B> factory) {
        return BLOCKS.register(name, factory);
    }

    @Override
    public Supplier<BlockItem> registerBlockItem(String name, Supplier<? extends Block> block) {
        return ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    @Override
    public Supplier<CreativeModeTab> registerCreativeModeTab(String name, Supplier<CreativeModeTab> factory) {
        return TABS.register(name, factory);
    }
}
