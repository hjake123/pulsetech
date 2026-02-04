package dev.hyperlynx.pulsetech.feature.debugger;

import dev.hyperlynx.pulsetech.Pulsetech;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;

public record DebuggerInfoRequest(BlockPos pos, int id) implements CustomPacketPayload {
    public static final StreamCodec<ByteBuf, DebuggerInfoRequest> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, DebuggerInfoRequest::pos,
            ByteBufCodecs.INT, DebuggerInfoRequest::id,
            DebuggerInfoRequest::new
    );

    public static final CustomPacketPayload.Type<DebuggerInfoRequest> TYPE = new CustomPacketPayload.Type<>(Pulsetech.location("debugger_info_request"));

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    /// Runs on the server to service this request
    public void processRequest(IPayloadContext context) {
        if(!context.player().level().isLoaded(pos)) {
            return;
        }
        BlockEntity be = context.player().level().getBlockEntity(pos);
        if(be instanceof DebuggerInfoSource source) {
            var getters = source.getDebugInfoGetters();
            if(getters.size() >= id) {
                PacketDistributor.sendToPlayer((ServerPlayer) context.player(), source.getDebugInfoGetters().get(id).get());
            }
        }
    }
}
