/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.Sound
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scheduler.BukkitTask
 */
package com.flycountdown.managers;

import com.flycountdown.FlyCountdown;
import com.flycountdown.managers.ActionBarManager;
import com.flycountdown.managers.DataManager;
import com.flycountdown.utils.MessageUtils;
import com.flycountdown.utils.TimeUtils;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

public class FlightManager {
    private final FlyCountdown plugin;
    private final DataManager dataManager;
    private final Map<UUID, Long> activeFlightTimes = new ConcurrentHashMap<UUID, Long>();
    private final Map<UUID, Long> initialFlightTimes = new ConcurrentHashMap<UUID, Long>();
    private final Map<UUID, Boolean> flightEnabled = new ConcurrentHashMap<UUID, Boolean>();
    private final Set<String> warnedPlayers = ConcurrentHashMap.newKeySet();
    private final Set<UUID> recentlyExpired = ConcurrentHashMap.newKeySet();
    private BukkitTask timerTask;
    private final Map<UUID, Long> temporaryFlightTimes = new ConcurrentHashMap<UUID, Long>();
    private ActionBarManager actionBarManager;
    private int tickCounter = 0;

    public FlightManager(FlyCountdown plugin, DataManager dataManager) {
        this.plugin = plugin;
        this.dataManager = dataManager;
    }

    public void setActionBarManager(ActionBarManager manager) {
        this.actionBarManager = manager;
    }

    public boolean hasTemporaryFlightTime(UUID uuid) {
        return this.temporaryFlightTimes.containsKey(uuid);
    }

    public long getInitialFlightTime(UUID uuid) {
        return this.initialFlightTimes.getOrDefault(uuid, 0L);
    }

    public void restoreFlights() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            long time;
            if (!p.getAllowFlight()) continue;
            UUID uuid = p.getUniqueId();
            this.flightEnabled.put(uuid, true);
            if (p.hasPermission("FlyCountdown.fly.inf") || (time = this.dataManager.getFlightTime(uuid)) <= 0L) continue;
            this.activeFlightTimes.put(uuid, time);
            this.initialFlightTimes.put(uuid, time);
        }
    }

    public void startTimer() {
        if (this.timerTask != null) {
            this.timerTask.cancel();
        }
        this.tickCounter = 0;
        this.timerTask = Bukkit.getScheduler().runTaskTimer((Plugin)this.plugin, () -> {
            ++this.tickCounter;
            if (this.tickCounter % 2 == 0) {
                for (UUID uuid : new HashSet<UUID>(this.activeFlightTimes.keySet())) {
                    Player p = Bukkit.getPlayer((UUID)uuid);
                    if (p == null || !p.isOnline()) {
                        this.dataManager.setFlightTime(uuid, this.activeFlightTimes.remove(uuid));
                        this.flightEnabled.remove(uuid);
                        continue;
                    }
                    if (p.hasPermission("FlyCountdown.fly.inf")) {
                        this.activeFlightTimes.remove(uuid);
                        continue;
                    }
                    if (!this.isFlightEnabled(uuid)) continue;
                    if (this.isWorldBlocked(p.getWorld().getName())) {
                        this.disableFlight(uuid, false);
                        continue;
                    }
                    long time = this.activeFlightTimes.get(uuid);
                    long initial = this.initialFlightTimes.getOrDefault(uuid, time);
                    if (initial > 0L) {
                        double pct = (double)time / (double)initial * 100.0;
                        List intervals = this.plugin.getConfig().getIntegerList("warning-intervals-percent");
                        Iterator iterator = intervals.iterator();
                        while (iterator.hasNext()) {
                            int i = (Integer)iterator.next();
                            double nextPct = (double)(time + 1L) / (double)initial * 100.0;
                            String key = String.valueOf(uuid) + "_" + i;
                            if (!(pct <= (double)i) || !(nextPct > (double)i) || this.warnedPlayers.contains(key)) continue;
                            String msg = this.plugin.getConfig().getString("messages.flight-warning", "Warning: %time% remaining");
                            MessageUtils.sendMessage((CommandSender)p, this.processInternal(p, msg, time));
                            this.playSound(p, "sound-warning");
                            this.warnedPlayers.add(key);
                            if (this.actionBarManager == null) continue;
                            this.actionBarManager.triggerFlicker(p, 5);
                        }
                    }
                    this.activeFlightTimes.put(uuid, --time);
                    if (time >= 0L) continue;
                    this.expireFlight(uuid);
                }
            }
        }, 0L, 10L);
    }

    public boolean toggleFlight(Player p) {
        UUID uuid = p.getUniqueId();
        if (!p.hasPermission("FlyCountdown.fly") && !p.hasPermission("FlyCountdown.fly.inf")) {
            MessageUtils.sendMessage((CommandSender)p, this.formatMessage("messages.no-permission"));
            return false;
        }
        if (this.isWorldBlocked(p.getWorld().getName())) {
            MessageUtils.sendMessage((CommandSender)p, this.formatMessage("messages.flight-blocked-world"));
            return false;
        }
        if (p.hasPermission("FlyCountdown.fly.inf")) {
            p.setAllowFlight(!p.getAllowFlight());
            p.setFlying(p.getAllowFlight());
            String key = p.getAllowFlight() ? "messages.flight-enabled" : "messages.flight-disabled";
            MessageUtils.sendMessage((CommandSender)p, this.processInternal(p, this.formatMessage(key), -1L));
            if (p.getAllowFlight()) {
                this.flightEnabled.put(uuid, true);
                this.playSound(p, "sound-enabled");
            } else {
                this.flightEnabled.remove(uuid);
                this.playSound(p, "sound-disabled");
            }
            return true;
        }
        if (this.isFlightEnabled(uuid)) {
            this.disableFlight(uuid, true);
            return true;
        }
        long time = this.getFlightTime(uuid);
        if (time <= 0L) {
            MessageUtils.sendMessage((CommandSender)p, this.formatMessage("messages.no-flight-time"));
            return false;
        }
        this.enableFlight(uuid, time);
        return true;
    }

    public void enableFlight(UUID uuid, long time) {
        Player p = Bukkit.getPlayer((UUID)uuid);
        if (p == null) {
            return;
        }
        this.activeFlightTimes.put(uuid, time);
        this.initialFlightTimes.put(uuid, time);
        this.flightEnabled.put(uuid, true);
        this.warnedPlayers.removeIf(k -> k.startsWith(uuid.toString()));
        p.setAllowFlight(true);
        p.setFlying(true);
        MessageUtils.sendMessage((CommandSender)p, this.processInternal(p, this.formatMessage("messages.flight-enabled"), time));
        this.playSound(p, "sound-enabled");
    }

    public void disableFlight(UUID uuid, boolean send) {
        Player p = Bukkit.getPlayer((UUID)uuid);
        if (this.activeFlightTimes.containsKey(uuid)) {
            this.dataManager.setFlightTime(uuid, Math.max(0L, this.activeFlightTimes.get(uuid)));
        }
        if (p != null) {
            p.setFlying(false);
            p.setAllowFlight(false);
            if (send) {
                MessageUtils.sendMessage((CommandSender)p, this.formatMessage("messages.flight-disabled"));
                this.playSound(p, "sound-disabled");
            }
        }
        this.activeFlightTimes.remove(uuid);
        this.initialFlightTimes.remove(uuid);
        this.flightEnabled.remove(uuid);
        this.warnedPlayers.removeIf(k -> k.startsWith(uuid.toString()));
    }

    public void expireFlight(UUID uuid) {
        Player p = Bukkit.getPlayer((UUID)uuid);
        if (p != null) {
            MessageUtils.sendMessage((CommandSender)p, this.formatMessage("messages.flight-expired"));
            this.playSound(p, "sound-expired");
            if (this.plugin.getConfig().getBoolean("prevent-fall-damage", true)) {
                p.setFallDistance(0.0f);
                this.recentlyExpired.add(uuid);
                Bukkit.getScheduler().runTaskLater((Plugin)this.plugin, () -> this.recentlyExpired.remove(uuid), 100L);
            }
        }
        this.disableFlight(uuid, false);
    }

    public void addFlightTime(UUID uuid, long s) {
        long cur = this.dataManager.getFlightTime(uuid);
        this.dataManager.setFlightTime(uuid, cur + s);
        if (this.isFlightEnabled(uuid) && this.activeFlightTimes.containsKey(uuid)) {
            this.activeFlightTimes.put(uuid, this.activeFlightTimes.get(uuid) + s);
        }
    }

    public void removeFlightTime(UUID uuid, long s) {
        long cur = this.dataManager.getFlightTime(uuid);
        this.dataManager.setFlightTime(uuid, Math.max(0L, cur - s));
        if (this.isFlightEnabled(uuid) && this.activeFlightTimes.containsKey(uuid)) {
            long up = Math.max(0L, this.activeFlightTimes.get(uuid) - s);
            if (up <= 0L) {
                this.expireFlight(uuid);
            } else {
                this.activeFlightTimes.put(uuid, up);
            }
        }
    }

    public long getFlightTime(UUID uuid) {
        if (this.temporaryFlightTimes.containsKey(uuid)) {
            return this.temporaryFlightTimes.get(uuid);
        }
        if (this.activeFlightTimes.containsKey(uuid)) {
            return this.activeFlightTimes.get(uuid);
        }
        return this.dataManager.getFlightTime(uuid);
    }

    public long getStoredFlightTime(UUID uuid) {
        return this.dataManager.getFlightTime(uuid);
    }

    public boolean isFlightEnabled(UUID uuid) {
        return this.flightEnabled.getOrDefault(uuid, false);
    }

    public boolean isWorldBlocked(String w) {
        return this.plugin.getConfig().getStringList("blocked-worlds").contains(w);
    }

    public void blockWorld(String w) {
        ArrayList<String> list = new ArrayList<String>(this.plugin.getConfig().getStringList("blocked-worlds"));
        if (!list.contains(w)) {
            list.add(w);
            this.plugin.getConfig().set("blocked-worlds", list);
            this.plugin.saveConfig();
        }
    }

    public void unblockWorld(String w) {
        ArrayList list = new ArrayList(this.plugin.getConfig().getStringList("blocked-worlds"));
        list.remove(w);
        this.plugin.getConfig().set("blocked-worlds", list);
        this.plugin.saveConfig();
    }

    public void disableAllFlights() {
        for (UUID uuid : new HashSet<UUID>(this.flightEnabled.keySet())) {
            this.disableFlight(uuid, false);
        }
    }

    public void saveAllActiveFlights() {
        for (UUID uuid : this.activeFlightTimes.keySet()) {
            this.dataManager.setFlightTime(uuid, this.activeFlightTimes.get(uuid));
        }
    }

    public boolean isRecentlyExpired(UUID uuid) {
        return this.recentlyExpired.contains(uuid);
    }

    public void setTemporaryFlightTime(UUID uuid, long t) {
        if (t < 0L) {
            this.temporaryFlightTimes.remove(uuid);
        } else {
            this.temporaryFlightTimes.put(uuid, t);
        }
    }

    public void reload() {
        this.plugin.reloadConfig();
        for (UUID uuid : new HashSet<UUID>(this.flightEnabled.keySet())) {
            Player p = Bukkit.getPlayer((UUID)uuid);
            if (p == null || !this.isWorldBlocked(p.getWorld().getName())) continue;
            this.disableFlight(uuid, true);
        }
    }

    private void playSound(Player p, String key) {
        String s;
        if (this.plugin.getConfig().getBoolean("sounds-enabled", true) && (s = this.plugin.getConfig().getString(key)) != null && !s.isEmpty()) {
            try {
                p.playSound(p.getLocation(), Sound.valueOf((String)s), 1.0f, 1.0f);
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
    }

    private String formatMessage(String key) {
        String pre = this.plugin.getConfig().getString("messages.prefix", "");
        return pre + this.plugin.getConfig().getString(key, "");
    }

    public String processInternal(Player p, String msg, long time) {
        if (msg == null) {
            return "";
        }
        String t = time == -1L ? "\u221e" : TimeUtils.formatTime(time);
        return msg.replace("%flycountdown_time%", t).replace("%time%", t).replace("%player%", p.getName());
    }
}

