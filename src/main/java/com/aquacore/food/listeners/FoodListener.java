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
        // "intervenir la saturacion nativa en minecraft para que no sea afectada por ningun item de minecraft"
        // This usually means we want to control saturation manually.
        // If the event is caused by eating, we might want to let the food level change but NOT the saturation?
        // Or maybe we cancel the event entirely and handle it manually?
        // The request says "no sea afectada por ningun item... solo por un comando".
        // But "la saturacion a su vez decae de forma normal".
        
        // If we cancel the event, food level won't change either.
        // We probably want to allow food level changes (hunger) but prevent saturation gain from items.
        // However, FoodLevelChangeEvent doesn't easily distinguish source (Item vs Exhaustion).
        // Actually, if the new food level is higher, it's likely eating.
        // If lower, it's exhaustion.
        
        if (event.getItem() != null) {
            // It's caused by an item (eating)
            // We want to prevent saturation change?
            // The event sets both food level and saturation.
            // We can modify the entity's saturation directly or cancel the saturation part if possible.
            // But FoodLevelChangeEvent doesn't have setSaturation.
            // We can set the player's saturation back to what it was after the event?
            // Or we can just set the saturation to the old value immediately.
            
            float oldSaturation = event.getEntity().getSaturation();
            
            // We let the food level change happen (so they get hunger bars filled),
            // but we force saturation to stay the same (or handle it via our logic).
            // But wait, "solo es incrementativa en base a 3 estadisticas".
            // So eating shouldn't give saturation.
            
            // We can schedule a task to reset saturation 1 tick later? 
            // Or just set it on the player immediately? 
            // Setting it on the player immediately might be overridden by the event application.
            // Actually, if we consume the item, the server logic applies food/sat.
            
            // Let's try to set the saturation back to old value 1 tick later.
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                event.getEntity().setSaturation(oldSaturation);
            });
        }
    }
}
