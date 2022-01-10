/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.frozenorb.qlib.command.Command
 *  net.frozenorb.qlib.command.Param
 *  net.frozenorb.qlib.util.Callback
 *  org.bukkit.ChatColor
 *  org.bukkit.entity.Player
 */
package net.frozenorb.hydrogen.commands.punishment;

import java.util.UUID;
import net.frozenorb.hydrogen.commands.punishment.parameter.PunishmentTarget;
import net.frozenorb.hydrogen.punishment.menu.MainPunishmentMenu;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import net.frozenorb.qlib.util.Callback;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CheckPunishmentsCommand {
    @Command(names={"checkpunishments", "cp", "c"}, permission="minehq.punishment.view", description="Check a user's punishments")
    public static void checkPunishments(Player sender, @Param(name="target") PunishmentTarget target) {
        target.resolveUUID((Callback<UUID>)((Callback)uuid -> {
            if (uuid == null) {
                sender.sendMessage((Object)ChatColor.RED + "An error occurred when contacting the Mojang API.");
                return;
            }
            new MainPunishmentMenu(uuid.toString(), target.getName()).openMenu(sender);
        }));
    }
}

