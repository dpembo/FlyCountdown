/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.milkbowl.vault.economy.Economy
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.Material
 *  org.bukkit.OfflinePlayer
 *  org.bukkit.Sound
 *  org.bukkit.command.CommandSender
 *  org.bukkit.configuration.ConfigurationSection
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.inventory.InventoryClickEvent
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.ItemMeta
 *  org.bukkit.plugin.RegisteredServiceProvider
 */
package com.flycountdown.managers;

import com.flycountdown.FlyCountdown;
//import com.flycountdown.managers.FlightManager;
import com.flycountdown.utils.MessageUtils;
import com.flycountdown.utils.TimeUtils;
import java.util.ArrayList;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.RegisteredServiceProvider;

public class ShopManager
implements Listener {
    private final FlyCountdown plugin;
    private final FlightManager flightManager;
    private Economy economy;
    private final String guiTitle;

    public ShopManager(FlyCountdown plugin, FlightManager flightManager) {
        this.plugin = plugin;
        this.flightManager = flightManager;
        this.guiTitle = ChatColor.translateAlternateColorCodes((char)'&', (String)plugin.getConfig().getString("shop.title", "&8Flight Shop"));
        this.setupEconomy();
    }

    private boolean setupEconomy() {
        if (this.plugin.getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider rsp = this.plugin.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        this.economy = (Economy)rsp.getProvider();
        return this.economy != null;
    }

    public void openShop(Player player) {
        if (!this.plugin.getConfig().getBoolean("shop.enabled")) {
            MessageUtils.sendMessage((CommandSender)player, this.formatMessage("messages.shop-disabled"));
            return;
        }
        if (this.economy == null && !this.setupEconomy()) {
            MessageUtils.sendMessage((CommandSender)player, this.formatMessage("messages.vault-missing"));
            return;
        }
        int size = this.plugin.getConfig().getInt("shop.size", 27);
        Inventory inv = Bukkit.createInventory(null, (int)size, (String)this.guiTitle);
        String bgName = this.plugin.getConfig().getString("shop.background-color", "BLACK");
        Material glassPane = Material.getMaterial((String)(bgName + "_STAINED_GLASS_PANE"));
        if (glassPane == null) {
            glassPane = Material.BLACK_STAINED_GLASS_PANE;
        }
        ItemStack bg = new ItemStack(glassPane);
        ItemMeta bgMeta = bg.getItemMeta();
        bgMeta.setDisplayName(" ");
        bg.setItemMeta(bgMeta);
        for (int i = 0; i < size; ++i) {
            inv.setItem(i, bg);
        }
        ConfigurationSection itemsSection = this.plugin.getConfig().getConfigurationSection("shop.items");
        if (itemsSection != null) {
            for (String key : itemsSection.getKeys(false)) {
                ConfigurationSection itemSection = itemsSection.getConfigurationSection(key);
                if (itemSection == null) continue;
                int slot = itemSection.getInt("slot");
                String name = ChatColor.translateAlternateColorCodes((char)'&', (String)itemSection.getString("name", "Option"));
                ArrayList<String> lore = new ArrayList<String>();
                for (String line : itemSection.getStringList("lore")) {
                    lore.add(ChatColor.translateAlternateColorCodes((char)'&', (String)line));
                }
                String matName = itemSection.getString("material", "FEATHER");
                Material material = Material.getMaterial((String)matName);
                if (material == null) {
                    material = Material.FEATHER;
                }
                ItemStack item = new ItemStack(material);
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName(name);
                meta.setLore(lore);
                item.setItemMeta(meta);
                inv.setItem(slot, item);
            }
        }
        player.openInventory(inv);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals(this.guiTitle)) {
            return;
        }
        event.setCancelled(true);
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        Player player = (Player)event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() == Material.AIR) {
            return;
        }
        ConfigurationSection itemsSection = this.plugin.getConfig().getConfigurationSection("shop.items");
        if (itemsSection != null) {
            for (String key : itemsSection.getKeys(false)) {
                ConfigurationSection itemSection = itemsSection.getConfigurationSection(key);
                if (itemSection == null || event.getSlot() != itemSection.getInt("slot")) continue;
                this.handlePurchase(player, itemSection);
                return;
            }
        }
    }

    private void handlePurchase(Player player, ConfigurationSection itemSection) {
        double cost = itemSection.getDouble("cost");
        long timeSeconds = itemSection.getLong("time-seconds");
        if (this.economy.getBalance((OfflinePlayer)player) >= cost) {
            this.economy.withdrawPlayer((OfflinePlayer)player, cost);
            this.flightManager.addFlightTime(player.getUniqueId(), timeSeconds);
            if (this.plugin.getConfig().getBoolean("sounds-enabled")) {
                String soundName = this.plugin.getConfig().getString("sound-purchase", "ENTITY_PLAYER_LEVELUP");
                try {
                    player.playSound(player.getLocation(), Sound.valueOf((String)soundName), 1.0f, 1.0f);
                }
                catch (IllegalArgumentException illegalArgumentException) {
                    // empty catch block
                }
            }
            String timeFormatted = TimeUtils.formatTime(timeSeconds);
            MessageUtils.sendMessage((CommandSender)player, this.formatMessage("messages.shop-purchase-success").replace("%time%", timeFormatted));
            String actionbarMsg = this.plugin.getConfig().getString("messages.shop-purchase-actionbar", "<green>Purchase complete added ( %time% )").replace("%time%", timeFormatted);
            if (this.plugin.getActionBarManager() != null) {
                this.plugin.getActionBarManager().setPriorityMessage(player, actionbarMsg, 3);
            }
            player.closeInventory();
        } else {
            MessageUtils.sendMessage((CommandSender)player, this.formatMessage("messages.shop-purchase-failed"));
            try {
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
    }

    private String formatMessage(String key) {
        String prefix = this.plugin.getConfig().getString("messages.prefix", "");
        String msg = this.plugin.getConfig().getString(key, "");
        return prefix + msg;
    }
}

