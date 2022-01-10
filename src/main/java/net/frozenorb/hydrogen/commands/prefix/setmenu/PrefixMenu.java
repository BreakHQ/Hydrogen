/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  net.frozenorb.qlib.menu.Button
 *  net.frozenorb.qlib.menu.Menu
 *  net.minecraft.util.com.google.common.base.Objects
 *  org.bukkit.ChatColor
 *  org.bukkit.entity.Player
 */
package net.frozenorb.hydrogen.commands.prefix.setmenu;

import com.google.common.collect.Maps;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.frozenorb.hydrogen.Hydrogen;
import net.frozenorb.hydrogen.commands.prefix.setmenu.PrefixButton;
import net.frozenorb.hydrogen.prefix.Prefix;
import net.frozenorb.hydrogen.profile.Profile;
import net.frozenorb.qlib.menu.Button;
import net.frozenorb.qlib.menu.Menu;
import net.minecraft.util.com.google.common.base.Objects;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class PrefixMenu
extends Menu {
    private String targetName;
    private UUID targetUUID;

    public String getTitle(Player player) {
        return ChatColor.YELLOW.toString() + (Object)ChatColor.BOLD + "Choose a Prefix";
    }

    public Map<Integer, Button> getButtons(Player player) {
        HashMap buttons = Maps.newHashMap();
        List<Prefix> prefixes = Hydrogen.getInstance().getPrefixHandler().getPrefixes();
        Profile playerProfile = Hydrogen.getInstance().getProfileHandler().getProfile(player.getUniqueId()).get();
        Set<Prefix> authorizedPrefixes = playerProfile.getAuthorizedPrefixes();
        for (int i = 0; i < prefixes.size(); ++i) {
            buttons.put(i, new PrefixButton(this.targetName, this.targetUUID, prefixes.get(i), Objects.equal((Object)prefixes.get(i), (Object)playerProfile.getActivePrefix()), authorizedPrefixes.contains(prefixes.get(i))));
        }
        return buttons;
    }

    public PrefixMenu(String targetName, UUID targetUUID) {
        this.targetName = targetName;
        this.targetUUID = targetUUID;
    }
}

