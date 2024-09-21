package fr.toinouh.endSaver;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;

import jdk.jfr.Description;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
//import org.bukkit.damage.DamageSource;   // Still not supported by the new api with MockBukkit
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PlayerVoidListenerTest {

    private PlayerVoidListener listener;

    @Mock
    private World endWorld;

    @Mock
    private Player player;

    private static ServerMock server;

    @BeforeAll
    public static void setUpMockBukkit() {
        server = MockBukkit.mock();
    }

    @AfterAll
    public static void tearDownMockBukkit() {
        MockBukkit.unmock();
    }

    @BeforeEach
    public void setUp() {
        EndSaver plugin = MockBukkit.load(EndSaver.class);
        listener = new PlayerVoidListener(plugin);
        when(player.getWorld()).thenReturn(endWorld);
    }

    @Test
    void playerTeleportedToOverworldWhenFallingIntoVoid() {
        Location location = new Location(endWorld, 0, -61, 0);
        when(player.getLocation()).thenReturn(location);

        Vector velocity = new Vector(0, -1, 0);
        when(player.getVelocity()).thenReturn(velocity);


        server.addSimpleWorld("world");
        when(endWorld.getEnvironment()).thenReturn(World.Environment.THE_END);
        when(player.teleportAsync(any(Location.class))).thenReturn(CompletableFuture.completedFuture(true));

        PlayerMoveEvent event = new PlayerMoveEvent(player, location, location);
        listener.onPlayerMove(event);

        verify(player).teleportAsync(any(Location.class));
        verify(player).sendMessage("You were falling into the void and have been teleported back to the overworld.");
    }

    @Test
    void playerNotTeleportedWhenNotInEnd() {
        when(endWorld.getEnvironment()).thenReturn(World.Environment.NORMAL);
        when(player.getVelocity()).thenReturn(new Vector(0, -100, 0));

        PlayerMoveEvent event = new PlayerMoveEvent(player, player.getLocation(), player.getLocation());
        listener.onPlayerMove(event);

        verify(player, never()).teleportAsync(any(Location.class));
    }

    @Test
    void playerNotTeleportedWhenAboveVoid() {
        Location location = new Location(endWorld, 0, 10, 0);
        when(player.getVelocity()).thenReturn(new Vector(0, 0, 0));

        PlayerMoveEvent event = new PlayerMoveEvent(player, location, location);
        listener.onPlayerMove(event);

        verify(player, never()).teleportAsync(any(Location.class));
    }

    @Test
    void voidDamageCancelledInEnd() {
        when(player.getWorld()).thenReturn(endWorld);
        when(endWorld.getEnvironment()).thenReturn(World.Environment.THE_END);

        EntityDamageEvent event = new EntityDamageEvent((Entity) player, EntityDamageEvent.DamageCause.VOID, 10);
        listener.onEntityDamage(event);

        assertTrue(event.isCancelled());
    }

    @Test
    @Description("The damage should not be cancelled if the player is not void damage.")
    void nonVoidDamageNotCancelled() {
        EntityDamageEvent event = new EntityDamageEvent(player, EntityDamageEvent.DamageCause.FALL, 10);
        listener.onEntityDamage(event);

        assertFalse(event.isCancelled());
    }

    @Test
    @Description("The damage should not be cancelled if the player is not in the End dimension.")
    void voidDamageNotCancelledOutsideEnd() {
        when(player.getWorld()).thenReturn(endWorld);
        when(endWorld.getEnvironment()).thenReturn(World.Environment.NORMAL);

        EntityDamageEvent event = new EntityDamageEvent(player, EntityDamageEvent.DamageCause.VOID, 10.0);
        listener.onEntityDamage(event);

        assertFalse(event.isCancelled());
    }
}