package dev.lostluma.lightningpodoboo;

import net.fabricmc.api.ModInitializer;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.MapColor;
import net.minecraft.block.Material;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class LightningPodoboo implements ModInitializer {
    // Block Settings were copied from vanilla FireBlock instantiation
    public static final CosmeticFireBlock COSMETIC_FIRE_BLOCK = new CosmeticFireBlock(AbstractBlock.Settings.of(Material.FIRE, MapColor.BRIGHT_RED).noCollision().breakInstantly().luminance(state -> 15).sounds(BlockSoundGroup.WOOL));

    @Override
    public void onInitialize() {
        Registry.register(Registry.BLOCK, new Identifier("lightningpodoboo", "cosmetic_fire"), COSMETIC_FIRE_BLOCK);
    }
}
