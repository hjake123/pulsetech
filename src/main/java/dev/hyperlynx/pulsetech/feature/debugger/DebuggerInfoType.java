package dev.hyperlynx.pulsetech.feature.debugger;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;

public abstract class DebuggerInfoType<T> {
    public abstract StreamCodec<? extends ByteBuf, T> streamCodec();
}
