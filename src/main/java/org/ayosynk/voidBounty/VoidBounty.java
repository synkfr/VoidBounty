package org.ayosynk.voidBounty;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VoidBounty extends JavaPlugin {

    private double defaultBountyPerKill;
    private Map<String, Double> mobBounties = new HashMap<>();
    private double streakMultiplier;
    private List<String> mobBlacklist;
    private Map<String, JoinMessage> joinMessages = new HashMap<>();
    private FileConfiguration playerDataConfig; // Player data configuration

    // Method to access player data configuration
    public FileConfiguration getPlayerDataConfig() {
        return playerDataConfig;
    }

    // ChatUtils class for formatting and colorizing chat messages
    static class ChatUtils {

        public static String formatBounty(double amount) {
            if (amount >= 1_000_000_000_000_000_000L) {
                // Quintillions
                return ChatColor.GREEN + String.format("%.2f", amount / 1_000_000_000_000_000_000L) + "Q";
            } else if (amount >= 1_000_000_000_000L) {
                // Trillions
                return ChatColor.GREEN + String.format("%.2f", amount / 1_000_000_000_000L) + "T";
            } else if (amount >= 1_000_000_000) {
                // Billions
                return ChatColor.GREEN + String.format("%.2f", amount / 1_000_000_000) + "B";
            } else if (amount >= 1_000_000) {
                // Millions
                return ChatColor.GREEN + String.format("%.2f", amount / 1_000_000) + "M";
            } else if (amount >= 1_000) {
                // Thousands
                return ChatColor.GREEN + String.format("%.2f", amount / 1_000) + "K";
            } else {
                // Less than 1K
                return ChatColor.GREEN + String.valueOf(amount);
            }
        }

        // Method to translate color codes
        public static String colorize(String message) {
            return ChatColor.translateAlternateColorCodes('&', message);
        }
    }

    @Override
    public void onEnable() {
        getLogger().info("VoidBounty enabled!");
        loadConfig();
        loadJoinMessages(); // Load join messages after loading the config
        loadPlayerData(); // Load player data when the plugin is enabled
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        getServer().getPluginManager().registerEvents(new MobListener(this), this);

    }

    // Load join messages from the config
    public void loadJoinMessages() {
        joinMessages.clear();
        FileConfiguration config = getConfig();
        config.getConfigurationSection("join-messages").getKeys(false).forEach(role -> {
            double min = config.getDouble("join-messages." + role + ".min");
            double max = config.getDouble("join-messages." + role + ".max");
            String message = config.getString("join-messages." + role + ".message");
            joinMessages.put(role, new JoinMessage(min, max, message));
        });
    }

    // Load player data from playerdata.yml
    private void loadPlayerData() {
        File playerDataFile = new File(getDataFolder(), "playerdata.yml");
        if (!playerDataFile.exists()) {
            saveResource("playerdata.yml", false);
        }
        playerDataConfig = YamlConfiguration.loadConfiguration(playerDataFile);
    }

    @Override
    public void onDisable() {
        getLogger().info("VoidBounty disabled!");
    }

    private void loadConfig() {
        saveDefaultConfig();
        FileConfiguration config = getConfig();
        defaultBountyPerKill = config.getDouble("default-bounty-per-kill");
        streakMultiplier = config.getDouble("streak-multiplier");

        // Load mob bounties
        for (String mob : config.getStringList("mob-bounties")) {
            String[] parts = mob.split(":");
            if (parts.length == 2) {
                mobBounties.put(parts[0].toUpperCase(), Double.parseDouble(parts[1]));
            }
        }

        // Load mob blacklist
        mobBlacklist = config.getStringList("mob-blacklist");
    }

    public double getDefaultBountyPerKill() {
        return defaultBountyPerKill;
    }

    public Map<String, Double> getMobBounties() {
        return mobBounties;
    }

    public double getStreakMultiplier() {
        return streakMultiplier;
    }

    public List<String> getMobBlacklist() {
        return mobBlacklist;
    }

    public Map<String, JoinMessage> getJoinMessages() {
        return joinMessages;
    }

    public void addRole(String roleName, double min, double max, String message) {
        if (joinMessages.containsKey(roleName)) {
            getLogger().warning("Role " + roleName + " already exists!");
            return;
        }

        FileConfiguration config = getConfig();
        config.set("join-messages." + roleName + ".min", min);
        config.set("join-messages." + roleName + ".max", max);
        config.set("join-messages." + roleName + ".message", message);

        saveConfig();
        loadJoinMessages();
    }

    public double getPlayerBounty(String playerName) {
        return playerDataConfig.getDouble("players." + playerName + ".bounty", 0);
    }

    public void setPlayerBounty(String playerName, double amount) {
        playerDataConfig.set("players." + playerName + ".bounty", amount);
        savePlayerData();
    }

    private void savePlayerData() {
        try {
            playerDataConfig.save(new File(getDataFolder(), "playerdata.yml"));
        } catch (Exception e) {
            getLogger().severe("Could not save player data: " + e.getMessage());
        }
    }

    public String getJoinMessageForBounty(double bounty) {
        for (Map.Entry<String, JoinMessage> entry : joinMessages.entrySet()) {
            JoinMessage joinMessage = entry.getValue();
            if (bounty >= joinMessage.getMin() && bounty <= joinMessage.getMax()) {
                return joinMessage.getMessage();
            }
        }
        return null;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("bounty")) {
            if (args.length == 0) {
                sender.sendMessage(ChatUtils.colorize("&cUsage: /bounty <set|remove|player_name|leaderboard|addrole> [arguments]"));
                return true;
            }

            if (args.length == 1) {
                // Display leaderboard if the argument is "leaderboard"
                if (args[0].equalsIgnoreCase("leaderboard")) {
                    displayLeaderboard(sender);
                    return true;
                }

                // Show the player's bounty
                Player target = Bukkit.getPlayer(args[0]);
                if (target != null) {
                    double bounty = getPlayerBounty(target.getName());
                    sender.sendMessage(ChatUtils.colorize("&a" + target.getName() + "'s bounty: " + ChatUtils.formatBounty(bounty)));
                } else {
                    sender.sendMessage(ChatUtils.colorize("&cPlayer not found."));
                }
                return true;
            }

            // Handle /bounty set <player> <amount>
            if (args.length >= 3 && args[0].equalsIgnoreCase("set")) {
                if (sender.hasPermission("voidbounty.admin") || sender.isOp()) {
                    Player target = Bukkit.getPlayer(args[1]);
                    if (target != null) {
                        try {
                            double amount = Double.parseDouble(args[2]);
                            setPlayerBounty(target.getName(), amount);
                            sender.sendMessage(ChatUtils.colorize("&aBounty set for " + target.getName() + ": " + ChatUtils.formatBounty(amount)));
                        } catch (NumberFormatException e) {
                            sender.sendMessage(ChatUtils.colorize("&cInvalid number format for bounty amount."));
                        }
                    } else {
                        sender.sendMessage(ChatUtils.colorize("&cPlayer not found."));
                    }
                } else {
                    sender.sendMessage(ChatUtils.colorize("&cYou do not have permission to use this command."));
                }
                return true;
            }

            // Handle /bounty remove <player>
            if (args.length == 2 && args[0].equalsIgnoreCase("remove")) {
                if (sender.hasPermission("voidbounty.admin") || sender.isOp()) {
                    Player target = Bukkit.getPlayer(args[1]);
                    if (target != null) {
                        setPlayerBounty(target.getName(), 0);
                        sender.sendMessage(ChatUtils.colorize("&aRemoved bounty for " + target.getName() + "."));
                    } else {
                        sender.sendMessage(ChatUtils.colorize("&cPlayer not found."));
                    }
                } else {
                    sender.sendMessage(ChatUtils.colorize("&cYou do not have permission to use this command."));
                }
                return true;
            }

            // Handle invalid commands
            sender.sendMessage(ChatUtils.colorize("&cUsage: /bounty <set|remove|player_name|leaderboard|addrole> [arguments]"));
            return true;
        }
        return false;
    }

    private void displayLeaderboard(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "=== Bounty Leaderboard ===");

        if (!playerDataConfig.contains("players")) {
            sender.sendMessage(ChatColor.RED + "No players found.");
            return;
        }

        // Create a map to store player names and their bounties
        Map<String, Double> playerBounties = new HashMap<>();
        for (String playerName : playerDataConfig.getConfigurationSection("players").getKeys(false)) {
            double bounty = getPlayerBounty(playerName);
            playerBounties.put(playerName, bounty);
        }

        // Sort players by bounty in descending order
        List<Map.Entry<String, Double>> sortedPlayers = new ArrayList<>(playerBounties.entrySet());
        sortedPlayers.sort((a, b) -> Double.compare(b.getValue(), a.getValue())); // Sort by bounty

        // Display the top 10 players
        int rank = 1;
        for (Map.Entry<String, Double> entry : sortedPlayers) {
            if (rank > 10) break; // Only show top 10 players
            String playerName = entry.getKey();
            double bounty = entry.getValue();
            sender.sendMessage(ChatUtils.colorize("&6#" + rank + " " + playerName + ": " + ChatUtils.formatBounty(bounty)));
            rank++;
        }
    }
}