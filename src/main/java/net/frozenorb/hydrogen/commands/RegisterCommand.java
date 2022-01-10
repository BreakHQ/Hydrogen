/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  net.frozenorb.qlib.command.Command
 *  net.frozenorb.qlib.command.Param
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
import net.frozenorb.hydrogen.connection.RequestHandler;
import net.frozenorb.hydrogen.connection.RequestResponse;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.json.JSONObject;

public class RegisterCommand {
    @Command(names={"register"}, permission="", description="Become a member of the network!")
    public static void register(Player sender, @Param(name="email") String email) {
        Bukkit.getScheduler().scheduleAsyncDelayedTask((Plugin)Hydrogen.getInstance(), () -> {
            RequestResponse response = RequestHandler.post("/users/" + sender.getUniqueId() + "/registerEmail", (Map<String, Object>)ImmutableMap.of((Object)"email", (Object)email, (Object)"userIp", (Object)sender.getAddress().getAddress().getHostAddress()));
            if (!response.wasSuccessful()) {
                sender.sendMessage((Object)ChatColor.RED + response.getErrorMessage());
                return;
            }
            JSONObject json = response.asJSONObject();
            if (json.getBoolean("success")) {
                sender.sendMessage((Object)ChatColor.GREEN + "You should be receiving a confirmation e-mail shortly.");
            }
        });
    }
}

