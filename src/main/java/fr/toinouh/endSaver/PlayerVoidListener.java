package fr.toinouh.endSaver;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
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

        //Vector velocity = player.getVelocity();
        //double fallingSpeed = velocity.getY();

        // Check if the player is in the End and below y=0
        if (world.getEnvironment() == World.Environment.THE_END && player.getLocation().getY() < -60) { // Damage starts at y=-64

            // Teleport the player to the overworld at y=1024
            World overworld = Bukkit.getWorld("world"); // The name of the overworld is typically "world"
            if (overworld != null) {
                Location teleportLocation = new Location(overworld, player.getLocation().getX(), 1024, player.getLocation().getZ());
                player.teleport(teleportLocation);

                //Vector newVelocity = player.getVelocity();
                //newVelocity.setY(fallingSpeed); // Stop the player from falling in the overworld
                //player.setVelocity(newVelocity);

                player.sendMessage("You were falling into the void and have been teleported back to the overworld.");
                System.out.println("Player " + player.getName() + " was teleported from the End to the overworld.");
            } else {
                plugin.getLogger().warning("Overworld not found. Player could not be teleported.");
            }
        }
    }
}

