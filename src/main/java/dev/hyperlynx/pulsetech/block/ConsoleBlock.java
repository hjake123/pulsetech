package dev.hyperlynx.pulsetech.block;

import com.mojang.serialization.MapCodec;
import dev.hyperlynx.pulsetech.block.entity.ConsoleBlockEntity;
import dev.hyperlynx.pulsetech.block.entity.PatternEmitterBlockEntity;
import dev.hyperlynx.pulsetech.client.PulsetechClient;
import dev.hyperlynx.pulsetech.net.OpenConsolePayload;
import dev.hyperlynx.pulsetech.pulse.ProtocolBlock;
import dev.hyperlynx.pulsetech.registration.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ConsoleBlock extends ProtocolBlock implements EntityBlock {
    public ConsoleBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if(player instanceof ServerPlayer splayer && level.getBlockEntity(pos) instanceof ConsoleBlockEntity console) {
            PacketDistributor.sendToPlayer(splayer, new OpenConsolePayload(pos, console.getPriorLinesOrEmpty()));
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return ModBlockEntityTypes.CONSOLE.get().create(pos, state);
    }

    public static final MapCodec<ProtocolBlock> CODEC = simpleCodec(ConsoleBlock::new);

    @Override
    protected @NotNull MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }
}
