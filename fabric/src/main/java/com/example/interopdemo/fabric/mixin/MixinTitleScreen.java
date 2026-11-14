package com.example.interopdemo.fabric.mixin;

import com.example.interopdemo.Constants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Loader-specific mixin: only applied when running on Fabric.
@Mixin(TitleScreen.class)
public class MixinTitleScreen {

    @Inject(at = @At("HEAD"), method = "init()V")
    private void interopdemo$onInit(CallbackInfo info) {
        Constants.LOG.info("This line is printed by the Interop Demo FABRIC mixin");
        Constants.LOG.info("MC version type: {}", Minecraft.getInstance().getVersionType());
    }
}
