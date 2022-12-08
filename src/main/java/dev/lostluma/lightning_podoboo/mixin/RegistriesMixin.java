package dev.lostluma.lightning_podoboo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.lostluma.lightning_podoboo.Constants;
import dev.lostluma.lightning_podoboo.CosmeticFireBlock;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

@Mixin(Registries.class)
public class RegistriesMixin {
    @Unique
    private static Boolean lightning_podoboo$blockAdded = false;

    @Inject(method = "init()V", at = @At("HEAD"))
    private static void lightning_podoboo$onRegistriesInit(CallbackInfo callbackInfo) {
        if (!lightning_podoboo$blockAdded) {
            lightning_podoboo$blockAdded = true;
            Registry.register(Registries.BLOCK, new Identifier(Constants.MOD_ID, "cosmetic_fire"), CosmeticFireBlock.getInstance());
        }
    }
}
