/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 */
package com.flycountdown.commands;

import com.flycountdown.FlyCountdown;
import com.flycountdown.managers.FlightManager;
import com.flycountdown.managers.SettingsManager;
import com.flycountdown.managers.ShopManager;
import com.flycountdown.utils.MessageUtils;
import com.flycountdown.utils.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FlyCommand
implements CommandExecutor {
    private final FlyCountdown plugin;
    private final FlightManager flightManager;
    private final ShopManager shopManager;
    private final SettingsManager settingsManager;

    public FlyCommand(FlyCountdown plugin, FlightManager flightManager, ShopManager shopManager, SettingsManager settingsManager) {
        this.plugin = plugin;
        this.flightManager = flightManager;
        this.shopManager = shopManager;
        this.settingsManager = settingsManager;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        //String sub;
        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(String.valueOf(ChatColor.RED) + "Only players can use this!");
                return true;
            }
            this.flightManager.toggleFlight((Player)sender);
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "add": {
                return this.handleAdd(sender, args);
            }
            case "take": {
                return this.handleTake(sender, args);
            }
            case "check": {
                return this.handleCheck(sender, args);
            }
            case "reload": {
                return this.handleReload(sender);
            }
            case "toggle": {
                return this.handleToggle(sender, args);
            }
            case "blockworld": {
                return this.handleBlockWorld(sender, args);
            }
            case "shop": {
                return this.handleShop(sender);
            }
            case "settings": {
                return this.handleSettings(sender);
            }
        }
        sender.sendMessage(String.valueOf(ChatColor.RED) + "Unknown command!");
        return true;
    }

    private boolean handleAdd(CommandSender s, String[] args) {
        if (!s.hasPermission("FlyCountdown.admin")) {
            MessageUtils.sendMessage(s, this.format("messages.no-permission"));
            return true;
        }
        if (args.length < 3) {
            s.sendMessage(String.valueOf(ChatColor.RED) + "Usage: /fly add <player> <time>");
            return true;
        }
        Player t = Bukkit.getPlayer((String)args[1]);
        if (t == null) {
            MessageUtils.sendMessage(s, this.format("messages.player-not-found"));
            return true;
        }
        long time = TimeUtils.parseTime(args[2]);
        if (time < 0L) {
            MessageUtils.sendMessage(s, this.format("messages.invalid-time-format"));
            return true;
        }
        this.flightManager.addFlightTime(t.getUniqueId(), time);
        MessageUtils.sendMessage(s, this.flightManager.processInternal(t, this.format("messages.flight-time-added"), time));
        return true;
    }

    private boolean handleTake(CommandSender s, String[] args) {
        if (!s.hasPermission("FlyCountdown.admin")) {
            MessageUtils.sendMessage(s, this.format("messages.no-permission"));
            return true;
        }
        if (args.length < 3) {
            s.sendMessage(String.valueOf(ChatColor.RED) + "Usage: /fly take <player> <time>");
            return true;
        }
        Player t = Bukkit.getPlayer((String)args[1]);
        if (t == null) {
            MessageUtils.sendMessage(s, this.format("messages.player-not-found"));
            return true;
        }
        long time = TimeUtils.parseTime(args[2]);
        if (time < 0L) {
            MessageUtils.sendMessage(s, this.format("messages.invalid-time-format"));
            return true;
        }
        this.flightManager.removeFlightTime(t.getUniqueId(), time);
        MessageUtils.sendMessage(s, this.flightManager.processInternal(t, this.format("messages.flight-time-taken"), time));
        return true;
    }

    private boolean handleCheck(CommandSender s, String[] args) {
        Player t = null;
        if (!s.hasPermission("FlyCountdown.check")) {
            MessageUtils.sendMessage(s, this.format("messages.no-permission"));
            return true;
        }
        Player player = args.length < 2 ? (s instanceof Player ? (Player)s : null) : (t = Bukkit.getPlayer((String)args[1]));
        if (t == null) {
            MessageUtils.sendMessage(s, this.format("messages.player-not-found"));
            return true;
        }
        long time = this.flightManager.getFlightTime(t.getUniqueId());
        MessageUtils.sendMessage(s, this.flightManager.processInternal(t, this.format("messages.flight-time-check"), time));
        return true;
    }

    private boolean handleReload(CommandSender s) {
        if (!s.hasPermission("FlyCountdown.reload")) {
            MessageUtils.sendMessage(s, this.format("messages.no-permission"));
            return true;
        }
        this.flightManager.reload();
        MessageUtils.sendMessage(s, this.format("messages.config-reloaded"));
        return true;
    }

    private boolean handleToggle(CommandSender s, String[] args) {
        if (!s.hasPermission("FlyCountdown.toggle")) {
            MessageUtils.sendMessage(s, this.format("messages.no-permission"));
            return true;
        }
        if (args.length < 2) {
            s.sendMessage(String.valueOf(ChatColor.RED) + "Usage: /fly toggle <player>");
            return true;
        }
        Player t = Bukkit.getPlayer((String)args[1]);
        if (t == null) {
            MessageUtils.sendMessage(s, this.format("messages.player-not-found"));
            return true;
        }
        boolean was = this.flightManager.isFlightEnabled(t.getUniqueId());
        this.flightManager.toggleFlight(t);
        String key = was ? "messages.flight-toggled-off" : "messages.flight-toggled-on";
        MessageUtils.sendMessage(s, this.flightManager.processInternal(t, this.format(key), -1L));
        return true;
    }

    private boolean handleBlockWorld(CommandSender s, String[] args) {
        if (!s.hasPermission("FlyCountdown.admin")) {
            MessageUtils.sendMessage(s, this.format("messages.no-permission"));
            return true;
        }
        if (args.length < 2) {
            s.sendMessage(String.valueOf(ChatColor.RED) + "Usage: /fly blockworld <world>");
            return true;
        }
        String w = args[1];
        if (this.flightManager.isWorldBlocked(w)) {
            this.flightManager.unblockWorld(w);
            MessageUtils.sendMessage(s, this.format("messages.world-unblocked").replace("{world}", w));
        } else {
            this.flightManager.blockWorld(w);
            MessageUtils.sendMessage(s, this.format("messages.world-blocked").replace("{world}", w));
        }
        return true;
    }

    private boolean handleShop(CommandSender s) {
        if (!(s instanceof Player)) {
            s.sendMessage("Players only!");
            return true;
        }
        this.shopManager.openShop((Player)s);
        return true;
    }

    private boolean handleSettings(CommandSender s) {
        if (!(s instanceof Player)) {
            s.sendMessage("Players only!");
            return true;
        }
        if (!s.hasPermission("FlyCountdown.admin")) {
            MessageUtils.sendMessage(s, this.format("messages.no-permission"));
            return true;
        }
        this.settingsManager.openSettings((Player)s);
        return true;
    }

    private String format(String key) {
        String pre = this.plugin.getConfig().getString("messages.prefix", "");
        return pre + this.plugin.getConfig().getString(key, "");
    }
}

