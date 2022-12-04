package dev.lostluma.lightning_podoboo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.lostluma.lightning_podoboo.CosmeticFireBlock;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

@Mixin(Registries.class)
public class RegistriesMixin {
    @Inject(method = "freezeRegistries()V", at = @At("HEAD"))
    private static void beforeRegistryFreeze(CallbackInfo callbackInfo) {
        Registry.register(Registries.BLOCK, new Identifier("lightning_podoboo", "cosmetic_fire"), CosmeticFireBlock.getInstance());
    }
}
