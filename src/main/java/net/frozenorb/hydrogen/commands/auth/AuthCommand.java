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
package net.frozenorb.hydrogen.commands.auth;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.Optional;
import net.frozenorb.hydrogen.Hydrogen;
import net.frozenorb.hydrogen.connection.RequestHandler;
import net.frozenorb.hydrogen.connection.RequestResponse;
import net.frozenorb.hydrogen.profile.Profile;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.json.JSONObject;

public class AuthCommand {
    @Command(names={"auth", "authenticate", "2fa", "otp"}, permission="", description="Authenticate with the API, verifying your identity")
    public static void auth(Player sender, @Param(name="code", wildcard=true) String input) {
        if (!sender.hasMetadata("Locked")) {
            sender.sendMessage((Object)ChatColor.RED + "You don't need to authenticate at the moment.");
            return;
        }
        input = input.replace(" ", "");
        int code = Integer.parseInt(input);
        Bukkit.getScheduler().scheduleAsyncDelayedTask((Plugin)Hydrogen.getInstance(), () -> {
            RequestResponse response = RequestHandler.post("/users/" + sender.getUniqueId().toString() + "/verifyTotp", (Map<String, Object>)ImmutableMap.of((Object)"totpCode", (Object)code, (Object)"userIp", (Object)sender.getAddress().getAddress().getHostAddress()));
            if (response.wasSuccessful()) {
                Bukkit.getScheduler().runTask((Plugin)Hydrogen.getInstance(), () -> {
                    JSONObject object = response.asJSONObject();
                    boolean authorized = object.getBoolean("authorized");
                    if (!authorized) {
                        sender.sendMessage((Object)ChatColor.RED + object.getString("message"));
                        return;
                    }
                    Optional<Profile> profileOptional = Hydrogen.getInstance().getProfileHandler().getProfile(sender.getUniqueId());
                    if (!profileOptional.isPresent()) {
                        sender.sendMessage(ChatColor.DARK_RED.toString() + (Object)ChatColor.BOLD + "Your profile hasn't yet been loaded.");
                        return;
                    }
                    Profile profile = profileOptional.get();
                    profile.authenticated();
                    profile.updatePlayer(sender);
                    sender.sendMessage((Object)ChatColor.GREEN + "Your identity has been verified.");
                });
            } else {
                sender.sendMessage((Object)ChatColor.RED + response.getErrorMessage());
                Bukkit.getLogger().warning("TOTP - Failed to authenticate " + sender.getUniqueId() + ": " + response.getErrorMessage());
            }
        });
    }
}

