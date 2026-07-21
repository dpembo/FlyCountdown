/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  me.clip.placeholderapi.expansion.PlaceholderExpansion
 *  org.bukkit.entity.Player
 */
package com.flycountdown.placeholders;

import com.flycountdown.FlyCountdown;
import com.flycountdown.managers.FlightManager;
import com.flycountdown.utils.TimeUtils;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

public class FlyCountdownExpansion
extends PlaceholderExpansion {
    private final FlyCountdown plugin;
    private final FlightManager flightManager;

    public FlyCountdownExpansion(FlyCountdown plugin, FlightManager flightManager) {
        this.plugin = plugin;
        this.flightManager = flightManager;
    }

    public String getIdentifier() {
        return "flycountdown";
    }

    public String getAuthor() {
        return "FlyCountdown";
    }

    public String getVersion() {
        return this.plugin.getDescription().getVersion();
    }

    public boolean persist() {
        return true;
    }

    public String onPlaceholderRequest(Player player, String identifier) {
        if (player == null) {
            return "";
        }
        boolean isInfinite = player.hasPermission("FlyCountdown.fly.inf");
        if (identifier.equals("time")) {
            if (this.flightManager.hasTemporaryFlightTime(player.getUniqueId())) {
                return TimeUtils.formatTime(this.flightManager.getFlightTime(player.getUniqueId()));
            }
            if (isInfinite) {
                return "\u221e";
            }
            long flightTime = this.flightManager.getFlightTime(player.getUniqueId());
            return TimeUtils.formatTime(flightTime);
        }
        if (identifier.equals("time_digital")) {
            if (isInfinite) {
                return "\u221e";
            }
            long flightTime = this.flightManager.getFlightTime(player.getUniqueId());
            return TimeUtils.formatDigital(flightTime);
        }
        if (identifier.equals("time_seconds")) {
            if (isInfinite) {
                return "\u221e";
            }
            long flightTime = this.flightManager.getFlightTime(player.getUniqueId());
            return String.valueOf(flightTime);
        }
        if (identifier.equals("percent")) {
            if (isInfinite) {
                return "100";
            }
            long remainingTime = this.flightManager.getFlightTime(player.getUniqueId());
            long initialTime = this.flightManager.getInitialFlightTime(player.getUniqueId());
            if (initialTime > 0L) {
                double percentage = (double)remainingTime / (double)initialTime * 100.0;
                return String.valueOf((int)Math.round(percentage));
            }
            return "0";
        }
        if (identifier.equals("enabled")) {
            boolean enabled = this.flightManager.isFlightEnabled(player.getUniqueId());
            return enabled ? "true" : "false";
        }
        if (identifier.equals("is_flying")) {
            return player.isFlying() ? "true" : "false";
        }
        return null;
    }
}

