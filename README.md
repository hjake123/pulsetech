# pulsetech
<img src="https://github.com/hjake123/pulsetech/blob/d60f4dcd3207ede3d011f228400867e761fda2aa/pulsetech_preview.png"></img>

**Pulsetech** is a tech mod built around transmitting data using pulses of Redstone signal. The mod is fully documented in-game using Ponder, but feel free to poke around here to see how it works anyway.

# Releases
[Curseforge Download Page](https://www.curseforge.com/minecraft/mc-mods/pulsetech)

[Modrinth Download Page](https://modrinth.com/mod/pulsetech)

# Build Instructions
This mod is built using Gradle -- the exact properties can be found in `gradle/gradle-wrapper.properties`. It's developed using IntelliJ, so importing the project into that IDE should be sufficient for the inbuilt Gradle integration to pick up and prepare the project for building.

Alternatively, `./gradlew.bat init` should be sufficient to set up the build environment.

Once set up, use the `build` gradle task to compile everything. You should then use `runData` to create the generated data files. Once that is done, use the `jar` gradle task to create the mod file in `build/libs`.

The `runClient` task is also quite useful for testing the mod. `runServer` also works, but you need to log in using an account, so you cannot log into the debug server using the debug client.

If you have further questions, you can contact me at `dev@hyperlynx.dev`.

# Contribution Information
I do accept pull requests! I try to keep most development in the GitHub issues, and use GitHub features to make it clear what I'm up to. If I'm assigned to an open issue, that means I'm anticipating working on it myself. Feel free to comment on issues as well, as especially for feature/enhancement issues I like to talk things over with interested parties. In particular, if you're working on something, I'd appreciate if you comment on the issue so I know not to start it. I reserve the right to not merge features that I don't feel make sense for the mod, but I will almost always merge small enhancements, translations, and bug fixes. 

I add all contributors to a list inside `gradle.properties`, which displays in the mod entry in the NeoForge "Mods" menu.
