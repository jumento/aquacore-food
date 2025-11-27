package com.aquacore.food.listeners;

import com.aquacore.food.AquaCoreFood;
import com.aquacore.food.config.ConfigManager;
import com.aquacore.food.data.PlayerDataManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public class DeathListener implements Listener {

    private final AquaCoreFood plugin;

    public DeathListener(AquaCoreFood plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        PlayerDataManager data = plugin.getPlayerDataManager();
        ConfigManager config = plugin.getConfigManager();
        int defaultStats = config.getDefaultStatsOnDeath();

        data.setCarbs(event.getPlayer(), defaultStats);
        data.setProt(event.getPlayer(), defaultStats);
        data.setVit(event.getPlayer(), defaultStats);
    }
}
