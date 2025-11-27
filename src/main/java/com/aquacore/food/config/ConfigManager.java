package com.aquacore.food.config;

import com.aquacore.food.AquaCoreFood;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;

public class ConfigManager {

    private final AquaCoreFood plugin;
    private Map<Integer, RegenRule> regenRules = new HashMap<>();

    public ConfigManager(AquaCoreFood plugin) {
        this.plugin = plugin;
        loadRegenRules();
    }

    public void reload() {
        plugin.reloadConfig();
        loadRegenRules();
    }

    private void loadRegenRules() {
        regenRules.clear();
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("regeneration");
        if (section == null) return;

        for (String key : section.getKeys(false)) {
            try {
                int percentage = Integer.parseInt(key);
                double power = section.getDouble(key + ".power");
                double length = section.getDouble(key + ".length");
                regenRules.put(percentage, new RegenRule(power, length));
            } catch (NumberFormatException e) {
                plugin.getLogger().warning("Invalid percentage key in regeneration config: " + key);
            }
        }
    }

    public RegenRule getRegenRule(int average) {
        // Find the closest lower or equal key (0, 10, 20...)
        int key = (average / 10) * 10;
        return regenRules.getOrDefault(key, new RegenRule(0, 0));
    }

    public int getDecayFrequency(String stat) {
        return plugin.getConfig().getInt("decay." + stat + ".frequency", 60);
    }

    public int getDecayAmount(String stat) {
        return plugin.getConfig().getInt("decay." + stat + ".amount", 1);
    }

    public static class RegenRule {
        public final double power;
        public final double length;

        public RegenRule(double power, double length) {
            this.power = power;
            this.length = length;
        }
    }
}
