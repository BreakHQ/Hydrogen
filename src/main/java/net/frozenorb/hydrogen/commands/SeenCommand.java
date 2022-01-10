/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  mkremins.fanciful.FancyMessage
 *  net.frozenorb.qlib.command.Command
 *  net.frozenorb.qlib.command.Param
 *  net.frozenorb.qlib.util.Callback
 *  net.frozenorb.qlib.util.TimeUtils
 *  net.minecraft.util.org.apache.commons.lang3.StringUtils
 *  org.bukkit.ChatColor
 *  org.bukkit.command.CommandSender
 *  org.json.JSONArray
 *  org.json.JSONObject
 */
package net.frozenorb.hydrogen.commands;

import java.util.Date;
import java.util.UUID;
import mkremins.fanciful.FancyMessage;
import net.frozenorb.hydrogen.Hydrogen;
import net.frozenorb.hydrogen.commands.punishment.parameter.PunishmentTarget;
import net.frozenorb.hydrogen.connection.RequestHandler;
import net.frozenorb.hydrogen.connection.RequestResponse;
import net.frozenorb.hydrogen.listener.GeneralListener;
import net.frozenorb.hydrogen.server.Server;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import net.frozenorb.qlib.util.Callback;
import net.frozenorb.qlib.util.TimeUtils;
import net.minecraft.util.org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.json.JSONArray;
import org.json.JSONObject;

public class SeenCommand {
    @Command(names={"seen"}, permission="hydrogen.seen", description="See an user's IP and other info", async=true)
    public static void seen(CommandSender sender, @Param(name="player") PunishmentTarget target) {
        target.resolveUUID((Callback<UUID>)((Callback)uuid -> {
            if (uuid == null) {
                sender.sendMessage((Object)ChatColor.RED + "An error occurred when contacting the Mojang API.");
                return;
            }
            Server server = Hydrogen.getInstance().getServerHandler().find((UUID)uuid).isPresent() ? Hydrogen.getInstance().getServerHandler().find((UUID)uuid).get() : null;
            RequestResponse response = RequestHandler.get("/users/" + uuid.toString() + "/details");
            if (!response.wasSuccessful()) {
                sender.sendMessage((Object)ChatColor.RED + response.getErrorMessage());
                return;
            }
            JSONObject details = response.asJSONObject();
            JSONObject lastIpLog = null;
            if (details.has("ipLog")) {
                JSONArray array = details.getJSONArray("ipLog");
                for (Object logObject : array) {
                    JSONObject log = (JSONObject)logObject;
                    if (lastIpLog != null && log.getLong("lastSeenAt") <= lastIpLog.getLong("lastSeenAt")) continue;
                    lastIpLog = log;
                }
            }
            sender.sendMessage(ChatColor.GRAY.toString() + (Object)ChatColor.STRIKETHROUGH + StringUtils.repeat((char)'-', (int)53));
            sender.sendMessage((Object)ChatColor.RED + target.getName() + (Object)ChatColor.YELLOW + ":");
            if (server != null) {
                long joinTime = GeneralListener.joinTime.get(uuid);
                int playSeconds = (int)((System.currentTimeMillis() - joinTime) / 1000L);
                sender.sendMessage((Object)ChatColor.YELLOW + "Currently on " + (Object)ChatColor.RED + server.getDisplayName() + (Object)ChatColor.YELLOW + ". Online for " + (Object)ChatColor.RED + TimeUtils.formatIntoDetailedString((int)playSeconds) + (Object)ChatColor.YELLOW + ".");
            } else {
                Date lastSeen = new Date(details.getJSONObject("user").getLong("lastSeenAt"));
                sender.sendMessage((Object)ChatColor.YELLOW + "Currently " + (Object)ChatColor.RED + "offline" + (Object)ChatColor.YELLOW + ". Last seen at " + (Object)ChatColor.RED + TimeUtils.formatIntoCalendarString((Date)lastSeen) + (Object)ChatColor.YELLOW + ".");
            }
            if (sender.hasPermission("hydrogen.seen.ip") && lastIpLog != null) {
                FancyMessage ipMessage = new FancyMessage("(Hover to show last known IP)").color(ChatColor.YELLOW);
                ipMessage.tooltip((Object)ChatColor.RED + "sike bitch twitter.com/bizarreaiex nigga stay woketh :pray:");
                ipMessage.send(sender);
            }
            sender.sendMessage(ChatColor.GRAY.toString() + (Object)ChatColor.STRIKETHROUGH + StringUtils.repeat((char)'-', (int)53));
        }));
    }
}

