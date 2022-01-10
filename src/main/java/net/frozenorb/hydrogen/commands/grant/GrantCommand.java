/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.frozenorb.qlib.command.Command
 *  net.frozenorb.qlib.command.Param
 *  net.frozenorb.qlib.util.Callback
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scheduler.BukkitRunnable
 */
package net.frozenorb.hydrogen.commands.grant;

import java.util.UUID;
import net.frozenorb.hydrogen.Hydrogen;
import net.frozenorb.hydrogen.commands.grant.menu.RanksMenu;
import net.frozenorb.hydrogen.commands.punishment.parameter.PunishmentTarget;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import net.frozenorb.qlib.util.Callback;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class GrantCommand {
    @Command(names={"grant"}, permission="hydrogen.grant", description="Add a grant to an user's account - Cute GUI!")
    public static void grant(Player sender, final @Param(name="player") PunishmentTarget target) {
        target.resolveUUID((Callback<UUID>)((Callback)uuid -> new BukkitRunnable((UUID)uuid, sender){
            final /* synthetic */ UUID val$uuid;
            final /* synthetic */ Player val$sender;
            {
                this.val$uuid = uUID;
                this.val$sender = player;
            }

            public void run() {
                new RanksMenu(target.getName(), this.val$uuid).openMenu(this.val$sender);
            }
        }.runTask((Plugin)Hydrogen.getInstance())));
    }
}

