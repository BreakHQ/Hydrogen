/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.frozenorb.qlib.menu.Button
 *  org.bukkit.ChatColor
 *  org.bukkit.DyeColor
 *  org.bukkit.Material
 *  org.bukkit.entity.Player
 *  org.bukkit.event.inventory.ClickType
 */
package net.frozenorb.hydrogen.commands.prefix.setmenu;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import net.frozenorb.hydrogen.Hydrogen;
import net.frozenorb.hydrogen.prefix.Prefix;
import net.frozenorb.hydrogen.profile.Profile;
import net.frozenorb.qlib.menu.Button;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public class PrefixButton
extends Button {
    private String targetName;
    private UUID targetUUID;
    private Prefix prefix;
    private boolean active;
    private boolean authorized;

    public String getName(Player player) {
        return ChatColor.translateAlternateColorCodes((char)'&', (String)this.prefix.getButtonName());
    }

    public List<String> getDescription(Player player) {
        ArrayList description = Lists.newArrayList();
        if (this.active) {
            description.add((Object)ChatColor.RED + "This is already your prefix.");
            description.add((Object)ChatColor.RED + "Click to disable.");
        } else if (this.authorized) {
            description.add((Object)ChatColor.GRAY + "Click to make this your prefix.");
        } else if (this.prefix.isPurchaseable()) {
            if (this.prefix.getButtonDescription().contains("%newline%")) {
                Arrays.stream(this.prefix.getButtonDescription().split("%newline%")).map(string -> ChatColor.translateAlternateColorCodes((char)'&', (String)string)).forEach(description::add);
            } else {
                description.add(ChatColor.translateAlternateColorCodes((char)'&', (String)this.prefix.getButtonDescription()));
            }
        } else {
            description.add((Object)ChatColor.GRAY + "This prefix is unavailable and unpurchaseable.");
        }
        return description;
    }

    public Material getMaterial(Player player) {
        return Material.WOOL;
    }

    public byte getDamageValue(Player player) {
        return this.getColor().getWoolData();
    }

    public void clicked(Player player, int i, ClickType clickType) {
        if (this.active) {
            Profile playerProfile = Hydrogen.getInstance().getProfileHandler().getProfile(player.getUniqueId()).get();
            playerProfile.setActivePrefix(null);
            playerProfile.updatePlayer(player);
            player.sendMessage((Object)ChatColor.GREEN + "Removed prefix.");
            return;
        }
        if (!this.authorized) {
            player.sendMessage((Object)ChatColor.RED + "You can't use this prefix!");
            if (this.prefix.isPurchaseable()) {
                player.sendMessage((Object)ChatColor.RED + "But it's available for a limited time at store.veltpvp.com!");
            }
            return;
        }
        Profile playerProfile = Hydrogen.getInstance().getProfileHandler().getProfile(player.getUniqueId()).get();
        playerProfile.setActivePrefix(this.prefix);
        playerProfile.updatePlayer(player);
        player.sendMessage((Object)ChatColor.GREEN + "Updated prefix.");
    }

    private DyeColor getColor() {
        return this.active ? DyeColor.GREEN : (this.authorized ? DyeColor.RED : DyeColor.GRAY);
    }

    public PrefixButton(String targetName, UUID targetUUID, Prefix prefix, boolean active, boolean authorized) {
        this.targetName = targetName;
        this.targetUUID = targetUUID;
        this.prefix = prefix;
        this.active = active;
        this.authorized = authorized;
    }
}

