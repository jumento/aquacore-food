package com.aquacore.food.data;

import com.aquacore.food.AquaCoreFood;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerDataManager {

    private final AquaCoreFood plugin;
    private final NamespacedKey carbsKey;
    private final NamespacedKey protKey;
    private final NamespacedKey vitKey;
    private final Map<UUID, Long> lastFoodRegen = new HashMap<>();
    private final Map<UUID, Long> lastReplenish = new HashMap<>();
    private final Map<UUID, Map<String, Long>> commandCooldowns = new HashMap<>();

    public PlayerDataManager(AquaCoreFood plugin) {
        this.plugin = plugin;
        this.carbsKey = new NamespacedKey(plugin, "carbohydrates");
        this.protKey = new NamespacedKey(plugin, "proteins");
        this.vitKey = new NamespacedKey(plugin, "vitamins");
    }

    public long getLastFoodRegen(Player player) {
        return lastFoodRegen.getOrDefault(player.getUniqueId(), 0L);
    }

    public void setLastFoodRegen(Player player, long time) {
        lastFoodRegen.put(player.getUniqueId(), time);
    }

    public long getLastReplenish(Player player) {
        return lastReplenish.getOrDefault(player.getUniqueId(), 0L);
    }

    public void setLastReplenish(Player player, long time) {
        lastReplenish.put(player.getUniqueId(), time);
    }

    public long getLastCommandExecution(Player player, String command) {
        return commandCooldowns.computeIfAbsent(player.getUniqueId(), k -> new HashMap<>())
                .getOrDefault(command, 0L);
    }

    public void setLastCommandExecution(Player player, String command, long time) {
        commandCooldowns.computeIfAbsent(player.getUniqueId(), k -> new HashMap<>())
                .put(command, time);
    }

    public int getCarbs(Player player) {
        return getStat(player, carbsKey);
    }

    public void setCarbs(Player player, int value) {
        setStat(player, carbsKey, value);
    }

    public int getProt(Player player) {
        return getStat(player, protKey);
    }

    public void setProt(Player player, int value) {
        setStat(player, protKey, value);
    }

    public int getVit(Player player) {
        return getStat(player, vitKey);
    }

    public void setVit(Player player, int value) {
        setStat(player, vitKey, value);
    }

    private int getStat(Player player, NamespacedKey key) {
        PersistentDataContainer container = player.getPersistentDataContainer();
        return container.getOrDefault(key, PersistentDataType.INTEGER, 100);
    }

    private void setStat(Player player, NamespacedKey key, int value) {
        // Clamp between 0 and 100
        int clamped = Math.max(0, Math.min(100, value));
        player.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, clamped);
    }

    public void addStat(Player player, String stat, int amount) {
        switch (stat.toLowerCase()) {
            case "carbohydrates":
            case "carbs":
                setCarbs(player, getCarbs(player) + amount);
                break;
            case "proteins":
            case "prot":
                setProt(player, getProt(player) + amount);
                break;
            case "vitamins":
            case "vit":
                setVit(player, getVit(player) + amount);
                break;
        }
    }

    public int getAverage(Player player) {
        return (getCarbs(player) + getProt(player) + getVit(player)) / 3;
    }
}
