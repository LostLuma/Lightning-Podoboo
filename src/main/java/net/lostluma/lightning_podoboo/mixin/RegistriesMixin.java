package net.lostluma.lightning_podoboo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.lostluma.lightning_podoboo.Constants;
import net.lostluma.lightning_podoboo.CosmeticFireBlock;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

@Mixin(Registries.class)
public class RegistriesMixin {
    @Inject(method = "init()V", at = @At("RETURN"))
    private static void lightning_podoboo$onRegistriesInit(CallbackInfo callbackInfo) {
        Registry.register(Registries.BLOCK, new Identifier(Constants.MOD_ID, "cosmetic_fire"), CosmeticFireBlock.getInstance());
    }
}
