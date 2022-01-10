/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  net.frozenorb.qlib.qLib
 *  net.frozenorb.qlib.util.TPSUtils
 *  net.minecraft.util.com.google.common.reflect.TypeToken
 *  org.bukkit.Bukkit
 *  org.bukkit.entity.Player
 *  org.bukkit.scheduler.BukkitRunnable
 *  org.json.JSONObject
 */
package net.frozenorb.hydrogen.server.heartbeat;

import com.google.common.collect.ImmutableMap;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.frozenorb.hydrogen.Hydrogen;
import net.frozenorb.hydrogen.connection.RequestHandler;
import net.frozenorb.hydrogen.connection.RequestResponse;
import net.frozenorb.hydrogen.profile.Profile;
import net.frozenorb.hydrogen.rank.Rank;
import net.frozenorb.hydrogen.util.PermissionUtils;
import net.frozenorb.qlib.qLib;
import net.frozenorb.qlib.util.TPSUtils;
import net.minecraft.util.com.google.common.reflect.TypeToken;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.JSONObject;

public class HeartbeatTask
extends BukkitRunnable {
    private boolean first = true;
    private static final Queue<Map<String, Object>> eventQueue = new ConcurrentLinkedQueue<Map<String, Object>>();

    public void run() {
        Map<String, Object> event;
        HashMap onlinePlayers = new HashMap();
        for (Player player : Bukkit.getOnlinePlayers()) {
            HashMap<String, String> data = new HashMap<String, String>();
            data.put("username", player.getName());
            data.put("userIp", player.getAddress().getAddress().getHostAddress());
            onlinePlayers.put(player.getUniqueId().toString(), data);
        }
        LinkedList<Map<String, Object>> events = new LinkedList<Map<String, Object>>();
        while ((event = eventQueue.poll()) != null) {
            events.add(event);
        }
        RequestResponse response = RequestHandler.post("/servers/heartbeat", (Map<String, Object>)ImmutableMap.of((Object)"players", onlinePlayers, (Object)"lastTps", (Object)TPSUtils.getTPS(), (Object)"events", events, (Object)"permissionsNeeded", (Object)this.first));
        if (response.wasSuccessful()) {
            JSONObject json = response.asJSONObject();
            JSONObject playersJson = json.getJSONObject("players");
            for (String key : playersJson.keySet()) {
                Player player;
                Profile profile;
                UUID uuid = UUID.fromString(key);
                JSONObject info = playersJson.getJSONObject(key);
                Optional<Profile> profileOptional = Hydrogen.getInstance().getProfileHandler().getProfile(uuid);
                if (profileOptional.isPresent()) {
                    profile = profileOptional.get();
                    profile.update(uuid, info);
                    player = Bukkit.getPlayer((UUID)uuid);
                    if (player == null) continue;
                    profile.updatePlayer(player);
                    continue;
                }
                profile = new Profile(uuid, info);
                Hydrogen.getInstance().getProfileHandler().getProfiles().put(uuid, profile);
                player = Bukkit.getPlayer((UUID)uuid);
                if (player == null) continue;
                profile.updatePlayer(player);
            }
            if (json.has("permissions")) {
                Map ret = (Map)qLib.PLAIN_GSON.fromJson(json.get("permissions").toString(), new TypeToken<Map<String, List<String>>>(){}.getType());
                ConcurrentHashMap<Rank, Map<String, Boolean>> newCache = new ConcurrentHashMap<Rank, Map<String, Boolean>>();
                for (Map.Entry entry : ret.entrySet()) {
                    newCache.put(Hydrogen.getInstance().getRankHandler().getRank((String)entry.getKey()).get(), PermissionUtils.convertFromList((List)entry.getValue()));
                }
                Hydrogen.getInstance().getPermissionHandler().setPermissionCache(newCache);
            }
        } else {
            Bukkit.getLogger().warning("Heartbeat - Could not POST server heartbeat: " + response.getErrorMessage());
        }
        this.first = false;
    }

    public static Queue<Map<String, Object>> getEventQueue() {
        return eventQueue;
    }
}

