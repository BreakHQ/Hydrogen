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
package net.frozenorb.hydrogen.commands.grant.menu;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.frozenorb.hydrogen.Hydrogen;
import net.frozenorb.hydrogen.commands.grant.menu.RankButton;
import net.frozenorb.hydrogen.rank.Rank;
import net.frozenorb.qlib.menu.Button;
import net.frozenorb.qlib.menu.Menu;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class RanksMenu
extends Menu {
    private static final String CREATE_GRANT_PERMISSION = "minehq.grant.create";
    private String targetName;
    private UUID targetUUID;

    public String getTitle(Player player) {
        return ChatColor.YELLOW.toString() + (Object)ChatColor.BOLD + "Choose a Rank";
    }

    public Map<Integer, Button> getButtons(Player player) {
        HashMap buttons = Maps.newHashMap();
        List<Rank> ranks = this.getAllowedRanks(player);
        for (int i = 0; i < ranks.size(); ++i) {
            buttons.put(i, new RankButton(this.targetName, this.targetUUID, ranks.get(i)));
        }
        return buttons;
    }

    private List<Rank> getAllowedRanks(Player player) {
        List<Rank> allRanks = Hydrogen.getInstance().getRankHandler().getRanks();
        ArrayList ranks = Lists.newArrayList();
        for (int i = 0; i < allRanks.size(); ++i) {
            if (i == 0 || !this.isAllowed(allRanks.get(i), player)) continue;
            ranks.add(allRanks.get(i));
        }
        ranks.sort(Rank.DISPLAY_WEIGHT_COMPARATOR);
        return ranks;
    }

    private boolean isAllowed(Rank rank, Player player) {
        return player.hasPermission("minehq.grant.create." + rank.getId());
    }

    public void onClose(final Player player) {
        new BukkitRunnable(){

            public void run() {
                if (!Menu.currentlyOpenedMenus.containsKey(player.getName())) {
                    player.sendMessage((Object)ChatColor.RED + "Granting cancelled.");
                }
            }
        }.runTaskLater((Plugin)Hydrogen.getInstance(), 1L);
    }

    public RanksMenu(String targetName, UUID targetUUID) {
        this.targetName = targetName;
        this.targetUUID = targetUUID;
    }
}

