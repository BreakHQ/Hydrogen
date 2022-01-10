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
package net.frozenorb.hydrogen.punishment.menu;

import com.google.common.collect.Maps;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.frozenorb.hydrogen.punishment.Punishment;
import net.frozenorb.hydrogen.punishment.menu.MainPunishmentMenu;
import net.frozenorb.hydrogen.punishment.menu.PunishmentButton;
import net.frozenorb.qlib.menu.Button;
import net.frozenorb.qlib.menu.pagination.PaginatedMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public class PunishmentMenu
extends PaginatedMenu {
    private final String targetUUID;
    private final String targetName;
    private final Punishment.PunishmentType type;
    private final Map<Punishment, String> punishments;

    public String getPrePaginatedTitle(Player player) {
        return (Object)ChatColor.RED + this.type.getName() + "s";
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
                new MainPunishmentMenu(PunishmentMenu.this.targetUUID, PunishmentMenu.this.targetName).openMenu(player);
            }
        });
        return buttons;
    }

    public Map<Integer, Button> getAllPagesButtons(Player player) {
        HashMap buttons = Maps.newHashMap();
        int index = 0;
        for (Map.Entry<Punishment, String> entry : this.punishments.entrySet()) {
            buttons.put(index, new PunishmentButton(entry.getKey(), entry.getValue(), player.hasPermission("minehq.punishments.view.reason")));
            ++index;
        }
        return buttons;
    }

    public PunishmentMenu(String targetUUID, String targetName, Punishment.PunishmentType type, Map<Punishment, String> punishments) {
        this.targetUUID = targetUUID;
        this.targetName = targetName;
        this.type = type;
        this.punishments = punishments;
    }
}

