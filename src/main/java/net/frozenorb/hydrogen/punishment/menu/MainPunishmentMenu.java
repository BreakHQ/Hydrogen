/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Maps
 *  com.google.common.primitives.Longs
 *  net.frozenorb.qlib.menu.Button
 *  net.frozenorb.qlib.menu.Menu
 *  net.frozenorb.qlib.qLib
 *  net.minecraft.util.com.google.common.reflect.TypeToken
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.DyeColor
 *  org.bukkit.Material
 *  org.bukkit.entity.Player
 *  org.bukkit.event.inventory.ClickType
 *  org.bukkit.plugin.Plugin
 */
package net.frozenorb.hydrogen.punishment.menu;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.primitives.Longs;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.frozenorb.hydrogen.Hydrogen;
import net.frozenorb.hydrogen.connection.RequestHandler;
import net.frozenorb.hydrogen.connection.RequestResponse;
import net.frozenorb.hydrogen.punishment.Punishment;
import net.frozenorb.hydrogen.punishment.menu.PunishmentMenu;
import net.frozenorb.qlib.menu.Button;
import net.frozenorb.qlib.menu.Menu;
import net.frozenorb.qlib.qLib;
import net.minecraft.util.com.google.common.reflect.TypeToken;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.plugin.Plugin;

public class MainPunishmentMenu
extends Menu {
    private final String targetUUID;
    private final String targetName;

    public String getTitle(Player player) {
        return (Object)ChatColor.BLUE + "Punishments - " + this.targetName;
    }

    public Map<Integer, Button> getButtons(Player player) {
        HashMap buttons = Maps.newHashMap();
        if (player.hasPermission("minehq.punishments.view.blacklist")) {
            buttons.put(1, this.button(Punishment.PunishmentType.WARN));
            buttons.put(3, this.button(Punishment.PunishmentType.MUTE));
            buttons.put(5, this.button(Punishment.PunishmentType.BAN));
            buttons.put(7, this.button(Punishment.PunishmentType.BLACKLIST));
        } else {
            buttons.put(1, this.button(Punishment.PunishmentType.WARN));
            buttons.put(4, this.button(Punishment.PunishmentType.MUTE));
            buttons.put(7, this.button(Punishment.PunishmentType.BAN));
        }
        return buttons;
    }

    private Button button(final Punishment.PunishmentType type) {
        return new Button(){

            public String getName(Player player) {
                return (Object)ChatColor.RED + type.getName() + "s";
            }

            public List<String> getDescription(Player player) {
                return null;
            }

            public Material getMaterial(Player player) {
                return Material.WOOL;
            }

            public byte getDamageValue(Player player) {
                if (type == Punishment.PunishmentType.WARN) {
                    return DyeColor.YELLOW.getWoolData();
                }
                if (type == Punishment.PunishmentType.MUTE) {
                    return DyeColor.ORANGE.getWoolData();
                }
                if (type == Punishment.PunishmentType.BAN) {
                    return DyeColor.RED.getWoolData();
                }
                return DyeColor.BLACK.getWoolData();
            }

            public void clicked(Player player, int i, ClickType clickType) {
                player.closeInventory();
                player.sendMessage((Object)ChatColor.GREEN + "Loading " + MainPunishmentMenu.this.targetName + "'s " + type.getName() + "s...");
                Bukkit.getScheduler().scheduleAsyncDelayedTask((Plugin)Hydrogen.getInstance(), () -> {
                    RequestResponse response = RequestHandler.get("/punishments", (Map<String, Object>)ImmutableMap.of((Object)"user", (Object)MainPunishmentMenu.this.targetUUID));
                    if (response.wasSuccessful()) {
                        List allPunishments = (List)qLib.PLAIN_GSON.fromJson(response.getResponse(), new TypeToken<List<Punishment>>(){}.getType());
                        allPunishments.sort((first, second) -> Longs.compare((long)second.getAddedAt(), (long)first.getAddedAt()));
                        LinkedHashMap punishments = new LinkedHashMap();
                        allPunishments.stream().filter(punishment -> punishment.getType() == type).forEach(punishment -> punishments.put(punishment, punishment.resolveAddedBy()));
                        Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)Hydrogen.getInstance(), () -> new PunishmentMenu(MainPunishmentMenu.this.targetUUID, MainPunishmentMenu.this.targetName, type, punishments).openMenu(player));
                    } else {
                        player.sendMessage((Object)ChatColor.RED + response.getErrorMessage());
                    }
                });
            }
        };
    }

    public MainPunishmentMenu(String targetUUID, String targetName) {
        this.targetUUID = targetUUID;
        this.targetName = targetName;
    }
}

