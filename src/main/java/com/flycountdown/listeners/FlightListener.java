/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.entity.EntityDamageEvent
 *  org.bukkit.event.entity.EntityDamageEvent$DamageCause
 *  org.bukkit.event.player.PlayerChangedWorldEvent
 *  org.bukkit.event.player.PlayerJoinEvent
 *  org.bukkit.event.player.PlayerQuitEvent
 */
package com.flycountdown.listeners;

import com.flycountdown.FlyCountdown;
import com.flycountdown.managers.FlightManager;
import com.flycountdown.utils.MessageUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class FlightListener
implements Listener {
    private final FlyCountdown plugin;
    private final FlightManager flightManager;

    public FlightListener(FlyCountdown plugin, FlightManager flightManager) {
        this.plugin = plugin;
        this.flightManager = flightManager;
    }

    @EventHandler(priority=EventPriority.HIGH)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (player.getAllowFlight()) {
            this.flightManager.restoreFlights();
        }
        if (player.isOp() && !this.plugin.getConfig().getBoolean("update-message-sent", false)) {
            MessageUtils.sendMessage((CommandSender)player, "<gradient:#00BFFF:#1E90FF>Thanks for updating our plugin <red>\u2764</red></gradient>");
            MessageUtils.sendMessage((CommandSender)player, "<gray>Maked by <gradient:#FFD700:#FFA500>drafterplus</gradient>");
            this.plugin.getConfig().set("update-message-sent", (Object)true);
            this.plugin.saveConfig();
        }
    }

    @EventHandler(priority=EventPriority.HIGH)
    public void onPlayerQuit(PlayerQuitEvent event) {
        this.plugin.getDataManager().savePlayer(event.getPlayer());
        if (this.flightManager.isFlightEnabled(event.getPlayer().getUniqueId())) {
            this.flightManager.disableFlight(event.getPlayer().getUniqueId(), false);
        }
    }

    @EventHandler(priority=EventPriority.HIGH)
    public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
        if (this.flightManager.isWorldBlocked(event.getPlayer().getWorld().getName()) && this.flightManager.isFlightEnabled(event.getPlayer().getUniqueId())) {
            this.flightManager.disableFlight(event.getPlayer().getUniqueId(), true);
        }
    }

    @EventHandler(priority=EventPriority.HIGH)
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        if (event.getCause() != EntityDamageEvent.DamageCause.FALL) {
            return;
        }
        Player player = (Player)event.getEntity();
        if (this.plugin.getConfig().getBoolean("prevent-fall-damage", true) && this.flightManager.isRecentlyExpired(player.getUniqueId())) {
            event.setCancelled(true);
            player.setFallDistance(0.0f);
        }
    }
}

