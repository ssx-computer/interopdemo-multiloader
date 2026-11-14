package com.example.interopdemo;

import com.example.interopdemo.platform.Services;
import com.example.interopdemo.platform.services.IPlatformHelper;
import com.example.interopdemo.registry.ModBlocks;
import com.example.interopdemo.registry.ModItems;

// This class is part of the common project, meaning it is shared between all supported
// loaders. Code written here can only import and access the plain (Mojang-mapped)
// Minecraft codebase, libraries used by Minecraft, and loader-agnostic third party
// libraries. Loader-specific concepts (Fabric callbacks, NeoForge events,
// DeferredRegister) are wrapped behind the IPlatformHelper service below.
public class CommonClass {

    // ServiceLoader resolves this to FabricPlatformHelper or NeoForgePlatformHelper
    // depending on which loader launched the game. Every registration call in the
    // common registry classes goes through this instance.
    public static final IPlatformHelper PLATFORM = Services.load(IPlatformHelper.class);

    // The single entry point that BOTH loader entrypoints call at startup.
    public static void init() {
        Constants.LOG.info("Hello from Interop Demo common init on {} ({} environment)!",
                PLATFORM.getPlatformName(), PLATFORM.getEnvironmentName());

        // Touching the registry classes performs the registration: directly on
        // Fabric, through DeferredRegisters on NeoForge.
        ModItems.init();
        ModBlocks.init();
    }
}
