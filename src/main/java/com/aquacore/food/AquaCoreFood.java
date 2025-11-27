package com.aquacore.food;

import com.aquacore.food.commands.FoodCommand;
import com.aquacore.food.config.ConfigManager;
import com.aquacore.food.data.PlayerDataManager;
import com.aquacore.food.listeners.DamageListener;
import com.aquacore.food.listeners.FoodListener;
import com.aquacore.food.papi.PAPIExpansion;
import com.aquacore.food.tasks.StatTask;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class AquaCoreFood extends JavaPlugin {

    private static AquaCoreFood instance;
    private ConfigManager configManager;
    private PlayerDataManager playerDataManager;

    @Override
    public void onEnable() {
        instance = this;

        // Load Config
        saveDefaultConfig();
        this.configManager = new ConfigManager(this);

        // Initialize Managers
        this.playerDataManager = new PlayerDataManager(this);

        // Register Listeners
        getServer().getPluginManager().registerEvents(new FoodListener(this), this);
        getServer().getPluginManager().registerEvents(new DamageListener(this), this);

        // Register Commands
        getCommand("aquafood").setExecutor(new FoodCommand(this));

        // Register Tasks
        new StatTask(this).runTaskTimer(this, 20L, 20L); // Run every second, internal logic handles frequencies

        // Register PlaceholderAPI
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PAPIExpansion(this).register();
        }

        getLogger().info("AquaCore-Food has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("AquaCore-Food has been disabled!");
    }

    public static AquaCoreFood getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public PlayerDataManager getPlayerDataManager() {
        return playerDataManager;
    }
}
