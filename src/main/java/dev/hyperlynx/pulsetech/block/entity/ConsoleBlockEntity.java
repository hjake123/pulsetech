package dev.hyperlynx.pulsetech.block.entity;

import dev.hyperlynx.pulsetech.net.ConsoleLinePayload;
import dev.hyperlynx.pulsetech.net.ConsolePriorLinesPayload;
import dev.hyperlynx.pulsetech.pulse.ProtocolBlockEntity;
import dev.hyperlynx.pulsetech.pulse.Sequence;
import dev.hyperlynx.pulsetech.registration.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public class ConsoleBlockEntity extends ProtocolBlockEntity {
    private Mode mode = Mode.LISTEN;
    private String saved_lines = "";

    public ConsoleBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntityTypes.CONSOLE.get(), pos, blockState);
    }

    private int output_cursor = 0;
    private boolean output_initialized = false;

    @Override
    protected boolean run() {
        if(!output_initialized) {
            output_cursor = 0;
            output_initialized = true;
//            output(true);
//            return true;
        }
        output(buffer.get(output_cursor));
        output_cursor++;
        if(output_cursor < buffer.length()) {
            return true;
        }
        output_initialized = false;
        return false;
    }

    private final Map<String, Consumer<ServerPlayer>> BUILT_IN_COMMANDS = Map.of(
            "help", player -> {
                StringBuilder help_builder = new StringBuilder();
                help_builder.append("help").append(": ").append(Component.translatable("console.pulsetech.help_description").getString()).append("\n");
                help_builder.append("clear").append(": ").append(Component.translatable("console.pulsetech.clear_description").getString()).append("\n");
                for(String key : protocol.keys()) {
                    help_builder.append(key).append("\n");
                }
                PacketDistributor.sendToPlayer(player, new ConsoleLinePayload(getBlockPos(), help_builder.toString()));
            },
            "clear", player -> {
                PacketDistributor.sendToPlayer(player, new ConsolePriorLinesPayload(getBlockPos(), ""));
            }
    );

    public void processLine(String line, ServerPlayer player) {
        if(protocol == null) {
            PacketDistributor.sendToPlayer(player, new ConsoleLinePayload(getBlockPos(), Component.translatable("console.pulsetech.no_protocol").getString()));
            return;
        }
        Arrays.stream(line.split(" ")).forEach(token -> {
            if(BUILT_IN_COMMANDS.containsKey(token.toLowerCase())) {
                BUILT_IN_COMMANDS.get(token.toLowerCase()).accept(player);
            }
            else if(protocol.hasKey(token)) {
                buffer.append(true);
                buffer.appendAll(Objects.requireNonNull(protocol.sequenceFor(token)));
                buffer.append(false);
                buffer.append(false);
            } else {
                try {
                    buffer.appendAll(Sequence.fromShort(Short.parseShort(token)));
                } catch (NumberFormatException ignored) {
                    PacketDistributor.sendToPlayer(player, new ConsoleLinePayload(getBlockPos(), Component.translatable("console.pulsetech.invalid_token").getString() + token));
                }
            }
        });
        if(buffer.length() > 0) {
            setActive(true);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if(mode.equals(Mode.OUTPUT)) {
            tag.putBoolean("output_mode", true);
        }
        if(!saved_lines.isEmpty()) {
            tag.putString("saved_lines", saved_lines);
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if(tag.contains("output_mode")) {
            mode = Mode.OUTPUT;
        }
        if(tag.contains("saved_lines")) {
            saved_lines = tag.getString(saved_lines);
        }
    }

    public void savePriorLines(String lines) {
        saved_lines = lines;
        setChanged();
    }

    public String getPriorLinesOrEmpty() {
        return saved_lines;
    }

    private enum Mode {
        OUTPUT,
        LISTEN
    }
}
