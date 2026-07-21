/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.configuration.file.YamlConfiguration
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scheduler.BukkitTask
 */
package com.flycountdown.managers;

import com.flycountdown.FlyCountdown;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

public class DataManager {
    private final FlyCountdown plugin;
    private final File dataFile;
    private FileConfiguration dataConfig;
    private final Map<UUID, Long> cache;
    private BukkitTask autoSaveTask;

    public DataManager(FlyCountdown plugin) {
        this.plugin = plugin;
        this.dataFile = new File(plugin.getDataFolder(), "data.yml");
        this.cache = new ConcurrentHashMap<UUID, Long>();
        this.loadDataFile();
        this.startAutoSave();
    }

    private void loadDataFile() {
        if (!this.dataFile.exists()) {
            try {
                this.dataFile.getParentFile().mkdirs();
                this.dataFile.createNewFile();
            }
            catch (IOException e) {
                this.plugin.getLogger().severe("Could not create data.yml: " + e.getMessage());
            }
        }
        this.dataConfig = YamlConfiguration.loadConfiguration((File)this.dataFile);
    }

    private void startAutoSave() {
        int interval = this.plugin.getConfig().getInt("auto-save-interval", 300) * 20;
        this.autoSaveTask = this.plugin.getServer().getScheduler().runTaskTimerAsynchronously((Plugin)this.plugin, () -> this.saveAllPlayers(), (long)interval, (long)interval);
    }

    public void loadAllPlayers() {
        if (this.dataConfig == null) {
            this.loadDataFile();
        }
        if (this.dataConfig.getConfigurationSection("players") != null) {
            for (String uuidStr : this.dataConfig.getConfigurationSection("players").getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(uuidStr);
                    long flightTime = this.dataConfig.getLong("players." + uuidStr + ".flight-time", 0L);
                    this.cache.put(uuid, flightTime);
                }
                catch (IllegalArgumentException e) {
                    this.plugin.getLogger().warning("Invalid UUID in data.yml: " + uuidStr);
                }
            }
        }
    }

    public void saveAllPlayers() {
        for (UUID uuid : this.cache.keySet()) {
            long flightTime = this.cache.get(uuid);
            this.dataConfig.set("players." + uuid.toString() + ".flight-time", (Object)flightTime);
        }
        try {
            this.dataConfig.save(this.dataFile);
        }
        catch (IOException e) {
            this.plugin.getLogger().severe("Could not save data.yml: " + e.getMessage());
        }
    }

    public long getFlightTime(UUID uuid) {
        if (this.cache.containsKey(uuid)) {
            return this.cache.get(uuid);
        }
        long flightTime = this.dataConfig.getLong("players." + uuid.toString() + ".flight-time", 0L);
        this.cache.put(uuid, flightTime);
        return flightTime;
    }

    public void setFlightTime(UUID uuid, long seconds) {
        this.cache.put(uuid, seconds);
        this.dataConfig.set("players." + uuid.toString() + ".flight-time", (Object)seconds);
        try {
            this.dataConfig.save(this.dataFile);
        }
        catch (IOException e) {
            this.plugin.getLogger().severe("Could not save data.yml: " + e.getMessage());
        }
    }

    public void savePlayer(Player player) {
        UUID uuid = player.getUniqueId();
        if (this.cache.containsKey(uuid)) {
            this.dataConfig.set("players." + uuid.toString() + ".flight-time", (Object)this.cache.get(uuid));
            try {
                this.dataConfig.save(this.dataFile);
            }
            catch (IOException e) {
                this.plugin.getLogger().severe("Could not save data.yml for player " + player.getName() + ": " + e.getMessage());
            }
        }
    }

    public void onDisable() {
        if (this.autoSaveTask != null) {
            this.autoSaveTask.cancel();
        }
        this.saveAllPlayers();
    }
}

