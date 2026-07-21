/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.Particle
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scheduler.BukkitTask
 */
package com.flycountdown.managers;

import com.flycountdown.FlyCountdown;
//import com.flycountdown.managers.FlightManager;
import com.flycountdown.utils.MessageUtils;
import com.flycountdown.utils.TimeUtils;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

public class ActionBarManager {
    private final FlyCountdown plugin;
    private final FlightManager flightManager;
    private BukkitTask task;
    private final Map<UUID, Long> priorityEndTime = new ConcurrentHashMap<UUID, Long>();
    private final Map<UUID, String> priorityMessage = new ConcurrentHashMap<UUID, String>();
    private final Map<UUID, Long> flickerEndTime = new ConcurrentHashMap<UUID, Long>();
    private int tickCounter = 0;

    public ActionBarManager(FlyCountdown plugin, FlightManager flightManager) {
        this.plugin = plugin;
        this.flightManager = flightManager;
    }

    public void start() {
        if (this.task != null) {
            this.task.cancel();
        }
        this.task = Bukkit.getScheduler().runTaskTimer((Plugin)this.plugin, () -> {
            ++this.tickCounter;
            boolean actionbarEnabled = this.plugin.getConfig().getBoolean("actionbar-enabled", true);
            boolean particlesEnabled = this.plugin.getConfig().getBoolean("particles-enabled", true);
            for (Player player : Bukkit.getOnlinePlayers()) {
                UUID uuid = player.getUniqueId();
                boolean isFlying = this.flightManager.isFlightEnabled(uuid);
                if (actionbarEnabled) {
                    if (isFlying) {
                        this.updateActionBar(player);
                    } else {
                        MessageUtils.sendActionBar(player, "");
                    }
                }
                if (!particlesEnabled || !isFlying || this.tickCounter % 4 != 0) continue;
                this.spawnParticles(player);
            }
        }, 0L, 2L);
    }

    public void stop() {
        if (this.task != null) {
            this.task.cancel();
            this.task = null;
        }
    }

    private void updateActionBar(Player player) {
        String format;
        UUID uuid = player.getUniqueId();
        if (this.priorityEndTime.containsKey(uuid)) {
            if (System.currentTimeMillis() < this.priorityEndTime.get(uuid)) {
                MessageUtils.sendActionBar(player, this.priorityMessage.getOrDefault(uuid, ""));
                return;
            }
            this.priorityEndTime.remove(uuid);
            this.priorityMessage.remove(uuid);
        }
        long remaining = -1L;
        if (!player.hasPermission("FlyCountdown.fly.inf")) {
            remaining = this.flightManager.getFlightTime(uuid);
        }
        String timeStr = remaining == -1L ? "Inf" : TimeUtils.formatDigital(Math.max(0L, remaining));
        String color = this.plugin.getConfig().getString("actionbar-color", "&f");
        if (this.flickerEndTime.containsKey(uuid) && System.currentTimeMillis() < this.flickerEndTime.get(uuid)) {
            format = "&c" + timeStr;
        } else {
            if (this.flickerEndTime.containsKey(uuid)) {
                this.flickerEndTime.remove(uuid);
            }
            format = color + timeStr;
        }
        MessageUtils.sendActionBar(player, format);
    }

    private void spawnParticles(Player player) {
        String typeName = this.plugin.getConfig().getString("particle-type", "SOUL_FIRE_FLAME");
        try {
            player.getWorld().spawnParticle(Particle.valueOf((String)typeName), player.getLocation().add(0.0, 0.1, 0.0), 3, 0.02, 0.02, 0.02, 0.005);
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    public void setPriorityMessage(Player player, String message, int durationSeconds) {
        this.priorityMessage.put(player.getUniqueId(), message);
        this.priorityEndTime.put(player.getUniqueId(), System.currentTimeMillis() + (long)durationSeconds * 1000L);
    }

    public void triggerFlicker(Player player, int durationSeconds) {
        this.flickerEndTime.put(player.getUniqueId(), System.currentTimeMillis() + (long)durationSeconds * 1000L);
    }
}

