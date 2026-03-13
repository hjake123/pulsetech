package dev.hyperlynx.pulsetech.feature.number.bulb;

import com.mojang.serialization.Codec;
import dev.hyperlynx.pulsetech.core.Sequence;
import dev.hyperlynx.pulsetech.core.protocol.ProtocolBlockEntity;
import dev.hyperlynx.pulsetech.feature.debugger.DebuggerInfoManifest;
import dev.hyperlynx.pulsetech.feature.debugger.DebuggerInfoSource;
import dev.hyperlynx.pulsetech.feature.debugger.infotype.DebuggerByteInfo;
import dev.hyperlynx.pulsetech.feature.debugger.infotype.DebuggerInfoTypes;
import dev.hyperlynx.pulsetech.feature.debugger.infotype.DebuggerTextInfo;
import dev.hyperlynx.pulsetech.registration.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.Stack;
import java.util.function.BiFunction;
import java.util.function.Function;

public class NumberBulbBlockEntity extends ProtocolBlockEntity implements DebuggerInfoSource {
    private final Stack<Byte> numbers = new Stack<>();

    public NumberBulbBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntityTypes.NUMBER_BULB.get(), pos, blockState);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        numbers.clear();
        for(Byte b : Codec.list(Codec.BYTE).decode(NbtOps.INSTANCE, tag.get("number")).getPartialOrThrow().getFirst()) {
            numbers.addLast(b);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("numbers", Codec.list(Codec.BYTE).encodeStart(NbtOps.INSTANCE, numbers.stream().toList()).getPartialOrThrow());
    }

    @Override
    public void setChanged() {
        super.setChanged();
        NumberBulbBlock.updateValueLights(level, getBlockPos(), stackSize());
    }

    @Override
    public DebuggerInfoManifest getDebuggerInfoManifest() {
        if(NumberBulbBlock.getStackSize(level, getBlockPos()) == 1) {
            return super.getDebuggerInfoManifest()
                    .append(new DebuggerInfoManifest.Entry(
                            Component.translatable("debugger.pulsetech.number").getString(),
                            DebuggerInfoTypes.NUMBER.get(),
                            () -> new DebuggerByteInfo(numbers.isEmpty() ? 0 : numbers.peek()))
                    );
        }
        return super.getDebuggerInfoManifest()
                .append(new DebuggerInfoManifest.Entry(
                        Component.translatable("debugger.pulsetech.stack").getString(),
                        DebuggerInfoTypes.TEXT.get(),
                        () -> new DebuggerTextInfo(numbers.reversed().toString() + "\n\n" + Component.translatable("pulsetech.stack_size").getString() + NumberBulbBlock.getStackSize(level, getBlockPos())))
                );
    }

    public void push(Byte first) {
        if(NumberBulbBlock.getStackSize(level, getBlockPos()) <= numbers.size()) {
            // Stack overflow!
            return;
        }
        numbers.push(first);
        setChanged();
    }

    public byte pop() {
        if(numbers.isEmpty()) {
            return 0;
        }
        byte number = numbers.pop();
        setChanged();
        return number;
    }

    public byte peek() {
        return numbers.peek();
    }

    public void dropCount(Byte first) {
        for(int i = 0; i < first; i++) {
            pop();
        }
    }

    public byte stackSize() {
        return (byte) numbers.size();
    }

    public void dup() {
        push(peek());
    }

    public void swap() {
        byte number1 = pop();
        byte number2 = pop();
        push(number1);
        push(number2);
    }

    /// Perform an arbitrary operation on the top two numbers, returning one new number which is pushed back onto the stack.
    public void operate(BiFunction<Byte, Byte, Byte> operation) {
        byte x = pop();
        byte y = pop();
        push(operation.apply(x, y));
    }

    /// Emit a pulse if the given condition returns true.
    public void testTop(Function<Byte, Boolean> test) {
        if(test.apply(peek())) {
            emitRaw(new Sequence(true, false));
        }
    }

    public void fitToSize(byte new_stack_size) {
        if(new_stack_size < 0 || stackSize() < new_stack_size) {
            return;
        }
        dropCount((byte) (stackSize() - new_stack_size));
    }
}
