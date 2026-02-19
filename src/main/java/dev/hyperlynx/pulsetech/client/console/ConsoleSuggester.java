package dev.hyperlynx.pulsetech.client.console;

import dev.hyperlynx.pulsetech.core.program.ProgramInterpreter;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;
import java.util.Arrays;

public class ConsoleSuggester {
    private final EditBox box;
    private String current_suggestion = "";

    public ConsoleSuggester(EditBox box) {
        this.box = box;
    }

    public void responder(String current_input) {
        var tokens = current_input.split(" ");
        String initial_word = Arrays.stream(tokens).toList().getLast();
        current_suggestion = "";
        if(!initial_word.isEmpty()) {
            for(String key : ProgramInterpreter.BUILT_IN_COMMANDS.keySet()) {
                if(key.startsWith(initial_word)) {
                    current_suggestion = key.substring(initial_word.length());
                }
            }
        }
        box.setSuggestion(current_suggestion);
        box.setTooltip(Tooltip.create(Component.empty()));
    }

    public void confirmSuggestion() {
        box.setValue(box.getValue() + current_suggestion);
    }
}
