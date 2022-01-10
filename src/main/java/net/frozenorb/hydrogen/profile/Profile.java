/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Objects
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  net.frozenorb.qlib.nametag.FrozenNametagHandler
 *  net.frozenorb.qlib.qLib
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.entity.Player
 *  org.bukkit.metadata.FixedMetadataValue
 *  org.bukkit.metadata.MetadataValue
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scheduler.BukkitRunnable
 *  org.json.JSONArray
 *  org.json.JSONObject
 */
package net.frozenorb.hydrogen.profile;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import net.frozenorb.hydrogen.Hydrogen;
import net.frozenorb.hydrogen.Settings;
import net.frozenorb.hydrogen.connection.RequestHandler;
import net.frozenorb.hydrogen.connection.RequestResponse;
import net.frozenorb.hydrogen.prefix.Prefix;
import net.frozenorb.hydrogen.prefix.PrefixHandler;
import net.frozenorb.hydrogen.profile.ProfileHandler;
import net.frozenorb.hydrogen.punishment.Punishment;
import net.frozenorb.hydrogen.rank.Rank;
import net.frozenorb.hydrogen.rank.RankHandler;
import net.frozenorb.hydrogen.util.PermissionUtils;
import net.frozenorb.qlib.nametag.FrozenNametagHandler;
import net.frozenorb.qlib.qLib;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.JSONArray;
import org.json.JSONObject;

public final class Profile {
    private UUID player;
    private List<Rank> ranks;
    private Map<String, List<Rank>> scopeRanks;
    private boolean accessAllowed;
    private String accessDenialReason;
    private Map<String, Boolean> permissions;
    private boolean totpRequired;
    private boolean authenticated;
    private boolean ipWhitelisted;
    private Punishment mute;
    private ChatColor iconColor;
    private ChatColor nameColor;
    private Set<Prefix> authorizedPrefixes;
    private Prefix activePrefix;
    private Rank bestGeneralRank;
    private Rank bestDisplayRank;

    public Profile(UUID uuid, JSONObject json) {
        this.update(uuid, json);
    }

    /*
     * WARNING - void declaration
     */
    public void update(UUID uuid, JSONObject json) {
        void var8_12;
        RankHandler rankHandler = Hydrogen.getInstance().getRankHandler();
        PrefixHandler prefixHandler = Hydrogen.getInstance().getPrefixHandler();
        this.player = uuid;
        JSONArray ranks = json.getJSONArray("ranks");
        this.ranks = new ArrayList<Rank>();
        for (int i = 0; i < ranks.length(); ++i) {
            Optional<Rank> rank = rankHandler.getRank(ranks.getString(i));
            rank.ifPresent(this.ranks::add);
        }
        Rank bestGeneralRank = null;
        for (Rank rank : this.ranks) {
            if (bestGeneralRank != null && rank.getGeneralWeight() < bestGeneralRank.getGeneralWeight()) continue;
            bestGeneralRank = rank;
        }
        this.bestGeneralRank = bestGeneralRank;
        Rank bestDisplayRank = null;
        for (Rank rank : this.ranks) {
            if (bestDisplayRank != null && rank.getDisplayWeight() < bestDisplayRank.getDisplayWeight()) continue;
            bestDisplayRank = rank;
        }
        this.bestDisplayRank = bestDisplayRank;
        this.scopeRanks = new HashMap<String, List<Rank>>();
        if (json.has("scopeRanks")) {
            json.getJSONObject("scopeRanks").toMap().forEach((scope, rawRanks) -> {
                ArrayList parsedRanks = new ArrayList();
                for (String rawRank : (List)rawRanks) {
                    Optional<Rank> parsedRank = rankHandler.getRank(rawRank);
                    parsedRank.ifPresent(parsedRanks::add);
                }
                this.scopeRanks.put((String)scope, parsedRanks);
            });
        }
        this.mute = json.has("mute") ? (Punishment)qLib.PLAIN_GSON.fromJson(json.get("mute").toString(), Punishment.class) : null;
        if (json.has("access")) {
            this.accessAllowed = json.getJSONObject("access").optBoolean("allowed", true);
            this.accessDenialReason = json.getJSONObject("access").optString("message", "");
        } else {
            this.accessAllowed = true;
            this.accessDenialReason = "";
        }
        HashMap hashMap = Maps.newHashMap();
        for (Rank rank : this.ranks) {
            Map<String, Boolean> map = PermissionUtils.mergePermissions((Map<String, Boolean>)var8_12, Hydrogen.getInstance().getPermissionHandler().getPermissions(rank));
        }
        this.permissions = var8_12;
        this.iconColor = json.has("iconColor") ? ChatColor.valueOf((String)json.getString("iconColor")) : null;
        this.nameColor = json.has("nameColor") ? ChatColor.valueOf((String)json.getString("nameColor")) : null;
        if (json.has("prefixes")) {
            JSONArray jSONArray = json.getJSONArray("prefixes");
            HashSet authorizedPrefixes = Sets.newHashSet();
            for (int i = 0; i < jSONArray.length(); ++i) {
                Optional<Prefix> prefix2 = prefixHandler.getPrefix(jSONArray.getString(i));
                prefix2.ifPresent(authorizedPrefixes::add);
            }
            this.authorizedPrefixes = authorizedPrefixes;
        } else {
            this.authorizedPrefixes = ImmutableSet.of();
        }
        if (json.has("activePrefix")) {
            prefixHandler.getPrefix(json.getString("activePrefix")).ifPresent(prefix -> {
                if (this.authorizedPrefixes.stream().anyMatch(ap -> ap.getId().equals(prefix.getId()))) {
                    this.activePrefix = prefix;
                }
            });
        }
    }

    public void checkTotpLock(String ip) {
        ProfileHandler profileHandler = Hydrogen.getInstance().getProfileHandler();
        if (!profileHandler.getTotpEnabled().contains(this.player) && !this.getBestGeneralRank().isStaffRank()) {
            this.totpRequired = false;
            return;
        }
        RequestResponse response = RequestHandler.get("/users/" + this.player.toString() + "/requiresTotp", (Map<String, Object>)ImmutableMap.of((Object)"userIp", (Object)ip));
        if (response.wasSuccessful()) {
            this.totpRequired = response.asJSONObject().getBoolean("required");
            this.ipWhitelisted = response.asJSONObject().getString("message").equals("NOT_REQUIRED_IP_PRE_AUTHORIZED");
        }
    }

    private void updatePermissions(Player player) {
        Hydrogen.getInstance().getPermissionHandler().update(player, this.permissions);
    }

    public void updatePlayer(final Player player) {
        if (!this.accessAllowed) {
            new BukkitRunnable(){

                public void run() {
                    if (Profile.this.accessDenialReason != null && Profile.this.accessDenialReason.contains("VPNs are not allowed")) {
                        player.kickPlayer(Profile.this.accessDenialReason);
                        return;
                    }
                    if (Settings.isBannedJoinsEnabled()) {
                        String message = (Object)ChatColor.RED + "Banned players are only allowed to use /register.";
                        player.setMetadata("Locked", (MetadataValue)new FixedMetadataValue((Plugin)Hydrogen.getInstance(), (Object)message));
                    } else {
                        player.kickPlayer(Profile.this.accessDenialReason);
                    }
                }
            }.runTask((Plugin)Hydrogen.getInstance());
            return;
        }
        Rank bestDisplayRank = this.getBestDisplayRank();
        String gameColor = bestDisplayRank.getGameColor();
        if (bestDisplayRank.getId().equals("velt-plus")) {
            gameColor = ((ChatColor)Objects.firstNonNull((Object)this.nameColor, (Object)ChatColor.WHITE)).toString() + (Object)ChatColor.BOLD;
        }
        player.setDisplayName(gameColor + player.getName() + (Object)ChatColor.RESET);
        String gamePrefix = bestDisplayRank.getGamePrefix();
        if (bestDisplayRank.getId().equals("velt-plus")) {
            gamePrefix = ((ChatColor)Objects.firstNonNull((Object)this.iconColor, (Object)ChatColor.GRAY)).toString() + "\u2738" + gameColor + (Object)ChatColor.BOLD;
        }
        if (this.activePrefix != null && !Settings.isClean()) {
            gamePrefix = gamePrefix + ChatColor.translateAlternateColorCodes((char)'&', (String)this.activePrefix.getPrefix());
        }
        player.setMetadata("HydrogenPrefix", (MetadataValue)new FixedMetadataValue((Plugin)Hydrogen.getInstance(), (Object)gamePrefix));
        FrozenNametagHandler.reloadPlayer((Player)player);
        boolean totpRequired = false;
        boolean userTotpRequired = this.totpRequired;
        boolean staffTotpRequired = Settings.isForceStaffTotp() && this.getBestGeneralRank().isStaffRank();
        String totpMessage = null;
        if ((userTotpRequired || staffTotpRequired) && !this.authenticated && !this.ipWhitelisted) {
            totpRequired = true;
        }
        if (totpRequired && !player.hasMetadata("ForceAuth")) {
            totpMessage = !userTotpRequired && staffTotpRequired ? (Object)ChatColor.RED + "Please set up your two-factor authentication using /2fasetup." : (Object)ChatColor.RED + "Please provide your two-factor code. Type \"/auth <code>\" to authenticate.";
            player.setMetadata("Locked", (MetadataValue)new FixedMetadataValue((Plugin)Hydrogen.getInstance(), (Object)totpMessage));
        } else {
            player.removeMetadata("Locked", (Plugin)Hydrogen.getInstance());
        }
        this.updatePermissions(player);
        final String finalTotpMessage = totpMessage;
        new BukkitRunnable(){

            public void run() {
                if (finalTotpMessage != null) {
                    player.sendMessage(finalTotpMessage);
                }
            }
        }.runTaskLater((Plugin)Hydrogen.getInstance(), 10L);
    }

    public void setActivePrefix(Prefix prefix) {
        this.activePrefix = prefix;
        Bukkit.getScheduler().runTaskAsynchronously((Plugin)Hydrogen.getInstance(), () -> {
            HashMap meta = Maps.newHashMap();
            meta.put("prefix", prefix == null ? null : prefix.getId());
            RequestResponse response = RequestHandler.post("/users/" + this.player + "/prefix", meta);
            if (!response.wasSuccessful()) {
                Bukkit.getLogger().info(response.asJSONObject().toString());
            }
        });
    }

    public void authenticated() {
        this.authenticated = true;
    }

    public boolean isMuted() {
        return this.mute != null;
    }

    public UUID getPlayer() {
        return this.player;
    }

    public List<Rank> getRanks() {
        return this.ranks;
    }

    public Map<String, List<Rank>> getScopeRanks() {
        return this.scopeRanks;
    }

    public boolean isAccessAllowed() {
        return this.accessAllowed;
    }

    public String getAccessDenialReason() {
        return this.accessDenialReason;
    }

    public Map<String, Boolean> getPermissions() {
        return this.permissions;
    }

    public boolean isTotpRequired() {
        return this.totpRequired;
    }

    public boolean isAuthenticated() {
        return this.authenticated;
    }

    public boolean isIpWhitelisted() {
        return this.ipWhitelisted;
    }

    public Punishment getMute() {
        return this.mute;
    }

    public void setMute(Punishment mute) {
        this.mute = mute;
    }

    public ChatColor getIconColor() {
        return this.iconColor;
    }

    public void setIconColor(ChatColor iconColor) {
        this.iconColor = iconColor;
    }

    public ChatColor getNameColor() {
        return this.nameColor;
    }

    public void setNameColor(ChatColor nameColor) {
        this.nameColor = nameColor;
    }

    public Set<Prefix> getAuthorizedPrefixes() {
        return this.authorizedPrefixes;
    }

    public void setAuthorizedPrefixes(Set<Prefix> authorizedPrefixes) {
        this.authorizedPrefixes = authorizedPrefixes;
    }

    public Prefix getActivePrefix() {
        return this.activePrefix;
    }

    public Rank getBestGeneralRank() {
        return this.bestGeneralRank;
    }

    public Rank getBestDisplayRank() {
        return this.bestDisplayRank;
    }
}

