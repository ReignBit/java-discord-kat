[![Release Build](https://github.com/ReignBit/java-discord-kat/actions/workflows/release-build.yml/badge.svg)](https://github.com/ReignBit/java-discord-kat/actions/workflows/release-build.yml)
<a href="https://github.com/ReignBit/java-discord-kat/issues">
<img src="https://img.shields.io/github/issues/reignbit/java-discord-kat?style=flat-square">
</a>
# Kat - made in java
[My discord bot](https://github.com/Reignbit/discord-kat) rewritten in Java 17 from the ground up using JDA.

## Features [WIP]
 - Automod
 - Welcomer
 - Leveling system
 - Music player
 - Speech recognition & Voice commands

---
## How to build & run for the first time

If you just want to run the bot you can download the [latest compiled version here](
https://github.com/reignbit/java-discord-kat/releases/latest)

Otherwise, follow these instructions to build it yourself:
1. Clone this repo `git clone https://github.com/reignbit/java-discord-kat`
2. Run `./gradlew build`
3. Customize properties inside of `config.properties.example` and rename to `config.properties`
4. Start the Bot!
   1. Run `./gradlew run`
   2. Or you can run using the jar archive built at `build/libs/kat-VERSION-NUMBER.jar`

## Documentation
Documentation for the latest stable releases of Kat are included in the repo under `docs/`. The latest docs are also available at https://reignbit.github.io/java-discord-kat/

## TODO

### Core
 - [x] Command framework
    - [x] Categories
    - [x] Subcommands
    - [x] Slash Commands
    - [x] Argument conversions
 - [x] Metrics
 - [x] Backend API
 - [x] Timers/Events
 - [ ] Interactions [Slash commands, Buttons implemented]

### Command Categories
 - [x] Debug System commands
 - [ ] Fun (Emote)
 - [x] Music Player
 - [ ] Automod
 - [ ] Administration
 - [ ] Welcomer
 - [ ] Fun (Misc)


