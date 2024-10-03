package org.ayosynk.voidBounty;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.UUID;

public class VoidBounty extends JavaPlugin implements Listener {

    private FileConfiguration config;
    private HashMap<UUID, Double> bounties;
    private HashMap<UUID, Integer> killStreaks; // Track player kill streaks
    private MobBountyManager mobBountyManager;
    private BountyTargetManager bountyTargetManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        config = getConfig();

        bounties = new HashMap<>();
        killStreaks = new HashMap<>();

        mobBountyManager = new MobBountyManager(config);
        bountyTargetManager = new BountyTargetManager();

        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("BountyPlugin has been enabled!");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("bounty")) {
            if (args.length == 0) {
                sender.sendMessage(ChatColor.RED + "Use /bounty reload, /bounty set <player> <amount>, or /bounty leaderboard");
                return true;
            }

            if (args[0].equalsIgnoreCase("reload")) {
                reloadConfig();
                config = getConfig();
                mobBountyManager.reloadConfig(config);
                sender.sendMessage(ChatColor.GREEN + "Bounty config reloaded!");
                return true;
            }

            if (args[0].equalsIgnoreCase("set") && args.length == 3) {
                Player target = Bukkit.getPlayer(args[1]);
                if (target != null && target.isOnline()) {
                    double amount;
                    try {
                        amount = Double.parseDouble(args[2]);
                    } catch (NumberFormatException e) {
                        sender.sendMessage(ChatColor.RED + "Invalid amount!");
                        return true;
                    }
                    bounties.put(target.getUniqueId(), amount);
                    sender.sendMessage(ChatColor.GREEN + "Set bounty for " + target.getName() + " to " + amount);
                    return true;
                } else {
                    sender.sendMessage(ChatColor.RED + "Player not found!");
                    return true;
                }
            }

            if (args[0].equalsIgnoreCase("leaderboard")) {
                sender.sendMessage(ChatColor.GOLD + "Bounty Leaderboard:");
                bounties.entrySet().stream()
                        .sorted((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()))
                        .limit(10)
                        .forEach(entry -> {
                            Player player = Bukkit.getPlayer(entry.getKey());
                            if (player != null && player.isOnline()) {
                                sender.sendMessage(ChatColor.YELLOW + player.getName() + ": " + entry.getValue());
                            }
                        });
                return true;
            }

            // New Feature: Set a bounty target
            if (args[0].equalsIgnoreCase("target") && args.length == 3) {
                Player target = Bukkit.getPlayer(args[1]);
                if (target != null && target.isOnline()) {
                    double amount;
                    try {
                        amount = Double.parseDouble(args[2]);
                    } catch (NumberFormatException e) {
                        sender.sendMessage(ChatColor.RED + "Invalid bounty amount!");
                        return true;
                    }
                    bountyTargetManager.setBountyTarget(target, amount);
                    sender.sendMessage(ChatColor.GREEN + "Bounty set on " + target.getName() + " for " + amount);
                    return true;
                } else {
                    sender.sendMessage(ChatColor.RED + "Player not found!");
                    return true;
                }
            }
        }
        return false;
    }

    @EventHandler
    public void onEntityKill(EntityDeathEvent event) {
        Entity entity = event.getEntity();

        // Check if the entity is a LivingEntity (like players or mobs)
        if (entity instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity) entity;
            Player killer = livingEntity.getKiller(); // Get the player who killed the entity

            if (killer != null) {
                UUID killerId = killer.getUniqueId();
                double currentBounty = bounties.getOrDefault(killerId, 0.0);

                // Check for mob-specific bounty
                double mobBounty = mobBountyManager.getBountyForMob(livingEntity.getType());
                double newBounty = currentBounty + mobBounty;

                // Apply multiplier based on kill streak
                int streak = killStreaks.getOrDefault(killerId, 0);
                double multiplier = 1.0 + (streak * config.getDouble("streak-multiplier"));
                newBounty *= multiplier;

                bounties.put(killerId, newBounty);
                killer.sendMessage(ChatColor.GREEN + "You earned a bounty! Your new bounty is " + newBounty);
                killStreaks.put(killerId, streak + 1); // Increase kill streak
            }

            // Handle player death and bounty loss
            if (entity.getType() == EntityType.PLAYER) {
                Player killedPlayer = (Player) entity;
                UUID killedId = killedPlayer.getUniqueId();
                double bounty = bounties.getOrDefault(killedId, 0.0);
                if (bounty > 0) {
                    double lostBounty = bounty * config.getDouble("death-loss-percentage");
                    bounties.put(killedId, bounty - lostBounty);
                    killedPlayer.sendMessage(ChatColor.RED + "You lost " + lostBounty + " of your bounty.");
                }
                killStreaks.put(killedId, 0); // Reset kill streak on death
            }
        }
    }


    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();
        double bounty = bounties.getOrDefault(playerId, 0.0);

        for (String section : config.getConfigurationSection("join-messages").getKeys(false)) {
            double minBounty = config.getDouble("join-messages." + section + ".min");
            double maxBounty = config.getDouble("join-messages." + section + ".max");
            if (bounty >= minBounty && bounty < maxBounty) {
                String message = ChatColor.translateAlternateColorCodes('&', config.getString("join-messages." + section + ".message"));
                event.setJoinMessage(message);
                break;
            }
        }
    }
}
