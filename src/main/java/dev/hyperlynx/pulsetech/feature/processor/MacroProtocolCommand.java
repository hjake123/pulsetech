package dev.hyperlynx.pulsetech.feature.processor;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.hyperlynx.pulsetech.core.program.ProgramInterpreter;
import dev.hyperlynx.pulsetech.core.protocol.ExecutionContext;
import dev.hyperlynx.pulsetech.core.protocol.ProtocolCommand;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class MacroProtocolCommand extends ProtocolCommand {
    public static final Codec<MacroProtocolCommand> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("parameter_count").forGetter(MacroProtocolCommand::parameterCount),
            Codec.STRING.fieldOf("key").forGetter(MacroProtocolCommand::macro)
    ).apply(instance, MacroProtocolCommand::new));

    /// The macro for this protocol command. Used downstream to determine what the command should do.
    private final String macro;

    public String macro() {
        return macro;
    }

    protected MacroProtocolCommand(int parameter_count, String macro) {
        super(parameter_count);
        this.macro = macro;
    }

    @Override
    public void run(ExecutionContext context) {
        if(context.block() instanceof ProcessorBlockEntity processor) {
            List<String> command_sequence = new ArrayList<>();
            command_sequence.add(macro);
            for(int i = 0; i < parameterCount(); i++) {
                command_sequence.add(context.params().get(i).toString());
            }
            ProgramInterpreter.processTokenList(processor, command_sequence, null, 0);
        }
    }

    @Override
    public Component getNameComponent() {
        return Component.literal(macro);
    }
}
