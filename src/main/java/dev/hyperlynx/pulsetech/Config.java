package dev.hyperlynx.pulsetech;

import com.google.common.collect.Lists;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Neo's config APIs
public class Config {
    private static final ModConfigSpec.Builder COMMON = new ModConfigSpec.Builder();
    static final ModConfigSpec COMMON_SPEC;

    public static final ModConfigSpec.IntValue CANNON_MAX_RANGE;
    public static final ModConfigSpec.IntValue CANNON_MAX_BLAST_RESIST;
    public static final ModConfigSpec.IntValue ORB_MAX_RANGE;
    public static final ModConfigSpec.ConfigValue<List<String>> ORB_CANNOT_GRAB;
    public static final ModConfigSpec.DoubleValue ORB_SPEED;
    public static final ModConfigSpec.IntValue SCANNER_RANGE;
    public static final ModConfigSpec.IntValue MAX_MACRO_UNWRAP_COUNT;

    private static final ModConfigSpec.Builder CLIENT = new ModConfigSpec.Builder();
    static final ModConfigSpec CLIENT_SPEC;

    public static final ModConfigSpec.BooleanValue CONSOLE_AUTOCOMPLETE;

    static {
        MAX_MACRO_UNWRAP_COUNT = COMMON.comment("Maximum number of times the Console or Processor can unwrap macros in a single evaluation. Higher values mean more complex programs are possible, but may cause lag spikes when the macro is evaluated. [Default: 256]").defineInRange("maxMacroUnwrapCount", 256, 4, 512);
        CANNON_MAX_RANGE = COMMON.comment("Maximum range of the Coil Cannon in blocks. [Default: 25]").defineInRange("cannonMaxRange", 25, 1, 64);
        CANNON_MAX_BLAST_RESIST = COMMON.comment("The Coil Cannon can break blocks with below this blast resistance. [Default: 50]").defineInRange("cannonMaxBlastResist", 50, 1, Integer.MAX_VALUE);
        ORB_MAX_RANGE = COMMON.comment("Maximum range of the Orb Projector in blocks. [Default: 32]").defineInRange("orbProjectorMaxRange", 32, 1, 64);
        ORB_CANNOT_GRAB = COMMON.comment("A list of entities the Orb can never pick up. [Default: \"minecraft:ender_dragon\", \"minecraft:wither\", \"minecraft:warden\"]")
                .define("orbCannotGrab",Lists.newArrayList("minecraft:ender_dragon", "minecraft:wither", "minecraft:warden"));
        ORB_SPEED = COMMON.comment("How far the Orb moves each movement tick in blocks. [Default: 0.12]").defineInRange("orbSpeed", 0.12, 0.01, 1.0);
        SCANNER_RANGE = COMMON.comment("The range of detection for the Scanner has a radius of this many blocks. [Default: 16]").defineInRange("scannerRange", 16, 4, 64);
        COMMON_SPEC = COMMON.build();

        CONSOLE_AUTOCOMPLETE = CLIENT.comment("Whether the Console should attempt to autocomplete keywords. [Default: true]").define("consoleAutocomplete", true);
        CLIENT_SPEC = CLIENT.build();
    }
}
