package net.lostluma.lightning_podoboo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.lostluma.lightning_podoboo.Constants;
import net.lostluma.lightning_podoboo.CosmeticFireBlock;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

@Mixin(BuiltInRegistries.class)
public class BuiltInRegistriesMixin {
    @Inject(method = "createContents()V", at = @At("RETURN"))
    private static void lightning_podoboo$onRegistriesInit(CallbackInfo callbackInfo) {
        Registry.register(BuiltInRegistries.BLOCK, new ResourceLocation(Constants.MOD_ID, "cosmetic_fire"), CosmeticFireBlock.getInstance());
	}
}
