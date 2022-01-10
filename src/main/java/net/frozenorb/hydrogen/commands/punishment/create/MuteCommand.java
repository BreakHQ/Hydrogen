/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  mkremins.fanciful.FancyMessage
 *  net.frozenorb.qlib.command.Command
 *  net.frozenorb.qlib.command.Param
 *  net.frozenorb.qlib.util.Callback
 *  net.frozenorb.qlib.util.TimeUtils
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 */
package net.frozenorb.hydrogen.commands.punishment.create;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import mkremins.fanciful.FancyMessage;
import net.frozenorb.hydrogen.Hydrogen;
import net.frozenorb.hydrogen.commands.punishment.parameter.PunishmentTarget;
import net.frozenorb.hydrogen.connection.RequestResponse;
import net.frozenorb.hydrogen.punishment.Punishment;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import net.frozenorb.qlib.util.Callback;
import net.frozenorb.qlib.util.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MuteCommand {
    @Command(names={"mute"}, permission="minehq.punishment.create.mute", description="Temporarily mute an user, stopping them from talking in public chat", async=true)
    public static void mute(CommandSender sender, @Param(name="target") PunishmentTarget target, @Param(name="time") String timeString, @Param(name="reason", wildcard=true) String reason) {
        target.resolveUUID((Callback<UUID>)((Callback)uuid -> {
            if (uuid == null) {
                sender.sendMessage((Object)ChatColor.RED + "Unable to locate UUID of " + target.getName() + ".");
                return;
            }
            UUID senderUUID = sender instanceof Player ? ((Player)sender).getUniqueId() : null;
            Player bukkitTarget = Bukkit.getPlayer((UUID)uuid);
            int seconds = TimeUtils.parseTime((String)timeString);
            if (senderUUID != null && !((Player)sender).hasPermission("minehq.punishment.create.mute.permanent") && TimeUnit.DAYS.toSeconds(31L) < (long)seconds) {
                sender.sendMessage((Object)ChatColor.RED + "You don't have permission to create a mute this long. Maximum time allowed: 30 days.");
                return;
            }
            RequestResponse response = Hydrogen.getInstance().getPunishmentHandler().punish((UUID)uuid, senderUUID, Punishment.PunishmentType.MUTE, reason, reason, seconds);
            if (response.couldNotConnect()) {
                sender.sendMessage((Object)ChatColor.RED + "Could not reach API to complete punishment request. Adding a local punishment to the cache.");
                return;
            }
            if (!response.wasSuccessful()) {
                sender.sendMessage((Object)ChatColor.RED + response.getErrorMessage());
                return;
            }
            FancyMessage staffMessage = MuteCommand.getStaffMessage(sender, bukkitTarget, target.getName(), seconds, reason);
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (!player.hasPermission("minehq.punishment.create.mute")) continue;
                staffMessage.send(player);
            }
            staffMessage.send((CommandSender)Bukkit.getConsoleSender());
            if (bukkitTarget != null) {
                bukkitTarget.sendMessage((Object)ChatColor.RED + "You have been silenced for " + TimeUtils.formatIntoDetailedString((int)seconds) + ".");
            }
        }));
    }

    public static FancyMessage getStaffMessage(CommandSender sender, Player bukkitTarget, String correctedTarget, int seconds, String reason) {
        String senderName = sender instanceof Player ? ((Player)sender).getDisplayName() : ChatColor.DARK_RED.toString() + (Object)ChatColor.BOLD + "Console";
        return new FancyMessage(bukkitTarget == null ? (Object)ChatColor.GREEN + correctedTarget : bukkitTarget.getDisplayName()).tooltip(new String[]{(Object)ChatColor.YELLOW + "Reason: " + (Object)ChatColor.RED + reason, (Object)ChatColor.YELLOW + "Duration: " + (Object)ChatColor.RED + TimeUtils.formatIntoDetailedString((int)seconds)}).then(" was temporarily muted by ").color(ChatColor.GREEN).tooltip(new String[]{(Object)ChatColor.YELLOW + "Reason: " + (Object)ChatColor.RED + reason, (Object)ChatColor.YELLOW + "Duration: " + (Object)ChatColor.RED + TimeUtils.formatIntoDetailedString((int)seconds)}).then(senderName).tooltip(new String[]{(Object)ChatColor.YELLOW + "Reason: " + (Object)ChatColor.RED + reason, (Object)ChatColor.YELLOW + "Duration: " + (Object)ChatColor.RED + TimeUtils.formatIntoDetailedString((int)seconds)}).then(".").color(ChatColor.GREEN).tooltip(new String[]{(Object)ChatColor.YELLOW + "Reason: " + (Object)ChatColor.RED + reason, (Object)ChatColor.YELLOW + "Duration: " + (Object)ChatColor.RED + TimeUtils.formatIntoDetailedString((int)seconds)});
    }
}

