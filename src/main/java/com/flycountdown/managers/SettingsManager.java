/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.Material
 *  org.bukkit.Sound
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.inventory.ClickType
 *  org.bukkit.event.inventory.InventoryClickEvent
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.ItemMeta
 */
package com.flycountdown.managers;

import com.flycountdown.FlyCountdown;
import java.util.ArrayList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SettingsManager
implements Listener {
    private final FlyCountdown plugin;
    private final String guiTitle = String.valueOf(ChatColor.DARK_GRAY) + "Fly Settings";
    private final String colorGuiTitle = String.valueOf(ChatColor.DARK_GRAY) + "Select Color";

    public SettingsManager(FlyCountdown plugin) {
        this.plugin = plugin;
    }

    public void openSettings(Player player) {
        Inventory inv = Bukkit.createInventory(null, (int)27, (String)this.guiTitle);
        ItemStack bg = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta bgMeta = bg.getItemMeta();
        bgMeta.setDisplayName(" ");
        bg.setItemMeta(bgMeta);
        for (int i = 0; i < 27; ++i) {
            inv.setItem(i, bg);
        }
        boolean actionBar = this.plugin.getConfig().getBoolean("actionbar-enabled", true);
        String currentColor = this.plugin.getConfig().getString("actionbar-color", "<white>");
        inv.setItem(10, this.createToggleItem(Material.PAPER, "&bAction Bar", "&7Show flight time in action bar.", actionBar, "&7Current Color: &f" + currentColor, "&eLeft-Click: &fSelect Color", "&eRight-Click: &fToggle ON/OFF"));
        boolean sounds = this.plugin.getConfig().getBoolean("sounds-enabled", true);
        inv.setItem(12, this.createToggleItem(Material.NOTE_BLOCK, "&eSounds", "&7Play sound effects.", sounds, "", "&eClick to toggle!"));
        boolean fallDamage = this.plugin.getConfig().getBoolean("prevent-fall-damage", true);
        inv.setItem(14, this.createToggleItem(Material.FEATHER, "&aPrevent Fall Damage", "&7Prevent damage when flight expires.", fallDamage, "", "&eClick to toggle!"));
        boolean particles = this.plugin.getConfig().getBoolean("particles-enabled", true);
        inv.setItem(16, this.createToggleItem(Material.BLAZE_POWDER, "&6Particles", "&7Show particles while flying.", particles, "", "&eClick to toggle!"));
        player.openInventory(inv);
    }

    private void openColorSelector(Player player) {
        Inventory inv = Bukkit.createInventory(null, (int)27, (String)this.colorGuiTitle);
        ItemStack bg = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta bgMeta = bg.getItemMeta();
        bgMeta.setDisplayName(" ");
        bg.setItemMeta(bgMeta);
        for (int i = 0; i < 27; ++i) {
            inv.setItem(i, bg);
        }
        inv.setItem(10, this.createColorItem(Material.WHITE_DYE, "&fWhite", "&f"));
        inv.setItem(11, this.createColorItem(Material.LIGHT_BLUE_DYE, "&bLight Blue", "&b"));
        inv.setItem(12, this.createColorItem(Material.YELLOW_DYE, "&eYellow", "&e"));
        inv.setItem(13, this.createColorItem(Material.LIME_DYE, "&aGreen", "&a"));
        inv.setItem(14, this.createColorItem(Material.ORANGE_DYE, "&6Orange", "&6"));
        inv.setItem(15, this.createColorItem(Material.PINK_DYE, "&dPink", "&d"));
        inv.setItem(16, this.createColorItem(Material.PURPLE_DYE, "&5Purple", "&5"));
        ItemStack back = new ItemStack(Material.ARROW);
        ItemMeta backMeta = back.getItemMeta();
        backMeta.setDisplayName(String.valueOf(ChatColor.RED) + "Back to Settings");
        back.setItemMeta(backMeta);
        inv.setItem(22, back);
        player.openInventory(inv);
    }

    private ItemStack createColorItem(Material mat, String name, String tag) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes((char)'&', (String)name));
        ArrayList<String> lore = new ArrayList<String>();
        lore.add(String.valueOf(ChatColor.GRAY) + "Click to set action bar color to " + name);
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createToggleItem(Material mat, String name, String desc, boolean enabled, String extra, String ... prompts) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes((char)'&', (String)name));
        ArrayList<String> lore = new ArrayList<String>();
        lore.add(ChatColor.translateAlternateColorCodes((char)'&', (String)desc));
        if (!extra.isEmpty()) {
            lore.add(ChatColor.translateAlternateColorCodes((char)'&', (String)extra));
        }
        lore.add("");
        lore.add(ChatColor.translateAlternateColorCodes((char)'&', (String)(enabled ? "&a&lENABLED" : "&c&lDISABLED")));
        for (String prompt : prompts) {
            lore.add(ChatColor.translateAlternateColorCodes((char)'&', (String)prompt));
        }
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        String title = event.getView().getTitle();
        if (!title.equals(this.guiTitle) && !title.equals(this.colorGuiTitle)) {
            return;
        }
        event.setCancelled(true);
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        Player player = (Player)event.getWhoClicked();
        int slot = event.getSlot();
        if (title.equals(this.guiTitle)) {
            if (slot == 10) {
                if (event.getClick() == ClickType.LEFT) {
                    this.playSound(player);
                    this.openColorSelector(player);
                } else if (event.getClick() == ClickType.RIGHT) {
                    boolean current = this.plugin.getConfig().getBoolean("actionbar-enabled", true);
                    this.plugin.getConfig().set("actionbar-enabled", (Object)(!current ? 1 : 0));
                    this.plugin.saveConfig();
                    this.playSound(player);
                    this.openSettings(player);
                }
            } else if (slot == 12) {
                boolean current = this.plugin.getConfig().getBoolean("sounds-enabled", true);
                this.plugin.getConfig().set("sounds-enabled", (Object)(!current ? 1 : 0));
                this.plugin.saveConfig();
                this.playSound(player);
                this.openSettings(player);
            } else if (slot == 14) {
                boolean current = this.plugin.getConfig().getBoolean("prevent-fall-damage", true);
                this.plugin.getConfig().set("prevent-fall-damage", (Object)(!current ? 1 : 0));
                this.plugin.saveConfig();
                this.playSound(player);
                this.openSettings(player);
            } else if (slot == 16) {
                boolean current = this.plugin.getConfig().getBoolean("particles-enabled", true);
                this.plugin.getConfig().set("particles-enabled", (Object)(!current ? 1 : 0));
                this.plugin.saveConfig();
                this.playSound(player);
                this.openSettings(player);
            }
        } else if (title.equals(this.colorGuiTitle)) {
            if (slot == 22) {
                this.playSound(player);
                this.openSettings(player);
                return;
            }
            String colorTag = null;
            switch (slot) {
                case 10: {
                    colorTag = "&f";
                    break;
                }
                case 11: {
                    colorTag = "&b";
                    break;
                }
                case 12: {
                    colorTag = "&e";
                    break;
                }
                case 13: {
                    colorTag = "&a";
                    break;
                }
                case 14: {
                    colorTag = "&6";
                    break;
                }
                case 15: {
                    colorTag = "&d";
                    break;
                }
                case 16: {
                    colorTag = "&5";
                }
            }
            if (colorTag != null) {
                this.plugin.getConfig().set("actionbar-color", (Object)colorTag);
                this.plugin.saveConfig();
                this.playSound(player);
                player.sendMessage(String.valueOf(ChatColor.GREEN) + "Action bar color updated!");
                this.openSettings(player);
            }
        }
    }

    private void playSound(Player player) {
        try {
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
        }
        catch (Exception exception) {
            // empty catch block
        }
    }
}

