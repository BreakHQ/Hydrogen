/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  net.frozenorb.qlib.command.Command
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.Plugin
 *  org.json.JSONObject
 */
package net.frozenorb.hydrogen.commands;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import net.frozenorb.hydrogen.Hydrogen;
import net.frozenorb.hydrogen.Settings;
import net.frozenorb.hydrogen.connection.RequestHandler;
import net.frozenorb.hydrogen.connection.RequestResponse;
import net.frozenorb.qlib.command.Command;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.json.JSONObject;

public class LoginCommand {
    @Command(names={"login"}, permission="", description="Generate a disposable token which can be used to log into the website")
    public static void login(Player sender) {
        Bukkit.getScheduler().scheduleAsyncDelayedTask((Plugin)Hydrogen.getInstance(), () -> {
            RequestResponse response = RequestHandler.post("/disposableLoginTokens", (Map<String, Object>)ImmutableMap.of((Object)"user", (Object)sender.getUniqueId(), (Object)"userIp", (Object)sender.getAddress().getAddress().getHostAddress()));
            if (!response.wasSuccessful()) {
                sender.sendMessage((Object)ChatColor.RED + response.getErrorMessage());
                return;
            }
            JSONObject json = response.asJSONObject();
            if (json.getBoolean("success")) {
                sender.sendMessage((Object)ChatColor.GREEN + "www." + Settings.getNetworkWebsite() + "/login/" + json.getString("token"));
                sender.sendMessage((Object)ChatColor.YELLOW + "The link above expires in 5 minutes.");
            }
        });
    }
}

