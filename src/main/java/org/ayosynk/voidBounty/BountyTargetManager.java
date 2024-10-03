package org.ayosynk.voidBounty;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BountyTargetManager {

    private final Map<UUID, Double> bountyTargets;

    public BountyTargetManager() {
        this.bountyTargets = new HashMap<>();
    }

    // Set a bounty target for a player
    public void setBountyTarget(Player player, double amount) {
        if (player != null && amount > 0) {
            bountyTargets.put(player.getUniqueId(), amount);
        } else if (amount == 0) {
            // If bounty amount is zero, remove the player from bounty targets
            clearBountyTarget(player);
        }
    }

    // Get the bounty target for a player
    public double getBountyTarget(Player player) {
        if (player != null) {
            return bountyTargets.getOrDefault(player.getUniqueId(), 0.0);
        }
        return 0.0;
    }

    // Remove a player's bounty target
    public void clearBountyTarget(Player player) {
        if (player != null) {
            bountyTargets.remove(player.getUniqueId());
        }
    }

    // Check if a player has a bounty target
    public boolean hasBountyTarget(Player player) {
        return player != null && bountyTargets.containsKey(player.getUniqueId());
    }

    // Optional: Save bounty targets (can be implemented depending on your use case)
    // public void saveBountyTargets() { ... }

    // Optional: Load bounty targets (can be implemented depending on your use case)
    // public void loadBountyTargets() { ... }
}
