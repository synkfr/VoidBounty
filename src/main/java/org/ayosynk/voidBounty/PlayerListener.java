package org.ayosynk.voidBounty;

import org.ayosynk.voidBounty.VoidBounty;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class PlayerListener implements Listener {

    private final VoidBounty plugin;

    public PlayerListener(VoidBounty plugin) {
        this.plugin = plugin;
    }

    // Get the player's bounty from playerdata.yml
    public double getPlayerBounty(Player player) {
        File playerDataFile = new File(plugin.getDataFolder(), "playerdata.yml");
        YamlConfiguration playerData = YamlConfiguration.loadConfiguration(playerDataFile);

        String playerKey = player.getUniqueId().toString();
        return playerData.getDouble("players." + playerKey + ".bounty", 0.0);
    }

    // Set the player's bounty in playerdata.yml
    public void setPlayerBounty(Player player, double bounty) {
        File playerDataFile = new File(plugin.getDataFolder(), "playerdata.yml");
        YamlConfiguration playerData = YamlConfiguration.loadConfiguration(playerDataFile);

        String playerKey = player.getUniqueId().toString();
        playerData.set("players." + playerKey + ".bounty", bounty);

        try {
            playerData.save(playerDataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        VoidBounty plugin = JavaPlugin.getPlugin(VoidBounty.class);

        double bounty = plugin.getPlayerBounty(player.getName());
        String joinMessage = plugin.getJoinMessageForBounty(bounty);

        if (joinMessage != null) {
            String message = VoidBounty.ChatUtils.colorize(joinMessage.replace("{player}", player.getName()).replace("{bounty}", VoidBounty.ChatUtils.formatBounty(bounty)));
            Bukkit.broadcastMessage(message); // Broadcast to all players
        }
    }


    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        double currentBounty = plugin.getPlayerBounty(player.getName());
        double newBounty = currentBounty * 0.5; // Lose 50%
        plugin.setPlayerBounty(player.getName(), newBounty);
        player.sendMessage(VoidBounty.ChatUtils.colorize("&cYou lost 50% of your bounty upon death!"));
    }
}
