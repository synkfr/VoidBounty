# Bounty Plugin

## Overview
The Bounty Plugin enhances the gameplay experience by allowing players to earn bounties for killing other players or mobs. This plugin includes various features to manage bounties, track kill streaks, and display special messages based on bounty amounts.

## Features
- Earn bounties for killing players and mobs.
- Lose 50% of your bounty upon death.
- Commands to manage bounties:
  - `/bounty reload`: Reload the plugin configuration.
  - `/bounty set <player> <amount>`: Set a specific bounty on a player. This a Admin Command but if you have Luckperms or any kind of permission manager you can change it.
  - `/bounty leaderboard`: View the leaderboard of players with the highest bounties.
- Special join messages based on bounty ranges.
- Support for chat color in messages.
- Configure specific bounties for killing specific mobs.
- Kill streak tracking to apply multipliers to bounties.

## Installation
1. Download the latest release of the Bounty Plugin JAR file.
2. Place the JAR file into the `plugins` folder of your Minecraft server.
3. Start the server to generate the configuration files.
4. Configure the plugin by editing the `config.yml` file.

## Configuration
The main configuration file is located at `plugins/BountyPlugin/config.yml`. Here are some of the configurable options:

```yaml
# <<==================================>>
#            VoidBountyPlugin
# <<==================================>>

# Configuration For VoidBountyPlugin

# Bounty amount per kill if no specific mob bounty is set
default-bounty-per-kill: 100.0

# Mob-specific bounties
mob-bounties:
  - ZOMBIE:50
  - SKELETON:75
  - ENDER_DRAGON:10000
  - CREEPER:30
  - SPIDER:40

# Kill streak multipliers
streak-multiplier: 0.05  # 5% per streak


# ===========================
#        Join Message
# ===========================

# Join messages based on bounty ranges
join-messages:
  newbie:
    min: 0
    max: 99999
    message: '&b{player} has joined the game. Welcome, &aNewbie!'
  adventurer:
    min: 100000
    max: 999999
    message: '&b{player} has joined the game. Behold, the &9Adventurer!'
  warlord:
    min: 1000000
    max: 9999999
    message: '&b{player} has joined the game. All hail the &6Warlord!'
# add more if necessary
