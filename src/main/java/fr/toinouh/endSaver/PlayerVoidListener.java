package fr.toinouh.endSaver;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

public class PlayerVoidListener implements Listener {
    private final EndSaver plugin;

    public PlayerVoidListener(EndSaver plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        World world = player.getWorld();

        Vector velocity = player.getVelocity(); // Get the player's velocity
        // double fallingSpeed = velocity.getY();   // idk if we want to keep that since we don't really need it here
        double playerZ = velocity.getZ();
        double playerX = velocity.getX();

        // Check if the player is in the End and below y=0
        if (world.getEnvironment() == World.Environment.THE_END && player.getLocation().getY() < -60) { // Damage starts at y=-64

            // Teleport the player to the overworld at y=1024
            World overworld = Bukkit.getWorld("world"); // The name of the overworld is typically "world"
            if (overworld != null) {
                Location teleportLocation = new Location(overworld, player.getLocation().getX(), 1024, player.getLocation().getZ());
                player.teleportAsync(teleportLocation).thenRun(() -> {  // Teleport the player asynchronously because sync chunk load is pain for the server

                    Vector newVelocity = player.getVelocity();  // A new vector to store the new velocity
                    newVelocity.setY(3.98);                     // https://www.reddit.com/r/Minecraft/comments/as2rz3/blockssecond_a_player_travels_when_falling_please/
                    newVelocity.setX(playerX);                  // retrieve the player's X speed
                    newVelocity.setZ(playerZ);                  // retrieve the player's Z speed

                    player.setVelocity(newVelocity);            // apply the falling speed

                    player.sendMessage("You were falling into the void and have been teleported back to the overworld.");
                    System.out.println(player.getName() + " was teleported from the End to the Overworld.");    // Log the teleportation to the console
                    }).exceptionally(ex -> {
                        plugin.getLogger().warning("Failed to teleport player " + player.getName() + " to the overworld: " + ex.getMessage());
                        return null;
                    });
            } else {
                plugin.getLogger().warning("Overworld not found. Player could not be teleported.");
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            World world = player.getWorld();

            if (world.getEnvironment() == World.Environment.THE_END && event.getCause() == EntityDamageEvent.DamageCause.VOID) {
                event.setCancelled(true);
            }
        }
    }
}