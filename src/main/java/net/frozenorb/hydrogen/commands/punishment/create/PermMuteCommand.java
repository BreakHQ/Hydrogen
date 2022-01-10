/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  mkremins.fanciful.FancyMessage
 *  net.frozenorb.qlib.command.Command
 *  net.frozenorb.qlib.command.Param
 *  net.frozenorb.qlib.util.Callback
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 */
package net.frozenorb.hydrogen.commands.punishment.create;

import java.util.UUID;
import mkremins.fanciful.FancyMessage;
import net.frozenorb.hydrogen.Hydrogen;
import net.frozenorb.hydrogen.commands.punishment.parameter.PunishmentTarget;
import net.frozenorb.hydrogen.connection.RequestResponse;
import net.frozenorb.hydrogen.punishment.Punishment;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import net.frozenorb.qlib.util.Callback;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PermMuteCommand {
    @Command(names={"permmute"}, permission="minehq.punishment.create.mute.permanent", description="Permanently mute an user, stopping them from talking in public chat", async=true)
    public static void permmute(CommandSender sender, @Param(name="target") PunishmentTarget target, @Param(name="reason", wildcard=true) String reason) {
        target.resolveUUID((Callback<UUID>)((Callback)uuid -> {
            if (uuid == null) {
                sender.sendMessage((Object)ChatColor.RED + "Unable to locate UUID of " + target.getName() + ".");
                return;
            }
            UUID senderUUID = sender instanceof Player ? ((Player)sender).getUniqueId() : null;
            Player bukkitTarget = Bukkit.getPlayer((UUID)uuid);
            RequestResponse response = Hydrogen.getInstance().getPunishmentHandler().punish((UUID)uuid, senderUUID, Punishment.PunishmentType.MUTE, reason, reason, -1L);
            if (response.couldNotConnect()) {
                sender.sendMessage((Object)ChatColor.RED + "Could not reach API to complete punishment request. Adding a local punishment to the cache.");
                return;
            }
            if (!response.wasSuccessful()) {
                sender.sendMessage((Object)ChatColor.RED + response.getErrorMessage());
                return;
            }
            FancyMessage staffMessage = PermMuteCommand.getStaffMessage(sender, bukkitTarget, target.getName(), reason);
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (!player.hasPermission("minehq.punishment.create.mute")) continue;
                staffMessage.send(player);
            }
            staffMessage.send((CommandSender)Bukkit.getConsoleSender());
            if (bukkitTarget != null) {
                bukkitTarget.sendMessage((Object)ChatColor.RED + "You have been permanently silenced.");
            }
        }));
    }

    public static FancyMessage getStaffMessage(CommandSender sender, Player bukkitTarget, String correctedTarget, String reason) {
        String senderName = sender instanceof Player ? ((Player)sender).getDisplayName() : ChatColor.DARK_RED.toString() + (Object)ChatColor.BOLD + "Console";
        return new FancyMessage(bukkitTarget == null ? (Object)ChatColor.GREEN + correctedTarget : bukkitTarget.getDisplayName()).tooltip((Object)ChatColor.YELLOW + "Reason: " + (Object)ChatColor.RED + reason).then(" has been muted by ").color(ChatColor.GREEN).tooltip((Object)ChatColor.YELLOW + "Reason: " + (Object)ChatColor.RED + reason).then(senderName).tooltip((Object)ChatColor.YELLOW + "Reason: " + (Object)ChatColor.RED + reason).then(".").color(ChatColor.GREEN).tooltip((Object)ChatColor.YELLOW + "Reason: " + (Object)ChatColor.RED + reason);
    }
}

