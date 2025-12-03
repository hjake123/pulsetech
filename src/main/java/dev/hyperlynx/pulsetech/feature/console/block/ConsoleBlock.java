package dev.hyperlynx.pulsetech.feature.console.block;

import com.mojang.serialization.MapCodec;
import dev.hyperlynx.pulsetech.core.PulseBlock;
import dev.hyperlynx.pulsetech.feature.console.OpenConsolePayload;
import dev.hyperlynx.pulsetech.feature.console.macros.Macros;
import dev.hyperlynx.pulsetech.registration.ModBlockEntityTypes;
import dev.hyperlynx.pulsetech.registration.ModComponentTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ConsoleBlock extends PulseBlock implements EntityBlock {
    public ConsoleBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (player instanceof ServerPlayer splayer && level.getBlockEntity(pos) instanceof ConsoleBlockEntity console) {
            PacketDistributor.sendToPlayer(splayer, new OpenConsolePayload(pos, console.getPriorLinesOrEmpty()));
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if(level.isClientSide()) {
            return ItemInteractionResult.SUCCESS;
        }
        if (level.getBlockEntity(pos) instanceof ConsoleBlockEntity console && stack.is(Items.PAPER)) { // TODO Paper as placeholder
            Macros console_macros = console.getMacros();
            if (stack.has(ModComponentTypes.MACROS)) {
                // There are stored macros that need to be synced to the console, and merged into the data on the item
                Macros stored_macros = stack.get(ModComponentTypes.MACROS);
                if (console_macros.equals(stored_macros)) {
                    player.sendSystemMessage(Component.translatable("pulsetech.macros_up_to_date"));
                    return ItemInteractionResult.SUCCESS;
                }
                Macros merged = console_macros.mergeWith(stored_macros);
                stack.set(ModComponentTypes.MACROS, merged);
                console.addMacros(merged.macros());
            } else {
                // There are no stored macros, just set them on the item
                stack.set(ModComponentTypes.MACROS, console_macros);
            }
            player.sendSystemMessage(Component.translatable("pulsetech.synced_macros"));
            return ItemInteractionResult.SUCCESS;
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return ModBlockEntityTypes.CONSOLE.get().create(pos, state);
    }

    public static final MapCodec<ConsoleBlock> CODEC = simpleCodec(ConsoleBlock::new);

    @Override
    protected @NotNull MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (level.getBlockEntity(pos) instanceof ConsoleBlockEntity console) {
            if (console.emitter.isActive()) {
                level.addParticle(DustParticleOptions.REDSTONE, pos.getBottomCenter().x, pos.getBottomCenter().y + 1, pos.getBottomCenter().z, 0, 0, 0);
            }
        }
    }
}
