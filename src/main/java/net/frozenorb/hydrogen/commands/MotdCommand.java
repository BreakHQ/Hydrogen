/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.frozenorb.qlib.command.Command
 *  net.frozenorb.qlib.command.Param
 *  net.frozenorb.qlib.qLib
 *  net.frozenorb.qlib.redis.RedisCommand
 *  org.bukkit.ChatColor
 *  org.bukkit.command.CommandSender
 *  redis.clients.jedis.Jedis
 */
package net.frozenorb.hydrogen.commands;

import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import net.frozenorb.qlib.qLib;
import net.frozenorb.qlib.redis.RedisCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import redis.clients.jedis.Jedis;

public class MotdCommand {
    private static String line1;
    private static String line2;

    @Command(names={"motd set"}, permission="super")
    public static void motdSet(CommandSender sender, @Param(name="line") int line, @Param(name="text", wildcard=true) String text) {
        if (line == 1) {
            line1 = text;
        } else if (line == 2) {
            line2 = text;
        } else {
            sender.sendMessage((Object)ChatColor.RED + "Invalid line number...");
            return;
        }
        sender.sendMessage((Object)ChatColor.GREEN + "Line " + line + " set to \"" + text + "\"");
    }

    @Command(names={"motd push"}, permission="super", async=true)
    public static void motdPush(final CommandSender sender) {
        if (line1 == null) {
            sender.sendMessage((Object)ChatColor.RED + "Line 1 is not set! Unable to push.");
        } else if (line2 == null) {
            sender.sendMessage((Object)ChatColor.RED + "Line 2 is not set! Unable to push.");
        } else {
            qLib.getInstance().runBackboneRedisCommand((RedisCommand)new RedisCommand<Object>(){

                public Object execute(Jedis jedis) {
                    jedis.set("BungeeCordMOTD", line1 + "\n" + line2);
                    sender.sendMessage((Object)ChatColor.GREEN + "MOTD set.");
                    return null;
                }
            });
        }
    }
}

