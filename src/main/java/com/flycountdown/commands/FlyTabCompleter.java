/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandSender
 *  org.bukkit.command.TabCompleter
 *  org.bukkit.entity.Player
 */
package com.flycountdown.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class FlyTabCompleter
implements TabCompleter {
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        ArrayList<String> completions;
        block14: {
            String subCommand;
            block15: {
                String subCommand2;
                block16: {
                    block13: {
                        completions = new ArrayList<String>();
                        if (args.length != 1) break block13;
                        List<String> subCommands = Arrays.asList("add", "take", "check", "reload", "toggle", "blockworld", "shop", "settings");
                        String input = args[0].toLowerCase();
                        for (String subCmd : subCommands) {
                            if (!subCmd.startsWith(input)) continue;
                            if (subCmd.equals("add") || subCmd.equals("take") || subCmd.equals("blockworld") || subCmd.equals("settings")) {
                                if (!sender.hasPermission("FlyCountdown.admin")) continue;
                                completions.add(subCmd);
                                continue;
                            }
                            if (subCmd.equals("check")) {
                                if (!sender.hasPermission("FlyCountdown.check") && !sender.hasPermission("FlyCountdown.admin")) continue;
                                completions.add(subCmd);
                                continue;
                            }
                            if (subCmd.equals("reload")) {
                                if (!sender.hasPermission("FlyCountdown.reload")) continue;
                                completions.add(subCmd);
                                continue;
                            }
                            if (subCmd.equals("toggle")) {
                                if (!sender.hasPermission("FlyCountdown.toggle")) continue;
                                completions.add(subCmd);
                                continue;
                            }
                            if (!subCmd.equals("shop")) continue;
                            completions.add(subCmd);
                        }
                        if (!input.isEmpty() || !completions.isEmpty()) break block14;
                        for (String subCmd : subCommands) {
                            if (subCmd.equals("add") || subCmd.equals("take") || subCmd.equals("blockworld") || subCmd.equals("settings")) {
                                if (!sender.hasPermission("FlyCountdown.admin")) continue;
                                completions.add(subCmd);
                                continue;
                            }
                            if (subCmd.equals("check")) {
                                if (!sender.hasPermission("FlyCountdown.check") && !sender.hasPermission("FlyCountdown.admin")) continue;
                                completions.add(subCmd);
                                continue;
                            }
                            if (subCmd.equals("reload")) {
                                if (!sender.hasPermission("FlyCountdown.reload")) continue;
                                completions.add(subCmd);
                                continue;
                            }
                            if (subCmd.equals("toggle")) {
                                if (!sender.hasPermission("FlyCountdown.toggle")) continue;
                                completions.add(subCmd);
                                continue;
                            }
                            if (!subCmd.equals("shop")) continue;
                            completions.add(subCmd);
                        }
                        break block14;
                    }
                    if (args.length != 2) break block15;
                    subCommand2 = args[0].toLowerCase();
                    if (!subCommand2.equals("add") && !subCommand2.equals("take") && !subCommand2.equals("check") && !subCommand2.equals("toggle")) break block16;
                    String input = args[1].toLowerCase();
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (!player.getName().toLowerCase().startsWith(input)) continue;
                        completions.add(player.getName());
                    }
                    break block14;
                }
                if (!subCommand2.equals("blockworld")) break block14;
                String input = args[1].toLowerCase();
                Bukkit.getWorlds().forEach(world -> {
                    if (world.getName().toLowerCase().startsWith(input)) {
                        completions.add(world.getName());
                    }
                });
                break block14;
            }
            if (args.length == 3 && ((subCommand = args[0].toLowerCase()).equals("add") || subCommand.equals("take"))) {
                String input = args[2].toLowerCase();
                List<String> timeSuggestions = Arrays.asList("1s", "5s", "10s", "30s", "1m", "5m", "10m", "30m", "1h", "2h", "1d");
                for (String suggestion : timeSuggestions) {
                    if (!suggestion.startsWith(input)) continue;
                    completions.add(suggestion);
                }
            }
        }
        return completions.isEmpty() ? null : completions;
    }
}

