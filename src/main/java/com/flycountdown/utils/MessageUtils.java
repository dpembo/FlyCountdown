package com.flycountdown.utils;

import com.flycountdown.FlyCountdown;

import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class MessageUtils {

    private static BukkitAudiences adventure;
    private static final MiniMessage miniMessage = MiniMessage.miniMessage();

    public static void init(FlyCountdown plugin) {
        adventure = BukkitAudiences.create(plugin);
    }

    public static void close() {
        if (adventure != null) {
            adventure.close();
            adventure = null;
        }
    }

    public static void sendMessage(CommandSender sender, String message) {
        if (sender == null || message == null || message.isEmpty()) {
            return;
        }

        if (adventure != null && sender instanceof Player player) {
            try {
                Component component = miniMessage.deserialize(message);
                adventure.player(player).sendMessage(component);
                return;
            } catch (Exception ignored) {}
        }

        sender.sendMessage(translateLegacy(message));
    }

    public static void sendActionBar(Player player, String message) {
        if (player == null) {
            return;
        }

        String finalMsg = (message == null ? "" : message);

        if (adventure != null && finalMsg.contains("<") && finalMsg.contains(">")) {
            try {
                Component component = miniMessage.deserialize(finalMsg);
                adventure.player(player).sendActionBar(component);
                return;
            } catch (Exception ignored) {}
        }

        try {
            player.spigot().sendMessage(
                ChatMessageType.ACTION_BAR,
                TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', finalMsg))
            );
        } catch (Exception e) {
            try {
                player.spigot().sendMessage(
                    ChatMessageType.ACTION_BAR,
                    new TextComponent(ChatColor.translateAlternateColorCodes('&', finalMsg))
                );
            } catch (Exception ignored) {}
        }
    }

    private static String translateLegacy(String msg) {
        if (msg == null) {
            return "";
        }
        String clean = msg.replaceAll("<[^>]+>", "");
        return ChatColor.translateAlternateColorCodes('&', clean);
    }
}
