package com.example.interopdemo.mixin;

import com.example.interopdemo.Constants;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// A mixin that lives in the common project: written once, applied on both loaders.
// On NeoForge Mixin is provided by the loader itself; on Fabric it ships with the
// mixin bootstrap — the compiled code below is identical either way.
@Mixin(Minecraft.class)
public class MixinMinecraft {

    @Inject(at = @At("TAIL"), method = "<init>")
    private void interopdemo$onInit(CallbackInfo info) {
        Constants.LOG.info("Interop Demo common mixin fired! MC version: {}",
                Minecraft.getInstance().getVersionType());
    }
}
