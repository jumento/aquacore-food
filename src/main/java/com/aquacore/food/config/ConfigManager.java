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

    public ConfigManager(AquaCoreFood plugin) {
        this.plugin = plugin;
        loadRegenRules();
        loadDamageSettings();
    }

    public void reload() {
        plugin.reloadConfig();
        loadRegenRules();
        loadDamageSettings();
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
