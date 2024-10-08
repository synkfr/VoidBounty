package org.ayosynk.voidBounty;

import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class MobListener implements Listener {

    private VoidBounty plugin;

    public MobListener(VoidBounty plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onMobDeath(EntityDeathEvent event) {
        if (event.getEntity() instanceof Mob) { // Check if the entity is a mob
            String mobName = event.getEntityType().toString(); // Get the mob type
            double bounty = plugin.getMobBounties().getOrDefault(mobName.toUpperCase(), 0.0); // Get the bounty for the mob
            if (bounty > 0) {
                // Assuming you have a way to get the player who killed the mob
                Player killer = event.getEntity().getKiller();
                if (killer != null) {
                    double currentBounty = plugin.getPlayerBounty(killer.getName());
                    double newBounty = currentBounty + bounty;
                    plugin.setPlayerBounty(killer.getName(), newBounty);
                    killer.sendMessage(VoidBounty.ChatUtils.colorize("&aYou have gained a bounty of " + VoidBounty.ChatUtils.formatBounty(bounty) + " for killing a " + mobName + "!"));
                }
            }
        }
    }
}
