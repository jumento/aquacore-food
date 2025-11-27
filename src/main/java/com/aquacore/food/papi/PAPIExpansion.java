package com.aquacore.food.papi;

import com.aquacore.food.AquaCoreFood;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PAPIExpansion extends PlaceholderExpansion {

    private final AquaCoreFood plugin;

    public PAPIExpansion(AquaCoreFood plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "aquafood";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Antigravity";
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String params) {
        if (player == null) return "";

        switch (params.toLowerCase()) {
            case "carbs":
                return String.valueOf(plugin.getPlayerDataManager().getCarbs(player));
            case "prot":
                return String.valueOf(plugin.getPlayerDataManager().getProt(player));
            case "vit":
                return String.valueOf(plugin.getPlayerDataManager().getVit(player));
            case "average":
                return String.valueOf(plugin.getPlayerDataManager().getAverage(player));
        }

        return null;
    }
}
