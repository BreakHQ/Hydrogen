/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  mkremins.fanciful.FancyMessage
 *  net.frozenorb.qlib.command.Command
 *  net.frozenorb.qlib.command.Flag
 *  net.frozenorb.qlib.command.Param
 *  net.frozenorb.qlib.command.parameter.offlineplayer.OfflinePlayerWrapper
 *  net.frozenorb.qlib.util.Callback
 *  net.frozenorb.qlib.util.UUIDUtils
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scheduler.BukkitRunnable
 */
package net.frozenorb.hydrogen.commands.punishment.create;

import java.util.UUID;
import mkremins.fanciful.FancyMessage;
import net.frozenorb.hydrogen.Hydrogen;
import net.frozenorb.hydrogen.Settings;
import net.frozenorb.hydrogen.commands.punishment.parameter.PunishmentTarget;
import net.frozenorb.hydrogen.connection.RequestResponse;
import net.frozenorb.hydrogen.punishment.Punishment;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Flag;
import net.frozenorb.qlib.command.Param;
import net.frozenorb.qlib.command.parameter.offlineplayer.OfflinePlayerWrapper;
import net.frozenorb.qlib.util.Callback;
import net.frozenorb.qlib.util.UUIDUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class BanCommand {
    public static final String KICK_MESSAGE = (Object)ChatColor.RED + "Your account has been suspended from the " + Settings.getNetworkName() + " Network. \n\nAppeal at " + Settings.getNetworkWebsite() + "/support";

    @Command(names={"ban", "b", "banish"}, permission="minehq.punishment.create.ban.permanent", description="Permanently banish an user from the network", async=true)
    public static void ban(CommandSender sender, @Flag(value={"s", "silent"}, description="Silently ban the player") boolean silent, @Flag(value={"c", "clear"}, description="Clear the player's inventory") boolean clear, @Param(name="target") PunishmentTarget target, @Param(name="reason", wildcard=true) String reason) {
        BanCommand.ban0(sender, target, reason, true, clear);
    }

    private static void ban0(CommandSender sender, PunishmentTarget target, String reason, boolean silent, boolean clear) {
        target.resolveUUID((Callback<UUID>)((Callback)uuid -> {
            if (uuid == null) {
                sender.sendMessage((Object)ChatColor.RED + "Unable to locate UUID of " + target.getName() + ".");
                return;
            }
            UUID senderUUID = sender instanceof Player ? ((Player)sender).getUniqueId() : null;
            final Player bukkitTarget = Bukkit.getPlayer((UUID)uuid);
            RequestResponse response = Hydrogen.getInstance().getPunishmentHandler().punish((UUID)uuid, senderUUID, Punishment.PunishmentType.BAN, reason, reason, -1L);
            if (response.couldNotConnect()) {
                sender.sendMessage((Object)ChatColor.RED + "Could not reach API to complete punishment request. Adding a local punishment to the cache.");
            } else if (!response.wasSuccessful()) {
                sender.sendMessage((Object)ChatColor.RED + response.getErrorMessage());
                return;
            }
            FancyMessage staffMessage = BanCommand.getStaffMessage(sender, bukkitTarget, target.getName(), reason, silent);
            if (!silent) {
                for (Player player2 : Bukkit.getOnlinePlayers()) {
                    if (player2.hasPermission("minehq.punishment.view")) {
                        staffMessage.send(player2);
                        continue;
                    }
                    player2.sendMessage((Object)ChatColor.GREEN + target.getName() + " was banned by " + sender.getName() + ".");
                }
            } else {
                for (Player player3 : Bukkit.getOnlinePlayers()) {
                    if (!player3.hasPermission("minehq.punishment.view")) continue;
                    staffMessage.send(player3);
                }
            }
            staffMessage.send((CommandSender)Bukkit.getConsoleSender());
            if (clear) {
                OfflinePlayerWrapper wrapper = new OfflinePlayerWrapper(UUIDUtils.name((UUID)uuid));
                wrapper.loadAsync(player -> Bukkit.getScheduler().runTask((Plugin)Hydrogen.getInstance(), () -> {
                    player.getInventory().clear();
                    Bukkit.getScheduler().runTaskAsynchronously((Plugin)Hydrogen.getInstance(), ((Player)player)::saveData);
                }));
            }
            if (bukkitTarget != null) {
                new BukkitRunnable(){

                    public void run() {
                        bukkitTarget.kickPlayer(KICK_MESSAGE);
                    }
                }.runTaskLater((Plugin)Hydrogen.getInstance(), 1L);
            }
        }));
    }

    private static FancyMessage getStaffMessage(CommandSender sender, Player bukkitTarget, String correctedTarget, String reason, boolean silent) {
        String senderName = sender instanceof Player ? ((Player)sender).getDisplayName() : ChatColor.DARK_RED.toString() + (Object)ChatColor.BOLD + "Console";
        FancyMessage staffMessage = new FancyMessage(bukkitTarget == null ? (Object)ChatColor.GREEN + correctedTarget : bukkitTarget.getDisplayName()).tooltip((Object)ChatColor.YELLOW + "Reason: " + (Object)ChatColor.RED + reason).then(" was ").color(ChatColor.GREEN).tooltip((Object)ChatColor.YELLOW + "Reason: " + (Object)ChatColor.RED + reason);
        if (silent) {
            staffMessage.then("silently ").color(ChatColor.YELLOW).tooltip((Object)ChatColor.YELLOW + "Reason: " + (Object)ChatColor.RED + reason);
        }
        staffMessage.then("banned by ").color(ChatColor.GREEN).tooltip((Object)ChatColor.YELLOW + "Reason: " + (Object)ChatColor.RED + reason).then(senderName).tooltip((Object)ChatColor.YELLOW + "Reason: " + (Object)ChatColor.RED + reason).then(".").color(ChatColor.GREEN).tooltip((Object)ChatColor.YELLOW + "Reason: " + (Object)ChatColor.RED + reason);
        return staffMessage;
    }
}

