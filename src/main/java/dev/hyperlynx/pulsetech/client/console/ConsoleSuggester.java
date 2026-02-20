package dev.hyperlynx.pulsetech.client.console;

import dev.hyperlynx.pulsetech.core.program.ProgramInterpreter;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ConsoleSuggester {
    private final EditBox box;
    private String current_suggestion = "";
    private final Set<String> extra_valid_names = new HashSet<>();

    public ConsoleSuggester(EditBox box, List<String> initial_extra_names) {
        this.box = box;
        extra_valid_names.addAll(initial_extra_names);
    }

    public void responder(String current_input) {
        String initial_word = Arrays.stream(current_input.split(" ")).toList().getLast();
        current_suggestion = "";
        if(!initial_word.isEmpty()) {
            for(String key : ProgramInterpreter.BUILT_IN_COMMANDS.keySet()) {
                if(key.startsWith(initial_word)) {
                    current_suggestion = key.substring(initial_word.length());
                }
            }
            for(String name : extra_valid_names) {
                if(name.startsWith(initial_word)) {
                    current_suggestion = name.substring(initial_word.length());
                }
            }
        }
        box.setSuggestion(current_suggestion);
        box.setTooltip(Tooltip.create(Component.empty()));
    }

    public void confirmSuggestion() {
        box.setValue(box.getValue() + current_suggestion);
    }

    public void setExtraNames(List<String> macros) {
        extra_valid_names.clear();
        extra_valid_names.addAll(macros);
    }
}
