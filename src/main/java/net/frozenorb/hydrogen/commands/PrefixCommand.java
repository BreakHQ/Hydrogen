/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.frozenorb.qlib.command.Command
 *  org.bukkit.entity.Player
 */
package net.frozenorb.hydrogen.commands;

import net.frozenorb.hydrogen.commands.prefix.setmenu.PrefixMenu;
import net.frozenorb.qlib.command.Command;
import org.bukkit.entity.Player;

public class PrefixCommand {
    @Command(names={"prefix", "setprefix", "setprefixes"}, permission="")
    public static void prefix(Player sender) {
        new PrefixMenu(sender.getName(), sender.getUniqueId()).openMenu(sender);
    }
}

