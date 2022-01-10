/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.frozenorb.qlib.command.Command
 *  net.frozenorb.qlib.util.TimeUtils
 *  net.minecraft.util.org.apache.commons.lang3.StringUtils
 *  org.bukkit.ChatColor
 *  org.bukkit.command.CommandSender
 */
package net.frozenorb.hydrogen.commands.management;

import net.frozenorb.hydrogen.connection.RequestHandler;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.util.TimeUtils;
import net.minecraft.util.org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class APICommand {
    @Command(names={"api"}, permission="hydrogen.debug", description="See API info")
    public static void api(CommandSender sender) {
        sender.sendMessage(ChatColor.GRAY.toString() + (Object)ChatColor.STRIKETHROUGH + StringUtils.repeat((char)'-', (int)53));
        if (sender.hasPermission("super")) {
            // empty if block
        }
        sender.sendMessage((Object)ChatColor.YELLOW + "Status: " + (RequestHandler.isApiDown() ? (Object)ChatColor.RED + "Offline" : (Object)ChatColor.GREEN + "Online"));
        sender.sendMessage((Object)ChatColor.YELLOW + "Last Request: " + (RequestHandler.getLastAPIRequest() == 0L ? (Object)ChatColor.GREEN + "Never :3" : (Object)ChatColor.RED + TimeUtils.formatIntoDetailedString((int)((int)(System.currentTimeMillis() - RequestHandler.getLastAPIRequest()) / 1000)) + " ago"));
        sender.sendMessage((Object)ChatColor.YELLOW + "Last Error: " + (RequestHandler.getLastAPIError() == 0L ? (Object)ChatColor.GREEN + "Never :3" : (Object)ChatColor.RED + TimeUtils.formatIntoDetailedString((int)((int)(System.currentTimeMillis() - RequestHandler.getLastAPIError()) / 1000)) + " ago"));
        sender.sendMessage((Object)ChatColor.YELLOW + "Last Latency: " + (Object)ChatColor.RED + RequestHandler.getLastLatency() + "ms");
        sender.sendMessage((Object)ChatColor.YELLOW + "Average Latency: " + (Object)ChatColor.RED + RequestHandler.getAverageLatency() + "ms");
        sender.sendMessage(ChatColor.GRAY.toString() + (Object)ChatColor.STRIKETHROUGH + StringUtils.repeat((char)'-', (int)53));
    }
}

