package com.example.interopdemo.neoforge;

import com.example.interopdemo.CommonClass;
import com.example.interopdemo.Constants;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

// NeoForge entrypoint, declared through neoforge.mods.toml.
@Mod(Constants.MOD_ID)
public class ExampleMod {

    public ExampleMod(IEventBus modEventBus) {
        Constants.LOG.info("Hello from the NeoForge side of Interop Demo!");

        // Runs the common bootstrap. Because registrations here go through the
        // platform helper, they are collected in DeferredRegisters...
        CommonClass.init();

        // ...which must still be attached to the mod event bus.
        NeoForgePlatformHelper.bootstrap(modEventBus);
    }
}
