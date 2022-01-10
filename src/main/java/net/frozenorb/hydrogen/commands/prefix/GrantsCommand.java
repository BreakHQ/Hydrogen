/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  net.frozenorb.qlib.command.Command
 *  net.frozenorb.qlib.command.Param
 *  net.frozenorb.qlib.qLib
 *  net.frozenorb.qlib.util.Callback
 *  net.minecraft.util.com.google.common.reflect.TypeToken
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.Plugin
 */
package net.frozenorb.hydrogen.commands.prefix;

import com.google.common.collect.ImmutableMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.frozenorb.hydrogen.Hydrogen;
import net.frozenorb.hydrogen.commands.punishment.parameter.PunishmentTarget;
import net.frozenorb.hydrogen.connection.RequestHandler;
import net.frozenorb.hydrogen.connection.RequestResponse;
import net.frozenorb.hydrogen.prefix.PrefixGrant;
import net.frozenorb.hydrogen.prefix.menu.PrefixGrantMenu;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import net.frozenorb.qlib.qLib;
import net.frozenorb.qlib.util.Callback;
import net.minecraft.util.com.google.common.reflect.TypeToken;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class GrantsCommand {
    @Command(names={"prefixgrants"}, permission="hydrogen.grantprefix", description="Check a user's grants")
    public static void grants(Player sender, @Param(name="target") PunishmentTarget target) {
        target.resolveUUID((Callback<UUID>)((Callback)uuid -> {
            if (uuid == null) {
                sender.sendMessage((Object)ChatColor.RED + "An error occurred when contacting the Mojang API.");
                return;
            }
            Bukkit.getScheduler().scheduleAsyncDelayedTask((Plugin)Hydrogen.getInstance(), () -> {
                RequestResponse response = RequestHandler.get("/prefixes/grants", (Map<String, Object>)ImmutableMap.of((Object)"user", (Object)uuid));
                if (response.wasSuccessful()) {
                    List allGrants = (List)qLib.PLAIN_GSON.fromJson(response.getResponse(), new TypeToken<List<PrefixGrant>>(){}.getType());
                    allGrants.sort((first, second) -> {
                        if (first.getAddedAt() > second.getAddedAt()) {
                            return -1;
                        }
                        return 1;
                    });
                    LinkedHashMap grants = new LinkedHashMap();
                    allGrants.forEach(grant -> grants.put(grant, grant.resolveAddedBy()));
                    Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)Hydrogen.getInstance(), () -> new PrefixGrantMenu(grants).openMenu(sender));
                } else {
                    sender.sendMessage((Object)ChatColor.RED + response.getErrorMessage());
                }
            });
        }));
    }
}

