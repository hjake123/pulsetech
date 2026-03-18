package dev.hyperlynx.pulsetech.util;

import com.mojang.serialization.Codec;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;

public class TextComponentCodec {
    public static final Codec<Component> STRING_ONLY = Codec.STRING.xmap(Component::literal, Component::getString);
}
