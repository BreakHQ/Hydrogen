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
package net.frozenorb.hydrogen.prefix.menu;

import com.google.common.collect.Maps;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.frozenorb.hydrogen.prefix.PrefixGrant;
import net.frozenorb.hydrogen.prefix.menu.PrefixGrantButton;
import net.frozenorb.qlib.menu.Button;
import net.frozenorb.qlib.menu.pagination.PaginatedMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public class PrefixGrantMenu
extends PaginatedMenu {
    private final Map<PrefixGrant, String> prefixGrants;

    public String getPrePaginatedTitle(Player player) {
        return (Object)ChatColor.RED + "Prefix Grants";
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
        for (Map.Entry<PrefixGrant, String> entry : this.prefixGrants.entrySet()) {
            buttons.put(index, new PrefixGrantButton(entry.getKey(), entry.getValue()));
            ++index;
        }
        return buttons;
    }

    public PrefixGrantMenu(Map<PrefixGrant, String> prefixGrants) {
        this.prefixGrants = prefixGrants;
    }
}

