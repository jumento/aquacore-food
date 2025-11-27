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
            handleEffects(player);
        }
    }

    private void handleDecay(Player player) {
        PlayerDataManager data = plugin.getPlayerDataManager();
        ConfigManager config = plugin.getConfigManager();

        checkAndDecay(player, "carbohydrates", data.getCarbs(player), config.getDecayFrequency("carbohydrates"),
                config.getDecayAmount("carbohydrates"));
        checkAndDecay(player, "proteins", data.getProt(player), config.getDecayFrequency("proteins"),
                config.getDecayAmount("proteins"));
        checkAndDecay(player, "vitamins", data.getVit(player), config.getDecayFrequency("vitamins"),
                config.getDecayAmount("vitamins"));
    }

    private void checkAndDecay(Player player, String stat, int currentVal, int freq, int amount) {
        // Simple modulo check based on global time might desync per player, but it's
        // efficient.
        // For per-player timers, we'd need to store last decay time.
        // For MVP, let's use global tick count. If freq is 45s, we check if ticks %
        // (45*20) == 0.
        // Wait, ticks is in ticks. freq is in seconds.
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
