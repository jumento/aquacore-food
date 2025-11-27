package com.aquacore.food.commands;

import com.aquacore.food.AquaCoreFood;
import com.aquacore.food.data.PlayerDataManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FoodCommand implements CommandExecutor, TabCompleter {

    private final AquaCoreFood plugin;

    public FoodCommand(AquaCoreFood plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
            @NotNull String[] args) {
        if (args.length == 0) {
            sender.sendMessage("§bAquaCore-Food §7v" + plugin.getDescription().getVersion());
            sender.sendMessage("§7Use §f/aquafood help §7for commands.");
            return true;
        }

        String sub = args[0].toLowerCase();
        PlayerDataManager data = plugin.getPlayerDataManager();

        switch (sub) {
            case "help":
                sendHelp(sender);
                break;
            case "carbs":
                if (sender instanceof Player p) {
                    sender.sendMessage("§eCarbohydrates: §f" + data.getCarbs(p));
                } else
                    sender.sendMessage("§cOnly players.");
                break;
            case "vit":
                if (sender instanceof Player p) {
                    sender.sendMessage("§eVitamins: §f" + data.getVit(p));
                } else
                    sender.sendMessage("§cOnly players.");
                break;
            case "prot":
                if (sender instanceof Player p) {
                    sender.sendMessage("§eProteins: §f" + data.getProt(p));
                } else
                    sender.sendMessage("§cOnly players.");
                break;
            case "set":
                handleSet(sender, args);
                break;
            case "add":
                handleAdd(sender, args);
                break;
            case "reload":
                handleReload(sender);
                break;
            default:
                sender.sendMessage("§cUnknown command.");
                break;
        }

        return true;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage("§bAquaCore-Food Help:");
        sender.sendMessage("§f/aquafood carbs §7- Show carbohydrates");
        sender.sendMessage("§f/aquafood vit §7- Show vitamins");
        sender.sendMessage("§f/aquafood prot §7- Show proteins");
        sender.sendMessage("§f/aquafood set <stat> <val> <player> §7- Set stat");
        sender.sendMessage("§f/aquafood add <stat> <val> <player> §7- Add/Sub stat");
        sender.sendMessage("§f/aquafood reload §7- Reload config");
    }

    private void handleSet(CommandSender sender, String[] args) {
        if (!sender.hasPermission("aquafood.admin")) {
            sender.sendMessage("§cNo permission.");
            return;
        }
        if (args.length < 4) {
            sender.sendMessage("§cUsage: /aquafood set <stat> <value> <player>");
            return;
        }
        String stat = args[1];
        int val;
        try {
            val = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            sender.sendMessage("§cInvalid number.");
            return;
        }
        Player target = Bukkit.getPlayer(args[3]);
        if (target == null) {
            sender.sendMessage("§cPlayer not found.");
            return;
        }

        switch (stat.toLowerCase()) {
            case "carbs":
            case "carbohydrates":
                plugin.getPlayerDataManager().setCarbs(target, val);
                break;
            case "prot":
            case "proteins":
                plugin.getPlayerDataManager().setProt(target, val);
                break;
            case "vit":
            case "vitamins":
                plugin.getPlayerDataManager().setVit(target, val);
                break;
            default:
                sender.sendMessage("§cInvalid stat. Use carbs, prot, or vit.");
                return;
        }
        sender.sendMessage("§aSet " + stat + " to " + val + " for " + target.getName());
    }

    private void handleAdd(CommandSender sender, String[] args) {
        if (!sender.hasPermission("aquafood.admin")) {
            sender.sendMessage("§cNo permission.");
            return;
        }
        if (args.length < 4) {
            sender.sendMessage("§cUsage: /aquafood add <stat> <value> <player>");
            return;
        }
        String stat = args[1];
        int val;
        try {
            val = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            sender.sendMessage("§cInvalid number.");
            return;
        }
        Player target = Bukkit.getPlayer(args[3]);
        if (target == null) {
            sender.sendMessage("§cPlayer not found.");
            return;
        }

        plugin.getPlayerDataManager().addStat(target, stat, val);
        sender.sendMessage("§aAdded " + val + " to " + stat + " for " + target.getName());
    }

    private void handleReload(CommandSender sender) {
        if (!sender.hasPermission("aquafood.admin")) {
            sender.sendMessage("§cNo permission.");
            return;
        }
        plugin.getConfigManager().reload();
        sender.sendMessage("§aConfiguration reloaded.");
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
            @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            return Arrays.asList("help", "carbs", "vit", "prot", "set", "add", "reload");
        }
        if (args.length == 2 && (args[0].equalsIgnoreCase("set") || args[0].equalsIgnoreCase("add"))) {
            return Arrays.asList("carbs", "vit", "prot");
        }
        if (args.length == 4 && (args[0].equalsIgnoreCase("set") || args[0].equalsIgnoreCase("add"))) {
            return null; // Player names
        }
        return new ArrayList<>();
    }
}
