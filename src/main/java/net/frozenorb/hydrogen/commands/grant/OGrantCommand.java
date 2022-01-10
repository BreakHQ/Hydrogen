/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  net.frozenorb.qlib.command.Command
 *  net.frozenorb.qlib.command.Param
 *  net.frozenorb.qlib.util.Callback
 *  net.frozenorb.qlib.util.TimeUtils
 *  org.apache.commons.lang.StringUtils
 *  org.bukkit.ChatColor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 */
package net.frozenorb.hydrogen.commands.grant;

import com.google.common.collect.ImmutableList;
import java.util.HashMap;
import java.util.UUID;
import net.frozenorb.hydrogen.commands.punishment.parameter.PunishmentTarget;
import net.frozenorb.hydrogen.connection.RequestHandler;
import net.frozenorb.hydrogen.connection.RequestResponse;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import net.frozenorb.qlib.util.Callback;
import net.frozenorb.qlib.util.TimeUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class OGrantCommand {
    @Command(names={"ogrant"}, permission="hydrogen.grant", description="Add a grant to an user's account", async=true)
    public static void grant(CommandSender sender, @Param(name="player") PunishmentTarget target, @Param(name="rank") String rank, @Param(name="duration") String timeString, @Param(name="scopes") String scopes, @Param(name="reason", wildcard=true) String reason) {
        int duration = -1;
        if (!StringUtils.startsWithIgnoreCase((String)timeString, (String)"perm") && (duration = TimeUtils.parseTime((String)timeString) + 1) <= 0) {
            sender.sendMessage((Object)ChatColor.RED + "'" + timeString + "' is not a valid duration.");
            return;
        }
        if (scopes.equalsIgnoreCase("global")) {
            scopes = "";
        }
        String finalScopes = scopes;
        int expiresIn = duration;
        if (!sender.hasPermission("minehq.grant.create." + rank.toLowerCase())) {
            sender.sendMessage((Object)ChatColor.RED + "You don't have permission to do this.");
            return;
        }
        target.resolveUUID((Callback<UUID>)((Callback)uuid -> {
            RequestResponse response;
            if (uuid == null) {
                sender.sendMessage((Object)ChatColor.RED + "An error occurred when contacting the Mojang API.");
                return;
            }
            HashMap<String, Object> body = new HashMap<String, Object>();
            body.put("user", uuid);
            body.put("reason", reason);
            body.put("scopes", finalScopes.isEmpty() ? ImmutableList.of() : finalScopes.split(","));
            body.put("rank", rank);
            if (expiresIn > 0) {
                body.put("expiresIn", expiresIn);
            }
            if (sender instanceof Player) {
                body.put("addedBy", ((Player)sender).getUniqueId().toString());
                body.put("addedByIp", ((Player)sender).getAddress().getAddress().getHostAddress());
            }
            if ((response = RequestHandler.post("/grants", body)).wasSuccessful()) {
                sender.sendMessage((Object)ChatColor.GREEN + "Successfully granted " + (Object)ChatColor.WHITE + target.getName() + (Object)ChatColor.GREEN + " the " + (Object)ChatColor.WHITE + rank + (Object)ChatColor.GREEN + " rank.");
            } else {
                sender.sendMessage((Object)ChatColor.RED + response.getErrorMessage());
            }
        }));
    }
}

