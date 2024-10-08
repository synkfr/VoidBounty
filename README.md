# Bounty Plugin

## Overview
The Bounty Plugin enhances the gameplay experience by allowing players to earn bounties for killing other players or mobs. This plugin includes various features to manage bounties, track kill streaks, and display special messages based on bounty amounts.

## Features
- Earn bounties for killing players and mobs.
- Lose 50% of your bounty upon death.
- Commands to manage bounties use `/b` for short:
  - `/bounty set <player> <amount>`: Set a specific bounty on a player. Note that it doesn't add bounty its sets a specific Bounty on player.
  - `/bounty leaderboard`: View the leaderboard of players with the highest bounties.
  - `/bounty remove <player>`: Remove Bounty From the player.
  - `/bounty <player>`: view a player's bounty.
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

# ===========================
#        Bounties
# ===========================

# Bounty amount per kill if no specific mob bounty is set
default-bounty-per-kill: 100.0

# Mob-specific bounties
mob-bounties:
  - ZOMBIE:50
  - SKELETON:75
  - ENDER_DRAGON:10000
  - CREEPER:30
  - SPIDER:40
  # Add more mobs as needed, following the format MOB_NAME:BOUNTY_AMOUNT

# Kill streak multipliers
# Multiplier applied per kill streak, i.e., 5% per streak point
streak-multiplier: 0.05  # 5% per streak

# ===========================
#    Mob Blacklist
# ===========================

# Mob blacklist: Mobs listed here will not give a bounty when killed
mob-blacklist:
  - VILLAGER
  - PILLAGER
  # Add more mobs to blacklist if needed

# ===========================
#        Join Messages
# ===========================

# Join messages based on bounty ranges
# Use '' to show nothing.
join-messages:
  newbie:
    min: 0
    max: 99999
    message: '&d&lWelcome the &x&F&B&0&0&A&6&lN&x&F&C&3&3&B&8&le&x&F&D&6&6&C&A&lw&x&F&D&9&9&D&B&lb&x&F&E&C&C&E&D&li&x&F&F&F&F&F&F&le!'
  adventurer:
    min: 100000
    max: 999999
    message: '&a&lAn &x&0&0&F&B&2&8&lA&x&3&9&F&C&5&8&ld&x&7&1&F&D&8&8&lv&x&A&A&F&E&B&7&le&x&E&3&F&F&E&7&ln&x&E&B&F&F&E&F&lt&x&C&3&F&F&D&0&lu&x&9&A&F&F&B&1&lr&x&7&2&F&F&9&1&le&x&4&A&F&F&7&2&lr &a&lhas joined the server.'
  warlord:
    min: 1000000
    max: 9999999
    message: '&x&B&E&1&1&F&B&lAn &x&8&D&0&0&F&B&lW&x&C&6&8&0&F&D&la&x&F&F&F&F&F&F&lr&x&C&A&A&0&F&F&ll&x&9&4&4&0&F&F&lo&x&7&4&5&2&E&2&lr&x&5&4&6&4&C&5&ld &x&B&E&1&1&F&B&lhas join this server.'
  master:
    min: 10000000
    max: 99999999
    message: '&b&lThe &x&7&3&F&3&F&B&lM&x&A&B&F&8&F&D&la&x&E&3&F&D&F&E&ls&x&E&5&F&F&E&B&lt&x&B&1&F&F&C&2&le&x&7&D&F&F&9&A&lr &b&lof seven sea has arrived.'
  emperor:
    min: 100000000
    max: 999999999
    message: '&x&F&6&F&B&0&A&lThe &x&E&B&F&B&2&F&lE&x&F&5&F&D&9&7&lm&x&F&F&F&F&F&F&lp&x&F&D&F&F&9&5&le&x&F&B&F&F&2&B&lr&x&E&0&E&2&7&8&lo&x&C&5&C&5&C&5&lr &x&F&6&F&B&0&A&lhas joined server '
  god:
    min: 1000000000
    max: 9999999999
    message: '&&x&F&B&0&0&0&0&lA &x&C&B&2&D&3&E&lG&x&D&D&3&A&3&C&lo&x&E&F&4&7&3&A&ld &x&F&B&0&0&0&0&lhas arrived in this server.'

# Add more join messages as necessary, following the structure ROLE_NAME, min, max, and message.
# New roles can also be added dynamically with the "/bounty addrole" command. (coming soon)

