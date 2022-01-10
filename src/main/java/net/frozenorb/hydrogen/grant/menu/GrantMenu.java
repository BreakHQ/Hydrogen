/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  net.frozenorb.qlib.menu.Button
 *  net.frozenorb.qlib.menu.pagination.PaginatedMenu
 *  org.bukkit.ChatColor
 *  org.bukkit.Material
 *  org.bukkit.entity.Player
 *  org.bukkit.event.inventory.ClickType
 */
package net.frozenorb.hydrogen.grant.menu;

import com.google.common.collect.Maps;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.frozenorb.hydrogen.grant.Grant;
import net.frozenorb.hydrogen.grant.menu.GrantButton;
import net.frozenorb.qlib.menu.Button;
import net.frozenorb.qlib.menu.pagination.PaginatedMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public class GrantMenu
extends PaginatedMenu {
    private final Map<Grant, String> grants;

    public String getPrePaginatedTitle(Player player) {
        return (Object)ChatColor.RED + "Grants";
    }

    public Map<Integer, Button> getGlobalButtons(Player player) {
        HashMap buttons = Maps.newHashMap();
        buttons.put(4, new Button(){

            public String getName(Player player) {
                return (Object)ChatColor.YELLOW + "Back";
            }

            public List<String> getDescription(Player player) {
                return null;
            }

            public Material getMaterial(Player player) {
                return Material.PAPER;
            }

            public byte getDamageValue(Player player) {
                return 0;
            }

            public void clicked(Player player, int i, ClickType clickType) {
                player.closeInventory();
            }
        });
        return buttons;
    }

    public Map<Integer, Button> getAllPagesButtons(Player player) {
        HashMap buttons = Maps.newHashMap();
        int index = 0;
        for (Map.Entry<Grant, String> entry : this.grants.entrySet()) {
            buttons.put(index, new GrantButton(entry.getKey(), entry.getValue()));
            ++index;
        }
        return buttons;
    }

    public GrantMenu(Map<Grant, String> grants) {
        this.grants = grants;
    }
}

