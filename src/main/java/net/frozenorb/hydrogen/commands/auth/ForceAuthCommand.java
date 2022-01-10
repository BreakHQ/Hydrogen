/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.frozenorb.qlib.command.Command
 *  net.frozenorb.qlib.command.Param
 *  org.bukkit.ChatColor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 *  org.bukkit.metadata.FixedMetadataValue
 *  org.bukkit.metadata.MetadataValue
 *  org.bukkit.plugin.Plugin
 */
package net.frozenorb.hydrogen.commands.auth;

import net.frozenorb.hydrogen.Hydrogen;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

public class ForceAuthCommand {
    @Command(names={"forceauth"}, permission="op", description="Forcefully authenticate a player")
    public static void forceAuth(CommandSender sender, @Param(name="player") Player player) {
        player.removeMetadata("Locked", (Plugin)Hydrogen.getInstance());
        player.setMetadata("ForceAuth", (MetadataValue)new FixedMetadataValue((Plugin)Hydrogen.getInstance(), (Object)true));
        sender.sendMessage((Object)ChatColor.YELLOW + player.getName() + " has been forcefully authenticated.");
    }
}

