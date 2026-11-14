package com.example.interopdemo.fabric;

import com.example.interopdemo.CommonClass;
import com.example.interopdemo.Constants;
import net.fabricmc.api.ModInitializer;

// Fabric entrypoint, wired through the "main" entry in fabric.mod.json.
public class ExampleMod implements ModInitializer {

    @Override
    public void onInitialize() {
        Constants.LOG.info("Hello from the Fabric side of Interop Demo!");

        // All shared setup (and the actual registrations) live in the common project.
        CommonClass.init();
    }
}
