/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.frozenorb.qlib.menu.Button
 *  net.minecraft.util.org.apache.commons.lang3.StringUtils
 *  org.bukkit.ChatColor
 *  org.bukkit.DyeColor
 *  org.bukkit.Material
 *  org.bukkit.entity.Player
 *  org.bukkit.event.inventory.ClickType
 */
package net.frozenorb.hydrogen.commands.prefix.menu;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import net.frozenorb.hydrogen.commands.prefix.menu.ScopesMenu;
import net.frozenorb.hydrogen.server.Server;
import net.frozenorb.qlib.menu.Button;
import net.minecraft.util.org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public class ScopeButton
extends Button {
    private ScopesMenu parent;
    private Server scope;

    public String getName(Player player) {
        boolean status = this.parent.getStatus().get(this.scope.getId());
        return (Object)(status ? ChatColor.GREEN : ChatColor.RED) + this.scope.getId();
    }

    public List<String> getDescription(Player player) {
        boolean status = this.parent.getStatus().get(this.scope.getId());
        ArrayList description = Lists.newArrayList();
        description.add(ChatColor.GRAY.toString() + (Object)ChatColor.STRIKETHROUGH + StringUtils.repeat((char)'-', (int)30));
        if (status) {
            description.add((Object)ChatColor.BLUE + "Click to " + (Object)ChatColor.RED + "remove " + (Object)ChatColor.YELLOW + this.scope.getId() + (Object)ChatColor.BLUE + " from this grant's scopes.");
        } else {
            description.add((Object)ChatColor.BLUE + "Click to " + (Object)ChatColor.GREEN + "add " + (Object)ChatColor.YELLOW + this.scope.getId() + (Object)ChatColor.BLUE + " to this grant's scopes.");
        }
        description.add(ChatColor.GRAY.toString() + (Object)ChatColor.STRIKETHROUGH + StringUtils.repeat((char)'-', (int)30));
        return description;
    }

    public Material getMaterial(Player player) {
        return Material.WOOL;
    }

    public byte getDamageValue(Player player) {
        boolean status = this.parent.getStatus().get(this.scope.getId());
        return status ? DyeColor.LIME.getWoolData() : DyeColor.GRAY.getWoolData();
    }

    public void clicked(Player player, int i, ClickType clickType) {
        this.parent.getStatus().put(this.scope.getId(), this.parent.getStatus().getOrDefault(this.scope.getId(), false) == false);
        this.parent.setGlobal(false);
    }

    public ScopeButton(ScopesMenu parent, Server scope) {
        this.parent = parent;
        this.scope = scope;
    }
}

