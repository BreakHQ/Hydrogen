/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.frozenorb.qlib.command.Command
 *  net.frozenorb.qlib.command.Param
 *  org.bukkit.ChatColor
 *  org.bukkit.command.CommandSender
 */
package net.frozenorb.hydrogen.commands.management;

import net.frozenorb.hydrogen.Settings;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class APISetHostNameCommand {
    @Command(names={"api sethostname"}, permission="super", description="Set the API hostname")
    public static void apiSetHostName(CommandSender sender, @Param(name="hostname") String hostName) {
        Settings.setApiHost(hostName);
        Settings.save();
        sender.sendMessage((Object)ChatColor.GREEN + "API hostname set to \"" + (Object)ChatColor.WHITE + hostName + (Object)ChatColor.GREEN + "\"");
    }
}

