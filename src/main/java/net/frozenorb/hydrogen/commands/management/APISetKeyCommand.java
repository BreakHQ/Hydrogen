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

import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class APISetKeyCommand {
    @Command(names={"api setkey"}, permission="super", description="Set the API key we use to authenticate")
    public static void apiSetKey(CommandSender sender, @Param(name="key") String key) {
        sender.sendMessage((Object)ChatColor.RED + "This command is no longer in use. Please configure the server-name in server.properties.");
    }
}

