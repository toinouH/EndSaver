package fr.toinouh.endSaver;

import org.bukkit.plugin.java.JavaPlugin;

public class EndSaver extends JavaPlugin {  // Can't be final with MockBukkit

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new PlayerVoidListener(this), this);
        getLogger().info("EndVoidTeleportPlugin has been enabled.");
    }

    @Override
    public void onDisable() {
        getLogger().info("EndVoidTeleportPlugin has been disabled.");
    }
}