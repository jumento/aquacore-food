package com.aquacore.food.tasks;

import com.aquacore.food.AquaCoreFood;
import com.aquacore.food.config.ConfigManager;
import com.aquacore.food.data.PlayerDataManager;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class StatTask extends BukkitRunnable {

    private final AquaCoreFood plugin;
    private long ticks = 0;

    public StatTask(AquaCoreFood plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        ticks += 20; // We run every 20 ticks (1 second)

        for (Player player : Bukkit.getOnlinePlayers()) {
            handleDecay(player);
            handleRegen(player);
            handleReplenish(player);
            handleEffects(player);
        }

    }

    private void handleDecay(Player player) {
        PlayerDataManager data = plugin.getPlayerDataManager();
        ConfigManager config = plugin.getConfigManager();

        boolean sprinting = player.isSprinting();
        int freqMod = sprinting ? 10 : 1;

        // Count stats at zero
        int zeroStats = 0;
        if (data.getCarbs(player) <= 0)
            zeroStats++;
        if (data.getProt(player) <= 0)
            zeroStats++;
        if (data.getVit(player) <= 0)
            zeroStats++;

        // Calculate penalty modifier
        int penaltyMod = 1;
        if (zeroStats == 1)
            penaltyMod = 2;
        else if (zeroStats >= 2)
            penaltyMod = 5;

        // Calculate amount modifiers based on sprinting and penalty
        int carbMod = (sprinting ? config.getDamageMax("carbohydrates") : 1) * penaltyMod;
        if (ticks % (freq * 20L) == 0) {
            plugin.getPlayerDataManager().addStat(player, stat, -amount);
        }
    }

    private void handleRegen(Player player) {
        int avg = plugin.getPlayerDataManager().getAverage(player);
        ConfigManager.RegenRule rule = plugin.getConfigManager().getRegenRule(avg);

        // Saturation (Length)
        if (rule.length > 0) {
            float newSat = (float) (player.getSaturation() + rule.length);
            player.setSaturation(Math.min(20f, newSat));
        }

        // Food (Food amount) with Interval
        if (rule.food > 0 && rule.interval > 0) {
            long lastRegen = plugin.getPlayerDataManager().getLastFoodRegen(player);
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastRegen >= rule.interval * 1000L) {
                int newFood = Math.min(20, player.getFoodLevel() + rule.food);
                player.setFoodLevel(newFood);
                plugin.getPlayerDataManager().setLastFoodRegen(player, currentTime);
            }
        }

        // Regeneration Effect (Power)
        if (rule.power > 0) {
            int amp = (int) rule.power - 1;
            if (amp >= 0) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 40, amp, true, false));
            }
        }
    }

    private void handleReplenish(Player player) {
        if (player.getFoodLevel() >= 20)
            return;

        ConfigManager config = plugin.getConfigManager();
        PlayerDataManager data = plugin.getPlayerDataManager();

        // If all stats are zero, do not replenish (prevent free food bug)
        if (data.getCarbs(player) <= 0 && data.getProt(player) <= 0 && data.getVit(player) <= 0) {
            return;
        }

        long lastReplenish = data.getLastReplenish(player);
        long currentTime = System.currentTimeMillis();

        if (currentTime - lastReplenish >= config.getReplenishDelay() * 1000L) {
            // Sacrifice stats
            int carbDmg = config.getDamageMax("carbohydrates");
            int protDmg = config.getDamageMax("proteins");
            int vitDmg = config.getDamageMax("vitamins");

            data.addStat(player, "carbohydrates", -carbDmg);
            data.addStat(player, "proteins", -protDmg);
            data.addStat(player, "vitamins", -vitDmg);

            // Restore food
            int newFood = Math.min(20, player.getFoodLevel() + config.getReplenishAmount());
            player.setFoodLevel(newFood);

            data.setLastReplenish(player, currentTime);
        }
    }

    private void handleEffects(Player player) {
        int avg = plugin.getPlayerDataManager().getAverage(player);
        ConfigurationSection effectsSec = plugin.getConfig().getConfigurationSection("effects");
        if (effectsSec == null)
            return;

        for (String key : effectsSec.getKeys(false)) {
            int min = effectsSec.getInt(key + ".min");
            int max = effectsSec.getInt(key + ".max");

            if (avg >= min && avg <= max) {
                // Apply effects
                for (String effectStr : effectsSec.getStringList(key + ".effects")) {
                    String[] parts = effectStr.split(";");
                    if (parts.length >= 2) {
                        try {
                            PotionEffectType type = PotionEffectType.getByName(parts[0]);
                            int amp = Integer.parseInt(parts[1]);
                            if (type != null) {
                                player.addPotionEffect(new PotionEffect(type, 40, amp, true, false));
                            }
                        } catch (Exception ignored) {
                        }
                    }
                }

                // Apply particles
                String particleStr = effectsSec.getString(key + ".particles");
                if (particleStr != null && !particleStr.isEmpty()) {
                    try {
                        Particle particle = Particle.valueOf(particleStr);
                        player.spawnParticle(particle, player.getLocation().add(0, 1, 0), 1, 0.5, 0.5, 0.5, 0.05);
                    } catch (Exception ignored) {
                    }
                }
            }
        }
    }
}
