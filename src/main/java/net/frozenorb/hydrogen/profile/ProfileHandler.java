/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Maps
 *  net.frozenorb.qlib.qLib
 *  net.minecraft.util.com.google.common.reflect.TypeToken
 *  org.bukkit.Bukkit
 *  org.bukkit.plugin.Plugin
 *  org.json.JSONObject
 */
package net.frozenorb.hydrogen.profile;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import net.frozenorb.hydrogen.Hydrogen;
import net.frozenorb.hydrogen.connection.RequestHandler;
import net.frozenorb.hydrogen.connection.RequestResponse;
import net.frozenorb.hydrogen.profile.Profile;
import net.frozenorb.qlib.qLib;
import net.minecraft.util.com.google.common.reflect.TypeToken;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.json.JSONObject;

public class ProfileHandler {
    private Set<UUID> totpEnabled = new HashSet<UUID>();
    private Map<UUID, Profile> profiles = Maps.newConcurrentMap();

    public ProfileHandler() {
        this.refresh();
        Bukkit.getScheduler().scheduleAsyncRepeatingTask((Plugin)Hydrogen.getInstance(), this::refresh, 0L, 2400L);
    }

    private void refresh() {
        RequestResponse response = RequestHandler.get("/dumps/totp");
        if (response.wasSuccessful()) {
            this.totpEnabled = (Set)qLib.PLAIN_GSON.fromJson(response.getResponse(), new TypeToken<Set<UUID>>(){}.getType());
        } else {
            Bukkit.getLogger().warning("ProfileHandler - Could not get totp-enabled users from API: " + response.getErrorMessage());
        }
    }

    public void remove(UUID player) {
        this.profiles.remove(player);
    }

    public Optional<Profile> getProfile(UUID player) {
        return Optional.ofNullable(this.profiles.get(player));
    }

    public Profile loadProfile(UUID player, String name, String ip) {
        RequestResponse response = RequestHandler.post("/users/" + player.toString() + "/login", (Map<String, Object>)ImmutableMap.of((Object)"username", (Object)name, (Object)"userIp", (Object)ip));
        if (response.wasSuccessful()) {
            JSONObject json = response.asJSONObject();
            Profile profile = new Profile(player, json);
            this.profiles.put(player, profile);
            return profile;
        }
        Bukkit.getLogger().warning("ProfileHandler - Could not load profile for " + player.toString() + ": " + response.getErrorMessage());
        return null;
    }

    public Set<UUID> getTotpEnabled() {
        return this.totpEnabled;
    }

    public Map<UUID, Profile> getProfiles() {
        return this.profiles;
    }
}

