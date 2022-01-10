/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.frozenorb.qlib.util.TimeUtils
 *  net.minecraft.server.v1_7_R4.MinecraftServer
 *  net.minecraft.util.com.mojang.authlib.GameProfile
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.player.AsyncPlayerChatEvent
 *  org.bukkit.event.player.AsyncPlayerPreLoginEvent
 *  org.bukkit.event.player.AsyncPlayerPreLoginEvent$Result
 *  org.bukkit.event.player.PlayerJoinEvent
 *  org.bukkit.event.player.PlayerLoginEvent
 *  org.bukkit.event.player.PlayerLoginEvent$Result
 *  org.bukkit.event.player.PlayerQuitEvent
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scheduler.BukkitRunnable
 *  org.json.JSONObject
 */
package net.frozenorb.hydrogen.listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import net.frozenorb.hydrogen.Hydrogen;
import net.frozenorb.hydrogen.Settings;
import net.frozenorb.hydrogen.commands.punishment.parameter.PunishmentTarget;
import net.frozenorb.hydrogen.profile.Profile;
import net.frozenorb.qlib.util.TimeUtils;
import net.minecraft.server.v1_7_R4.MinecraftServer;
import net.minecraft.util.com.mojang.authlib.GameProfile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.JSONObject;

public class GeneralListener
implements Listener {
    public static Map<UUID, Long> joinTime = new HashMap<UUID, Long>();

    @EventHandler(priority=EventPriority.LOWEST)
    public void preLogin(final AsyncPlayerPreLoginEvent event) {
        Profile profile = Hydrogen.getInstance().getProfileHandler().loadProfile(event.getUniqueId(), event.getName(), event.getAddress().toString().replace("/", ""));
        String disallowedReason = null;
        if (profile != null && !profile.isAccessAllowed()) {
            disallowedReason = profile.getAccessDenialReason();
        }
        if (disallowedReason != null) {
            if (disallowedReason.contains("VPNs are not allowed")) {
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, (Object)ChatColor.RED + disallowedReason);
                return;
            }
            if (!Settings.isBannedJoinsEnabled()) {
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, (Object)ChatColor.RED + disallowedReason);
                return;
            }
            final String finalDisallowedReason = disallowedReason;
            new BukkitRunnable(){

                public void run() {
                    Player player = Bukkit.getPlayer((UUID)event.getUniqueId());
                    if (player != null) {
                        for (String line : finalDisallowedReason.split("\n")) {
                            player.sendMessage((Object)ChatColor.RED + line);
                        }
                    }
                }
            }.runTaskLater((Plugin)Hydrogen.getInstance(), 40L);
        }
        if (profile != null) {
            profile.checkTotpLock(event.getAddress().toString().replace("/", ""));
        }
        if (Bukkit.getOnlinePlayers().size() >= Bukkit.getMaxPlayers()) {
            boolean bypasses = false;
            if (profile != null && profile.getPermissions() != null) {
                boolean permissionBypass = profile.getPermissions().getOrDefault("server.joinfull", false);
                boolean opBypass = MinecraftServer.getServer().getPlayerList().isOp(new GameProfile(event.getUniqueId(), event.getName()));
                boolean bl = bypasses = permissionBypass || opBypass;
            }
            if (!bypasses) {
                event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_FULL);
                event.setKickMessage((Object)ChatColor.RED + "Server is full! Buy a reserved slot at store." + Settings.getNetworkWebsite() + "!");
            } else {
                event.allow();
            }
        }
    }

    @EventHandler(priority=EventPriority.LOWEST)
    public void onPlayerLogin(PlayerLoginEvent event) {
        UUID playerUuid = event.getPlayer().getUniqueId();
        Profile profile = Hydrogen.getInstance().getProfileHandler().getProfile(playerUuid).orElse(null);
        if (profile == null) {
            profile = new Profile(playerUuid, new JSONObject().put("ranks", new ArrayList()).put("scopeRanks", new HashMap()));
            Hydrogen.getInstance().getProfileHandler().getProfiles().put(playerUuid, profile);
        }
        profile.updatePlayer(event.getPlayer());
        if (Bukkit.getOnlinePlayers().size() >= Bukkit.getMaxPlayers()) {
            boolean bypasses = MinecraftServer.getServer().getPlayerList().isOp(new GameProfile(event.getPlayer().getUniqueId(), event.getPlayer().getName())) ? true : (profile.getPermissions() != null ? profile.getPermissions().getOrDefault("server.joinfull", false) : false);
            if (!bypasses) {
                event.disallow(PlayerLoginEvent.Result.KICK_FULL, (Object)ChatColor.RED + "Server is full! Buy a reserved slot at store." + Settings.getNetworkWebsite() + "!");
            } else {
                event.allow();
            }
        }
    }

    @EventHandler
    public void onAsyncPlayerChat0(AsyncPlayerChatEvent event) {
        Optional<Profile> optionalProfile = Hydrogen.getInstance().getProfileHandler().getProfile(event.getPlayer().getUniqueId());
        if (optionalProfile.isPresent() && optionalProfile.get().isMuted()) {
            int timeRemaining = (int)((optionalProfile.get().getMute().getExpiresAt() - System.currentTimeMillis()) / 1000L);
            if (timeRemaining <= 0) {
                timeRemaining = -1;
            }
            event.getPlayer().sendMessage((Object)ChatColor.RED + "You are currently silenced." + (timeRemaining == -1 ? "" : " Your mute will be lifted in " + (Object)ChatColor.BOLD + TimeUtils.formatIntoDetailedString((int)timeRemaining) + (Object)ChatColor.RED + "."));
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onLogin(PlayerJoinEvent event) {
        joinTime.put(event.getPlayer().getUniqueId(), System.currentTimeMillis());
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Hydrogen.getInstance().getProfileHandler().remove(player.getUniqueId());
        Hydrogen.getInstance().getPermissionHandler().removeAttachment(player);
        Hydrogen.getInstance().getServerHandler().leave(player.getUniqueId());
        PunishmentTarget.getRecentlyDisconnected().add(player.getName());
        Bukkit.getScheduler().runTaskLater((Plugin)Hydrogen.getInstance(), () -> PunishmentTarget.getRecentlyDisconnected().remove(player.getName()), 600L);
    }
}

