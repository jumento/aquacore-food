package com.aquacore.food.config;

import com.aquacore.food.AquaCoreFood;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;

public class ConfigManager {

    private final AquaCoreFood plugin;
    private Map<Integer, RegenRule> regenRules = new HashMap<>();
    private Map<String, Integer> damageSettings = new HashMap<>();
    private int replenishDelay = 5;
    private int replenishAmount = 1;
    private int defaultStatsOnDeath = 25;

    public ConfigManager(AquaCoreFood plugin) {
        this.plugin = plugin;
        updateConfig();
        loadRegenRules();
        loadDamageSettings();
        loadReplenishSettings();
    }

    public void reload() {
        plugin.reloadConfig();
        updateConfig();
        loadRegenRules();
        loadDamageSettings();
        loadReplenishSettings();
    }

    private void updateConfig() {
        plugin.saveDefaultConfig();
        FileConfiguration config = plugin.getConfig();
        int version = config.getInt("config-version", 0);

        // If version is less than 2
        if (version < 2) {
            config.set("config-version", 2);
            if (!config.contains("food-replenish.delay"))
                config.set("food-replenish.delay", 5);
            if (!config.contains("food-replenish.amount"))
                config.set("food-replenish.amount", 1);
            if (!config.contains("default-stats-on-death"))
                config.set("default-stats-on-death", 25);
            plugin.saveConfig();
            plugin.getLogger().info("Config updated to version 2.");
        }
    }

    private void loadRegenRules() {
        regenRules.clear();
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("regeneration");
        if (section == null)
            return;

        for (String key : section.getKeys(false)) {
            try {
                int percentage = Integer.parseInt(key);
                double power = section.getDouble(key + ".power");
                double length = section.getDouble(key + ".length");
                int food = section.getInt(key + ".food", 0);
                int interval = section.getInt(key + ".interval", 0);
                regenRules.put(percentage, new RegenRule(power, length, food, interval));
            } catch (NumberFormatException e) {
                plugin.getLogger().warning("Invalid percentage key in regeneration config: " + key);
            }
        }
    }

    private void loadDamageSettings() {
        damageSettings.clear();
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("damage");
        if (section == null)
            return;

        for (String key : section.getKeys(false)) {
            damageSettings.put(key, section.getInt(key));
        }
    }

    private void loadReplenishSettings() {
        replenishDelay = plugin.getConfig().getInt("food-replenish.delay", 5);
        replenishAmount = plugin.getConfig().getInt("food-replenish.amount", 1);
        defaultStatsOnDeath = plugin.getConfig().getInt("default-stats-on-death", 25);
    }

    public RegenRule getRegenRule(int average) {
        // Find the closest lower or equal key (0, 10, 20...)
        int key = (average / 10) * 10;
        return regenRules.getOrDefault(key, new RegenRule(0, 0, 0, 0));
    }

    public int getDecayFrequency(String stat) {
        return plugin.getConfig().getInt("decay." + stat + ".frequency", 60);
    }

    public int getDecayAmount(String stat) {
        return plugin.getConfig().getInt("decay." + stat + ".amount", 1);
    }

    public int getDamageMax(String stat) {
        return damageSettings.getOrDefault(stat, 0);
    }

    public int getReplenishDelay() {
        return replenishDelay;
    }

    public int getReplenishAmount() {
        return replenishAmount;
    }

    public int getDefaultStatsOnDeath() {
        return defaultStatsOnDeath;
    }

    public static class RegenRule {
        public final double power;
        public final double length;
        public final int food;
        public final int interval;

        public RegenRule(double power, double length, int food, int interval) {
            this.power = power;
            this.length = length;
            this.food = food;
            this.interval = interval;
        }
    }
}
