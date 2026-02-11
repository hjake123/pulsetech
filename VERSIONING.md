# Versioning Scheme
Pulsetech uses [Semantic Versioning](semver.org) somewhat more literally then other mods.
Each mod release is labeled in the format `minecraft_version-PLATFORM.FEATURE.PATCH`

Aside from the self-explanatory `minecraft_version`, the other version number components should be as follows:
# `PLATFORM`
The `PLATFORM` version will increase only if I make enough changes to Pulsetech to break existing setups in a world -- removing blocks, changing commands, or redesigning whole systems. I will try to not have to increase it very often, and might never end up increasing it. 

I might also increase the `PLATFORM` version for a feature update that adds such a substantial new system that it would seriously change the balance of using the mod in a modpack. This would be a way to advertise the new system and alert pack developers to the change.

Note that if setups break after an update in a way not mentioned in a changelog, that should be considered a bug and reported, especially if the update did not change the `PLATFORM` version. That being said, the `PLATFORM` version reacts only to a change in the INTENDED behavior of the systems, so setups relying on unintended behavior might break after a lesser update.
# `FEATURE`
The `FEATURE` version increases whenever I add new features to the mod. The 'new features' in question could be player-facing features, mod pack creator features, or major enhancements to existing features.
# `PATCH`
The `PATCH` version increases for any update that doesn't meet the prior criteria -- bug fixes, translations, balance adjustments, minor redesigns, and internal changes all fit here.