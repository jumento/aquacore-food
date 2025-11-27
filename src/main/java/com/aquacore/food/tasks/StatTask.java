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

        checkAndDecay(player, "carbohydrates", data.getCarbs(player), config.getDecayFrequency("carbohydrates"), config.getDecayAmount("carbohydrates"));
        checkAndDecay(player, "proteins", data.getProt(player), config.getDecayFrequency("proteins"), config.getDecayAmount("proteins"));
        checkAndDecay(player, "vitamins", data.getVit(player), config.getDecayFrequency("vitamins"), config.getDecayAmount("vitamins"));
    }

    private void checkAndDecay(Player player, String stat, int currentVal, int freq, int amount) {
        // Simple modulo check based on global time might desync per player, but it's efficient.
        // For per-player timers, we'd need to store last decay time.
        // For MVP, let's use global tick count. If freq is 45s, we check if ticks % (45*20) == 0.
        // Wait, ticks is in ticks. freq is in seconds.
        if (ticks % (freq * 20L) == 0) {
            plugin.getPlayerDataManager().addStat(player, stat, -amount);
        }
    }

    private void handleRegen(Player player) {
        int avg = plugin.getPlayerDataManager().getAverage(player);
        ConfigManager.RegenRule rule = plugin.getConfigManager().getRegenRule(avg);

        // "potencia de 1 y una longitud de 0.010"
        // We interpreted this as: Power = Regen Amount/Amplifier? Length = Saturation to add?
        // Let's implement:
        // If Power > 0, give Regen effect? Or heal directly?
        // If Length > 0, add Saturation?
        
        // Let's assume Power is direct HP heal? Or Regen Effect Amplifier?
        // If it's a plugin for "Food", maybe it heals hunger/saturation?
        // "intervenir la saturacion... solo es incrementativa en base a 3 estadisticas"
        // So this logic MUST add saturation.
        
        if (rule.length > 0) {
            float newSat = (float) (player.getSaturation() + rule.length);
            player.setSaturation(Math.min(20f, newSat)); // Max saturation is usually 20 or matches food level.
        }
        
        // What about Power? "potencia de 1". Maybe it's regeneration potion effect?
        // If we apply regen every second, we should give a short duration regen effect.
        if (rule.power > 0) {
             // Power 1 usually means Amplifier 0 (Regen I). Power 2 = Amp 1.
             // Let's assume config value is the raw amplifier + 1? Or just amplifier?
             // Let's use it as amplifier.
             // Duration 2 seconds to ensure continuity.
             // But standard regen is slow.
             // If the user wants custom regen speed, we might need to heal manually.
             // Let's stick to PotionEffect for visual consistency if they want "Regeneration".
             // But if "Power" is 1, maybe they mean 1 HP?
             // Given "Regeneracion", likely the effect.
             
             // Let's try applying Regen effect.
             // If power is 1, let's assume amplifier 0.
             // If config says 1, we use 0. If 2, use 1.
             int amp = (int) rule.power - 1;
             if (amp >= 0) {
                 player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 40, amp, true, false));
             }
        }
    }

    private void handleEffects(Player player) {
        int avg = plugin.getPlayerDataManager().getAverage(player);
        ConfigurationSection effectsSec = plugin.getConfig().getConfigurationSection("effects");
        if (effectsSec == null) return;

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
                        } catch (Exception ignored) {}
                    }
                }

                // Apply particles
                String particleStr = effectsSec.getString(key + ".particles");
                if (particleStr != null && !particleStr.isEmpty()) {
                    try {
                        Particle particle = Particle.valueOf(particleStr);
                        player.spawnParticle(particle, player.getLocation().add(0, 1, 0), 1, 0.5, 0.5, 0.5, 0.05);
                    } catch (Exception ignored) {}
                }
            }
        }
    }
}
