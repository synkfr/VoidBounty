package org.ayosynk.voidBounty;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;

import java.util.HashMap;
import java.util.Map;

public class MobBountyManager {

    private final Map<EntityType, Double> mobBounties;

    public MobBountyManager(FileConfiguration config) {
        mobBounties = new HashMap<>();
        loadMobBounties(config);
    }

    public void reloadConfig(FileConfiguration config) {
        loadMobBounties(config);
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
                    // Invalid mob type or bounty amount, ignore this entry
                }
            }
        }
    }

    public double getBountyForMob(EntityType mobType) {
        return mobBounties.getOrDefault(mobType, 0.0);
    }
}
