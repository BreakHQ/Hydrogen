/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 *  org.bukkit.permissions.PermissionAttachment
 *  org.bukkit.plugin.Plugin
 */
package net.frozenorb.hydrogen.permission;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.frozenorb.hydrogen.Hydrogen;
import net.frozenorb.hydrogen.rank.Rank;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.Plugin;

public class PermissionHandler {
    private static Field permissionsField;
    private Map<Rank, Map<String, Boolean>> permissionCache = new ConcurrentHashMap<Rank, Map<String, Boolean>>();
    private final Map<UUID, PermissionAttachment> attachments = new ConcurrentHashMap<UUID, PermissionAttachment>();

    public Map<String, Boolean> getPermissions(Rank rank) {
        return this.permissionCache.get(rank);
    }

    public void update(Player player, Map<String, Boolean> permissions) {
        this.attachments.computeIfAbsent(player.getUniqueId(), i -> player.addAttachment((Plugin)Hydrogen.getInstance()));
        PermissionAttachment attachment = this.attachments.get(player.getUniqueId());
        try {
            permissionsField.set((Object)attachment, permissions);
            player.recalculatePermissions();
        }
        catch (IllegalAccessException ex) {
            ex.printStackTrace();
        }
    }

    public void removeAttachment(Player player) {
        this.attachments.remove(player.getUniqueId());
    }

    public void setPermissionCache(Map<Rank, Map<String, Boolean>> permissionCache) {
        this.permissionCache = permissionCache;
    }

    static {
        try {
            permissionsField = PermissionAttachment.class.getDeclaredField("permissions");
            permissionsField.setAccessible(true);
        }
        catch (NoSuchFieldException ex) {
            ex.printStackTrace();
        }
    }
}

