package org.ayosynk.voidBounty;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class BountyTargetManager {

    private HashMap<UUID, Double> bountyTargets;

    public BountyTargetManager() {
        bountyTargets = new HashMap<>();
    }

    public void setBountyTarget(Player player, double amount) {
        bountyTargets.put(player.getUniqueId(), amount);
    }

    public double getBountyTarget(Player player) {
        return bountyTargets.getOrDefault(player.getUniqueId(), 0.0);
    }

    public void clearBountyTarget(Player player) {
        bountyTargets.remove(player.getUniqueId());
    }
}
