package org.ayosynk.voidBounty;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MobBountyManager {

    private final Map<EntityType, Double> mobBounties;
    private List<String> mobBlacklist;

    public MobBountyManager(FileConfiguration config) {
        mobBounties = new HashMap<>();
        loadMobBounties(config);
        loadBlacklist(config); // Load the blacklist on initialization
    }

    public void reloadConfig(FileConfiguration config) {
        loadMobBounties(config);
        loadBlacklist(config); // Reload blacklist when config reloads
    }

    private void loadMobBounties(FileConfiguration config) {
        mobBounties.clear();
        for (String mobEntry : config.getStringList("mob-bounties")) {
            String[] parts = mobEntry.split(":");
            if (parts.length == 2) {
                try {
                    EntityType mobType = EntityType.valueOf(parts[0].toUpperCase());
                    double bountyAmount = Double.parseDouble(parts[1]);
                    mobBounties.put(mobType, bountyAmount);
                } catch (IllegalArgumentException e) {
                    // Log invalid entries for better debugging
                    Bukkit.getLogger().warning("Invalid mob-bounty entry: " + mobEntry);
                }
            }
        }
    }

    private void loadBlacklist(FileConfiguration config) {
        mobBlacklist = config.getStringList("mob-blacklist");
    }

    public double getBountyForMob(EntityType mobType) {
        return mobBounties.getOrDefault(mobType, 0.0);
    }

    public boolean isBlacklisted(EntityType mobType) {
        return mobBlacklist.contains(mobType.name());
    }
}
