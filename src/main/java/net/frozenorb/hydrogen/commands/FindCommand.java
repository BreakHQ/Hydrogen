/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.frozenorb.qlib.command.Command
 *  net.frozenorb.qlib.command.Param
 *  net.frozenorb.qlib.util.Callback
 *  org.bukkit.ChatColor
 *  org.bukkit.command.CommandSender
 */
package net.frozenorb.hydrogen.commands;

import java.util.Optional;
import java.util.UUID;
import net.frozenorb.hydrogen.Hydrogen;
import net.frozenorb.hydrogen.commands.punishment.parameter.PunishmentTarget;
import net.frozenorb.hydrogen.server.Server;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import net.frozenorb.qlib.util.Callback;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class FindCommand {
    @Command(names={"find"}, permission="hydrogen.find", description="See the server an user is currently playing on", async=true)
    public static void find(CommandSender sender, @Param(name="player") PunishmentTarget target) {
        target.resolveUUID((Callback<UUID>)((Callback)uuid -> {
            if (uuid == null) {
                sender.sendMessage((Object)ChatColor.RED + "An error occurred when contacting the Mojang API.");
                return;
            }
            Optional<Server> serverOptional = Hydrogen.getInstance().getServerHandler().find((UUID)uuid);
            if (!serverOptional.isPresent()) {
                sender.sendMessage((Object)ChatColor.RED + target.getName() + " is currently not on the network.");
                return;
            }
            Server server = serverOptional.get();
            sender.sendMessage((Object)ChatColor.BLUE + target.getName() + (Object)ChatColor.YELLOW + " is currently on " + (Object)ChatColor.BLUE + server.getDisplayName() + (Object)ChatColor.YELLOW + ".");
        }));
    }
}

