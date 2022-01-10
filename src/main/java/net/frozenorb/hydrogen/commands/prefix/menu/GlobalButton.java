/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  net.frozenorb.qlib.menu.Button
 *  org.bukkit.ChatColor
 *  org.bukkit.DyeColor
 *  org.bukkit.Material
 *  org.bukkit.entity.Player
 *  org.bukkit.event.inventory.ClickType
 */
package net.frozenorb.hydrogen.commands.prefix.menu;

import com.google.common.collect.ImmutableList;
import java.util.List;
import net.frozenorb.hydrogen.commands.prefix.menu.ScopesMenu;
import net.frozenorb.qlib.menu.Button;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public class GlobalButton
extends Button {
    private ScopesMenu parent;

    public String getName(Player player) {
        return (Object)ChatColor.BLUE + "Global";
    }

    public List<String> getDescription(Player player) {
        return ImmutableList.of();
    }

    public Material getMaterial(Player player) {
        return Material.WOOL;
    }

    public byte getDamageValue(Player player) {
        return this.parent.isGlobal() ? DyeColor.LIME.getWoolData() : DyeColor.GRAY.getWoolData();
    }

    public void clicked(Player player, int i, ClickType clickType) {
        for (String key : this.parent.getStatus().keySet()) {
            this.parent.getStatus().put(key, false);
        }
        this.parent.setGlobal(!this.parent.isGlobal());
    }

    public GlobalButton(ScopesMenu parent) {
        this.parent = parent;
    }
}

