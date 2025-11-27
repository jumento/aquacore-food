package com.aquacore.food.listeners;

import com.aquacore.food.AquaCoreFood;
import com.aquacore.food.config.ConfigManager;
import com.aquacore.food.data.PlayerDataManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.concurrent.ThreadLocalRandom;

public class DamageListener implements Listener {

    private final AquaCoreFood plugin;

    public DamageListener(AquaCoreFood plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        ConfigManager config = plugin.getConfigManager();
        PlayerDataManager data = plugin.getPlayerDataManager();

        reduceStat(player, data, "carbohydrates", config.getDamageMax("carbohydrates"));
        reduceStat(player, data, "proteins", config.getDamageMax("proteins"));
        reduceStat(player, data, "vitamins", config.getDamageMax("vitamins"));
    }

    private void reduceStat(Player player, PlayerDataManager data, String stat, int maxReduction) {
        if (maxReduction <= 0)
            return;
        int reduction = ThreadLocalRandom.current().nextInt(maxReduction + 1);
        if (reduction > 0) {
            data.addStat(player, stat, -reduction);
        }
    }
}
