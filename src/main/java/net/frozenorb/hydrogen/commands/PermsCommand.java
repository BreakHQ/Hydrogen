/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.frozenorb.qlib.command.Command
 *  net.frozenorb.qlib.command.Param
 *  net.minecraft.util.org.apache.commons.lang3.StringUtils
 *  org.bukkit.ChatColor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 */
package net.frozenorb.hydrogen.commands;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Optional;
import java.util.TreeMap;
import net.frozenorb.hydrogen.Hydrogen;
import net.frozenorb.hydrogen.profile.Profile;
import net.frozenorb.hydrogen.rank.Rank;
import net.frozenorb.hydrogen.util.pagination.PaginatedResult;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import net.minecraft.util.org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PermsCommand {
    @Command(names={"perms"}, permission="hydrogen.viewperms", description="View an user's permissions")
    public static void perms(CommandSender sender, final @Param(name="player", defaultValue="self") Player player, @Param(name="page", defaultValue="1") int page) {
        Optional<Profile> optionalProfile = Hydrogen.getInstance().getProfileHandler().getProfile(player.getUniqueId());
        if (!optionalProfile.isPresent()) {
            sender.sendMessage((Object)ChatColor.RED + player.getName() + "'s profile isn't loaded.");
            return;
        }
        final Rank bestDisplayRank = optionalProfile.get().getBestDisplayRank();
        TreeMap<String, Boolean> sortedPermissions = new TreeMap<String, Boolean>(String.CASE_INSENSITIVE_ORDER);
        sortedPermissions.putAll(optionalProfile.get().getPermissions());
        ArrayList toSend = Lists.newArrayList();
        for (String permission : sortedPermissions.keySet()) {
            toSend.add((player.hasPermission(permission) ? (Object)ChatColor.GREEN + " + " : (Object)ChatColor.RED + " - ") + (Object)ChatColor.WHITE + permission + " (" + sortedPermissions.get(permission) + ")");
        }
        new PaginatedResult<String>(){

            @Override
            public String getHeader(int page, int maxPages) {
                return ChatColor.translateAlternateColorCodes((char)'&', (String)("&c" + StringUtils.repeat((char)'-', (int)3) + " &r" + player.getDisplayName() + "&7(&r" + bestDisplayRank.getFormattedName() + "&7)'s Permissions (&e" + page + "&7/&e" + maxPages + "&7) &c" + StringUtils.repeat((char)'-', (int)3)));
            }

            @Override
            public String format(String entry, int index) {
                return entry;
            }
        }.display(sender, toSend, page);
    }

    @Command(names={"rankPerms"}, permission="hydrogen.viewperms", description="View an user's permissions")
    public static void rankPerms(CommandSender sender, @Param(name="page", defaultValue="default") String rank) {
        Hydrogen.getInstance().getPermissionHandler().getPermissions(Hydrogen.getInstance().getRankHandler().getRank(rank).get()).forEach((perm, value) -> sender.sendMessage(perm + ":" + value));
    }
}

