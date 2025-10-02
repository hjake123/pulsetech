package dev.hyperlynx.pulsetech.registration;

import com.mojang.datafixers.types.Type;
import dev.hyperlynx.pulsetech.Pulsetech;
import dev.hyperlynx.pulsetech.block.entity.NumberEmitterBlockEntity;
import dev.hyperlynx.pulsetech.block.entity.PatternDetectorBlockEntity;
import dev.hyperlynx.pulsetech.block.entity.PatternEmitterBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlockEntityTypes {
    public static final DeferredRegister<BlockEntityType<?>> TYPES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Pulsetech.MODID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PatternDetectorBlockEntity>> PATTERN_DETECTOR =
            TYPES.register("pattern_detector", () ->
            BlockEntityType.Builder.of(PatternDetectorBlockEntity::new, ModBlocks.PATTERN_DETECTOR.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PatternEmitterBlockEntity>> PATTERN_EMITTER =
            TYPES.register("pattern_emitter", () ->
                    BlockEntityType.Builder.of(PatternEmitterBlockEntity::new, ModBlocks.PATTERN_EMITTER.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<NumberEmitterBlockEntity>> NUMBER_EMITTER =
            TYPES.register("number_emitter", () ->
                    BlockEntityType.Builder.of(NumberEmitterBlockEntity::new, ModBlocks.NUMBER_EMITTER.get()).build(null));
}
