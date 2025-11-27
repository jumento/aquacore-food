package com.aquacore.food.listeners;

import com.aquacore.food.AquaCoreFood;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public class FoodListener implements Listener {

    private final AquaCoreFood plugin;

    public FoodListener(AquaCoreFood plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        // "intervenir la saturacion nativa en minecraft para que no sea afectada por
        // ningun item de minecraft"
        // This usually means we want to control saturation manually.
        // If the event is caused by eating, we might want to let the food level change
        // but NOT the saturation?
        // Or maybe we cancel the event entirely and handle it manually?
        // The request says "no sea afectada por ningun item... solo por un comando".
        // But "la saturacion a su vez decae de forma normal".

        // If we cancel the event, food level won't change either.
        // We probably want to allow food level changes (hunger) but prevent saturation
        // gain from items.
        // However, FoodLevelChangeEvent doesn't easily distinguish source (Item vs
        // Exhaustion).
        // Actually, if the new food level is higher, it's likely eating.
        // If lower, it's exhaustion.

        if (event.getItem() != null) {
            event.setCancelled(true);
        }
    }
}
