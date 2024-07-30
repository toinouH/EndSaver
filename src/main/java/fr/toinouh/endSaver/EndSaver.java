package fr.toinouh.endSaver;

import org.bukkit.plugin.java.JavaPlugin;

public final class EndSaver extends JavaPlugin {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new PlayerVoidListener(this), this);
        getLogger().info("EndVoidTeleportPlugin has been enabled.");
        // Plugin startup logic

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("EndVoidTeleportPlugin has been disabled.");
    }
}
