/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.frozenorb.qlib.menu.Button
 *  net.frozenorb.qlib.util.TimeUtils
 *  net.minecraft.util.org.apache.commons.lang3.StringUtils
 *  org.bukkit.ChatColor
 *  org.bukkit.Material
 *  org.bukkit.entity.Player
 *  org.bukkit.event.inventory.ClickType
 */
package net.frozenorb.hydrogen.commands.grant.menu;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import net.frozenorb.hydrogen.commands.grant.menu.ScopesMenu;
import net.frozenorb.hydrogen.connection.RequestHandler;
import net.frozenorb.hydrogen.connection.RequestResponse;
import net.frozenorb.hydrogen.rank.Rank;
import net.frozenorb.hydrogen.server.ServerGroup;
import net.frozenorb.qlib.menu.Button;
import net.frozenorb.qlib.util.TimeUtils;
import net.minecraft.util.org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public class GrantButton
extends Button {
    private Rank rank;
    private String targetName;
    private UUID targetUUID;
    private String reason;
    private ScopesMenu parent;
    private List<ServerGroup> scopes;
    private int duration;

    public String getName(Player player) {
        return (Object)ChatColor.GREEN + "Confirm and Grant";
    }

    public List<String> getDescription(Player player) {
        ArrayList description = Lists.newArrayList();
        description.add(ChatColor.GRAY.toString() + (Object)ChatColor.STRIKETHROUGH + StringUtils.repeat((char)'-', (int)30));
        description.add((Object)ChatColor.BLUE + "Click to add the " + (Object)ChatColor.WHITE + this.rank.getFormattedName() + (Object)ChatColor.BLUE + " to " + (Object)ChatColor.WHITE + this.targetName + (Object)ChatColor.BLUE + ".");
        if (this.parent.isGlobal()) {
            description.add((Object)ChatColor.BLUE + "This grant will be " + (Object)ChatColor.WHITE + "Global" + (Object)ChatColor.BLUE + ".");
        } else {
            List scopes = this.scopes.stream().map(ServerGroup::getId).collect(Collectors.toList());
            description.add((Object)ChatColor.BLUE + "This grant will apply on: " + (Object)ChatColor.WHITE + scopes.toString());
        }
        description.add((Object)ChatColor.BLUE + "Reasoning: " + (Object)ChatColor.WHITE + this.reason);
        description.add((Object)ChatColor.BLUE + "Duration: " + (Object)ChatColor.WHITE + (this.duration > 0 ? TimeUtils.formatIntoDetailedString((int)this.duration) : "Permanent"));
        description.add(ChatColor.GRAY.toString() + (Object)ChatColor.STRIKETHROUGH + StringUtils.repeat((char)'-', (int)30));
        return description;
    }

    public Material getMaterial(Player player) {
        return Material.DIAMOND_SWORD;
    }

    public byte getDamageValue(Player player) {
        return 0;
    }

    public void clicked(Player player, int i, ClickType clickType) {
        this.grant(this.targetUUID, this.targetName, this.reason, this.scopes, this.rank, this.duration, player);
        player.closeInventory();
    }

    private void grant(UUID user, String targetName, String reason, List<ServerGroup> scopes, Rank rank, int expiresIn, Player sender) {
        ArrayList finalScopes = Lists.newArrayList();
        finalScopes.addAll(scopes.stream().map(ServerGroup::getId).collect(Collectors.toList()));
        HashMap<String, Object> body = new HashMap<String, Object>();
        body.put("user", user);
        body.put("reason", reason);
        body.put("scopes", finalScopes.toArray(new String[finalScopes.size()]));
        body.put("rank", rank.getId());
        if (expiresIn > 0) {
            body.put("expiresIn", expiresIn);
        }
        body.put("addedBy", sender.getUniqueId().toString());
        body.put("addedByIp", sender.getAddress().getAddress().getHostAddress());
        RequestResponse response = RequestHandler.post("/grants", body);
        if (response.wasSuccessful()) {
            sender.sendMessage((Object)ChatColor.GREEN + "Successfully granted " + (Object)ChatColor.WHITE + targetName + (Object)ChatColor.GREEN + " the " + (Object)ChatColor.WHITE + rank.getFormattedName() + (Object)ChatColor.GREEN + " rank.");
            this.parent.setComplete(true);
        } else {
            sender.sendMessage((Object)ChatColor.RED + response.getErrorMessage());
        }
    }

    public GrantButton(Rank rank, String targetName, UUID targetUUID, String reason, ScopesMenu parent, List<ServerGroup> scopes, int duration) {
        this.rank = rank;
        this.targetName = targetName;
        this.targetUUID = targetUUID;
        this.reason = reason;
        this.parent = parent;
        this.scopes = scopes;
        this.duration = duration;
    }
}

