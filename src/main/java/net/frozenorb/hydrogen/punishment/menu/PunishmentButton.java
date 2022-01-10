/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.frozenorb.qlib.menu.Button
 *  net.frozenorb.qlib.util.TimeUtils
 *  net.frozenorb.qlib.util.UUIDUtils
 *  net.minecraft.util.org.apache.commons.lang3.StringUtils
 *  org.bukkit.ChatColor
 *  org.bukkit.DyeColor
 *  org.bukkit.Material
 *  org.bukkit.entity.Player
 *  org.bukkit.event.inventory.ClickType
 */
package net.frozenorb.hydrogen.punishment.menu;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import net.frozenorb.hydrogen.punishment.Punishment;
import net.frozenorb.qlib.menu.Button;
import net.frozenorb.qlib.util.TimeUtils;
import net.frozenorb.qlib.util.UUIDUtils;
import net.minecraft.util.org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public class PunishmentButton
extends Button {
    private Punishment punishment;
    private String addedByResolved;
    private boolean showReason;

    public String getName(Player player) {
        return (Object)ChatColor.YELLOW + TimeUtils.formatIntoCalendarString((Date)new Date(this.punishment.getAddedAt()));
    }

    public List<String> getDescription(Player player) {
        ArrayList description = Lists.newArrayList();
        description.add(ChatColor.GRAY.toString() + (Object)ChatColor.STRIKETHROUGH + StringUtils.repeat((char)'-', (int)25));
        String by = this.punishment.getAddedBy() == null ? "Console" : this.addedByResolved;
        int seconds = this.punishment.getExpiresAt() > 0L ? TimeUtils.getSecondsBetween((Date)new Date(), (Date)new Date(this.punishment.getExpiresAt())) : 0;
        String actor = this.punishment.getActorType() + (Object)ChatColor.YELLOW + " : " + (Object)ChatColor.RED + this.punishment.getActorName();
        if (this.punishment.getActorType().equals("Website")) {
            actor = this.punishment.getActorType();
        }
        description.add((Object)ChatColor.YELLOW + "By: " + (Object)ChatColor.RED + by);
        description.add((Object)ChatColor.YELLOW + "Added on: " + (Object)ChatColor.RED + actor);
        if (this.showReason || this.punishment.getAddedBy() == null) {
            description.add((Object)ChatColor.YELLOW + "Reason: " + (Object)ChatColor.RED + this.punishment.getPublicReason());
        }
        if (player.hasPermission("minehq.punishment.view.internal") || player.getUniqueId().equals(this.punishment.getAddedBy())) {
            description.add((Object)ChatColor.YELLOW + "Internal reason: " + (Object)ChatColor.RED + this.punishment.getPrivateReason());
        }
        if (this.punishment.isActive()) {
            if (this.punishment.getExpiresAt() != 0L) {
                description.add(ChatColor.GRAY.toString() + (Object)ChatColor.STRIKETHROUGH + StringUtils.repeat((char)'-', (int)25));
                description.add((Object)ChatColor.YELLOW + "Time remaining: " + (Object)ChatColor.RED + TimeUtils.formatIntoDetailedString((int)seconds));
            } else {
                description.add(ChatColor.GRAY.toString() + (Object)ChatColor.STRIKETHROUGH + StringUtils.repeat((char)'-', (int)25));
                description.add((Object)ChatColor.YELLOW + "This is a permanent punishment.");
            }
        } else if (this.punishment.isRemoved()) {
            String removedBy = this.punishment.getRemovedBy() == null ? "Console" : UUIDUtils.name((UUID)this.punishment.getRemovedBy());
            description.add(ChatColor.GRAY.toString() + (Object)ChatColor.STRIKETHROUGH + StringUtils.repeat((char)'-', (int)25));
            description.add((Object)ChatColor.RED + "Removed:");
            description.add((Object)ChatColor.YELLOW + removedBy + ": " + (Object)ChatColor.RED + this.punishment.getRemovalReason());
            description.add((Object)ChatColor.RED + "at " + (Object)ChatColor.YELLOW + TimeUtils.formatIntoCalendarString((Date)new Date(this.punishment.getRemovedAt())));
            if (this.punishment.getExpiresAt() != 0L) {
                description.add("");
                description.add((Object)ChatColor.YELLOW + "Duration: " + TimeUtils.formatIntoDetailedString((int)((int)((this.punishment.getExpiresAt() - this.punishment.getAddedAt()) / 1000L) + 1)));
            }
        } else if (this.punishment.isExpired()) {
            description.add((Object)ChatColor.YELLOW + "Duration: " + TimeUtils.formatIntoDetailedString((int)((int)((this.punishment.getExpiresAt() - this.punishment.getAddedAt()) / 1000L) + 1)));
            description.add((Object)ChatColor.GREEN + "Expired");
        }
        description.add(ChatColor.GRAY.toString() + (Object)ChatColor.STRIKETHROUGH + StringUtils.repeat((char)'-', (int)25));
        return description;
    }

    public Material getMaterial(Player player) {
        return Material.WOOL;
    }

    public byte getDamageValue(Player player) {
        return this.punishment.isActive() ? DyeColor.RED.getWoolData() : DyeColor.LIME.getWoolData();
    }

    public void clicked(Player player, int i, ClickType clickType) {
    }

    public PunishmentButton(Punishment punishment, String addedByResolved, boolean showReason) {
        this.punishment = punishment;
        this.addedByResolved = addedByResolved;
        this.showReason = showReason;
    }
}

