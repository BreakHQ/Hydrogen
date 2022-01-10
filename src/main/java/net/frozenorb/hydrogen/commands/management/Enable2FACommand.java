/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.frozenorb.qlib.command.Command
 *  org.bukkit.ChatColor
 *  org.bukkit.command.CommandSender
 */
package net.frozenorb.hydrogen.commands.management;

import net.frozenorb.hydrogen.Settings;
import net.frozenorb.qlib.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class Enable2FACommand {
    @Command(names={"enable2fa"}, permission="super", description="Enable two-factor authentication on the server")
    public static void enable2fa(CommandSender sender) {
        Settings.setForceStaffTotp(true);
        Settings.save();
        sender.sendMessage((Object)ChatColor.GREEN + "2FA set to \"" + (Object)ChatColor.WHITE + true + (Object)ChatColor.GREEN + "\"");
    }
}

