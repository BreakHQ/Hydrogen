/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  net.frozenorb.qlib.menu.Button
 *  net.frozenorb.qlib.menu.Menu
 *  org.bukkit.ChatColor
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scheduler.BukkitRunnable
 */
package net.frozenorb.hydrogen.commands.prefix.menu;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import net.frozenorb.hydrogen.Hydrogen;
import net.frozenorb.hydrogen.commands.prefix.menu.GlobalButton;
import net.frozenorb.hydrogen.commands.prefix.menu.GrantButton;
import net.frozenorb.hydrogen.commands.prefix.menu.ScopeButton;
import net.frozenorb.hydrogen.prefix.Prefix;
import net.frozenorb.hydrogen.server.Server;
import net.frozenorb.qlib.menu.Button;
import net.frozenorb.qlib.menu.Menu;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class ScopesMenu
extends Menu {
    private final Map<String, Boolean> status = Maps.newHashMap();
    private boolean global = false;
    private boolean complete;
    private Prefix prefix;
    private String targetName;
    private UUID targetUUID;
    private String reason;
    private int duration;

    public String getTitle(Player player) {
        return ChatColor.YELLOW.toString() + (Object)ChatColor.BOLD + "Select the Scopes";
    }

    public Map<Integer, Button> getButtons(Player player) {
        HashMap buttons = Maps.newHashMap();
        ArrayList groups = Lists.newArrayList();
        groups.addAll(Hydrogen.getInstance().getServerHandler().getServers());
        groups.sort((first, second) -> first.getId().compareToIgnoreCase(second.getId()));
        int i = 0;
        for (Server scope : groups) {
            if (scope.getServerGroup().toLowerCase().contains("bunker") || scope.getServerGroup().toLowerCase().contains("skywar") || scope.getServerGroup().toLowerCase().contains("hub")) continue;
            this.status.putIfAbsent(scope.getId(), false);
            buttons.put(i, new ScopeButton(this, scope));
            ++i;
        }
        ArrayList scopes = Lists.newArrayList();
        scopes.addAll(this.status.keySet().stream().filter(this.status::get).map(key -> Hydrogen.getInstance().getServerHandler().getServer((String)key).orElse(null)).collect(Collectors.toList()));
        buttons.put(22, new GlobalButton(this));
        buttons.put(31, new GrantButton(this.prefix, this.targetName, this.targetUUID, this.reason, this, scopes, this.duration));
        return buttons;
    }

    public void onClose(final Player player) {
        new BukkitRunnable(){

            public void run() {
                if (!Menu.currentlyOpenedMenus.containsKey(player.getName()) && !ScopesMenu.this.complete) {
                    player.sendMessage((Object)ChatColor.RED + "Granting cancelled.");
                }
            }
        }.runTaskLater((Plugin)Hydrogen.getInstance(), 1L);
    }

    public ScopesMenu(boolean global, boolean complete, Prefix prefix, String targetName, UUID targetUUID, String reason, int duration) {
        this.global = global;
        this.complete = complete;
        this.prefix = prefix;
        this.targetName = targetName;
        this.targetUUID = targetUUID;
        this.reason = reason;
        this.duration = duration;
    }

    public Map<String, Boolean> getStatus() {
        return this.status;
    }

    public boolean isGlobal() {
        return this.global;
    }

    public void setGlobal(boolean global) {
        this.global = global;
    }

    public boolean isComplete() {
        return this.complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }
}

