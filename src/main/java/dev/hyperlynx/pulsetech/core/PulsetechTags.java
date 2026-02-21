package dev.hyperlynx.pulsetech.core;

import dev.hyperlynx.pulsetech.Pulsetech;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class PulsetechTags {
    /// Items in this tag stop the normal block right click function for all [PulseBlock]s, to allow the item right click to run instead.
    public static final TagKey<Item> preventNormalUsage = TagKey.create(BuiltInRegistries.ITEM.key(), Pulsetech.location("override_normal_use"));
}
