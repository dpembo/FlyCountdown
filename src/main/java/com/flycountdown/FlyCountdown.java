/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.TabCompleter
 *  org.bukkit.event.Listener
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.java.JavaPlugin
 */
package com.flycountdown;

import com.flycountdown.commands.FlyCommand;
import com.flycountdown.commands.FlyTabCompleter;
import com.flycountdown.listeners.FlightListener;
import com.flycountdown.managers.ActionBarManager;
import com.flycountdown.managers.DataManager;
import com.flycountdown.managers.FlightManager;
import com.flycountdown.managers.SettingsManager;
import com.flycountdown.managers.ShopManager;
import com.flycountdown.placeholders.FlyCountdownExpansion;
import com.flycountdown.utils.MessageUtils;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class FlyCountdown
extends JavaPlugin {
    private static FlyCountdown instance;
    private FlightManager flightManager;
    private DataManager dataManager;
    private ShopManager shopManager;
    private SettingsManager settingsManager;
    private ActionBarManager actionBarManager;
    private FlyCountdownExpansion placeholderExpansion;

    public void onEnable() {

        /**
         *
::::::::::::::::::::::::::::::         .--.
::::::::::::::::::::::::::::::    .-._;.--.;_.-.
 ########:'##:::::::'##:::'##:   (_.'_..--.._'._)
 ##.....:: ##:::::::. ##:'##::    /.' . 60 . '.\
 ##::::::: ##::::::::. ####:::   // .      / . \\
 ######::: ##:::::::::. ##::::  |; .      /   . |;
 ##...:::: ##:::::::::: ##::::  ||45    ()    15||
 ##::::::: ##:::::::::: ##::::  |; .          . |;
 ##::::::: ########:::: ##::::   \\ .        . //
..::::::::........:::::..:::::    \'._' 30 '_.'/
::::::::::::::::::::::::::::::     '-._'--'_.-'


        */
        this.getLogger().info("::::::::::::::::::::::::::::::         .--.");
        this.getLogger().info("::::::::::::::::::::::::::::::    .-._;.--.;_.-.");
        this.getLogger().info("########:'##:::::::'##:::'##:   (_.'_..--.._'._)");
        this.getLogger().info("##.....:: ##:::::::. ##:'##::    /.' . 60 . '.\\");
        this.getLogger().info("##::::::: ##::::::::. ####:::   // .      / . \\\\");
        this.getLogger().info("######::: ##:::::::::. ##::::  |; .      /   . |;");
        this.getLogger().info("##...:::: ##:::::::::: ##::::  ||45    ()    15||");
        this.getLogger().info("##::::::: ##:::::::::: ##::::  |; .          . |;");
        this.getLogger().info("##::::::: ########:::: ##::::   \\\\ .        . //");
        this.getLogger().info("..::::::::........:::::..:::::    \\'._' 30 '_.'/");
        this.getLogger().info("::::::::::::::::::::::::::::::     '-._'--'_.-'");

        this.getLogger().info("FlyCountdown v" + this.getDescription().getVersion() + " is starting up...");


        instance = this;
        MessageUtils.init(this);
        this.saveDefaultConfig();
        this.dataManager = new DataManager(this);
        this.flightManager = new FlightManager(this, this.dataManager);
        this.actionBarManager = new ActionBarManager(this, this.flightManager);
        this.flightManager.setActionBarManager(this.actionBarManager);
        this.shopManager = new ShopManager(this, this.flightManager);
        this.settingsManager = new SettingsManager(this);
        FlyCommand flyCommand = new FlyCommand(this, this.flightManager, this.shopManager, this.settingsManager);
        this.getCommand("fly").setExecutor((CommandExecutor)flyCommand);
        this.getCommand("fly").setTabCompleter((TabCompleter)new FlyTabCompleter());
        this.getServer().getPluginManager().registerEvents((Listener)new FlightListener(this, this.flightManager), (Plugin)this);
        this.getServer().getPluginManager().registerEvents((Listener)this.shopManager, (Plugin)this);
        this.getServer().getPluginManager().registerEvents((Listener)this.settingsManager, (Plugin)this);
        this.dataManager.loadAllPlayers();
        this.flightManager.restoreFlights();
        this.flightManager.startTimer();
        this.actionBarManager.start();
        if (this.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            this.placeholderExpansion = new FlyCountdownExpansion(this, this.flightManager);
            this.placeholderExpansion.register();
            this.getLogger().info("PlaceholderAPI expansion registered!");
        } else {
            this.getLogger().info("PlaceholderAPI not found. Placeholders will not work.");
        }
        if (this.getServer().getPluginManager().getPlugin("Vault") != null) {
            this.getLogger().info("Vault found and hooked!");
        } else {
            this.getLogger().info("Vault not found. Shop will be disabled.");
        }
        this.getLogger().info("FlyCountdown has been enabled! - Made Originally by drafterplus, forked by dpembo");
    }

    public void onDisable() {
        if (this.actionBarManager != null) {
            this.actionBarManager.stop();
        }
        if (this.flightManager != null) {
            this.flightManager.saveAllActiveFlights();
        }
        if (this.dataManager != null) {
            this.dataManager.saveAllPlayers();
        }
        if (this.placeholderExpansion != null) {
            this.placeholderExpansion.unregister();
        }
        if (this.flightManager != null) {
            this.flightManager.disableAllFlights();
        }
        MessageUtils.close();
        this.getLogger().info("FlyCountdown has been disabled!");
    }

    public static FlyCountdown getInstance() {
        return instance;
    }

    public FlightManager getFlightManager() {
        return this.flightManager;
    }

    public DataManager getDataManager() {
        return this.dataManager;
    }

    public ShopManager getShopManager() {
        return this.shopManager;
    }

    public SettingsManager getSettingsManager() {
        return this.settingsManager;
    }

    public ActionBarManager getActionBarManager() {
        return this.actionBarManager;
    }
}

