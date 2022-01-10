/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 *  net.frozenorb.qlib.qLib
 *  net.minecraft.util.com.google.common.collect.ImmutableMap
 *  net.minecraft.util.com.google.common.reflect.TypeToken
 *  org.bukkit.Bukkit
 *  org.bukkit.craftbukkit.v1_7_R4.CraftServer
 *  org.bukkit.plugin.Plugin
 *  org.json.JSONObject
 */
package net.frozenorb.hydrogen.server;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import net.frozenorb.hydrogen.Hydrogen;
import net.frozenorb.hydrogen.connection.RequestHandler;
import net.frozenorb.hydrogen.connection.RequestResponse;
import net.frozenorb.hydrogen.server.ChatFilterEntry;
import net.frozenorb.hydrogen.server.Server;
import net.frozenorb.hydrogen.server.ServerGroup;
import net.frozenorb.hydrogen.server.heartbeat.HeartbeatTask;
import net.frozenorb.qlib.qLib;
import net.minecraft.util.com.google.common.collect.ImmutableMap;
import net.minecraft.util.com.google.common.reflect.TypeToken;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_7_R4.CraftServer;
import org.bukkit.plugin.Plugin;
import org.json.JSONObject;

public class ServerHandler {
    private Set<ChatFilterEntry> chatFilter;
    private Set<ServerGroup> serverGroups = Sets.newHashSet();
    private Set<Server> servers = Sets.newHashSet();

    public ServerHandler() {
        new HeartbeatTask().runTaskTimerAsynchronously((Plugin)Hydrogen.getInstance(), 5L, 600L);
        this.refresh();
        Bukkit.getScheduler().scheduleAsyncRepeatingTask((Plugin)Hydrogen.getInstance(), this::refresh, 0L, 6000L);
    }

    private void refresh() {
        RequestResponse response = RequestHandler.get("/serverGroups");
        if (response.wasSuccessful()) {
            this.serverGroups = (Set)qLib.PLAIN_GSON.fromJson(response.getResponse(), new TypeToken<Set<ServerGroup>>(){}.getType());
        } else {
            Bukkit.getLogger().warning("ServerHandler - Could not get server groups from API: " + response.getErrorMessage());
        }
        response = RequestHandler.get("/servers");
        if (response.wasSuccessful()) {
            this.servers = (Set)qLib.PLAIN_GSON.fromJson(response.getResponse(), new TypeToken<Set<Server>>(){}.getType());
        } else {
            Bukkit.getLogger().warning("ServerHandler - Could not get servers from API: " + response.getErrorMessage());
        }
        response = RequestHandler.get("/chatFilter");
        if (response.wasSuccessful()) {
            this.chatFilter = (Set)qLib.PLAIN_GSON.fromJson(response.getResponse(), new TypeToken<Set<ChatFilterEntry>>(){}.getType());
        } else {
            Bukkit.getLogger().warning("ServerHandler - Could not get chat filter list from API: " + response.getErrorMessage());
        }
        response = RequestHandler.get("/whoami");
        if (response.wasSuccessful()) {
            String id = response.asJSONObject().getString("name");
            this.getServer(id).ifPresent(server -> {
                try {
                    ((CraftServer)Bukkit.getServer()).setServerGroup(server.getServerGroup());
                }
                catch (NoSuchMethodError noSuchMethodError) {
                    // empty catch block
                }
            });
        } else {
            Bukkit.getLogger().warning("ServerHandler - Could not load our identity from API: " + response.getErrorMessage());
        }
    }

    public Optional<ServerGroup> getServerGroup(String parse) {
        for (ServerGroup group : this.serverGroups) {
            if (!group.getId().equalsIgnoreCase(parse)) continue;
            return Optional.of(group);
        }
        return Optional.empty();
    }

    public Optional<Server> getServer(String parse) {
        for (Server server : this.servers) {
            if (!server.getId().equalsIgnoreCase(parse) && !server.getDisplayName().equalsIgnoreCase(parse)) continue;
            return Optional.of(server);
        }
        return Optional.empty();
    }

    public Optional<Server> find(UUID player) {
        String server;
        RequestResponse response = RequestHandler.get("/users/" + player.toString());
        if (!response.wasSuccessful()) {
            return Optional.empty();
        }
        JSONObject json = response.asJSONObject();
        if (json.has("online") && !json.getBoolean("online")) {
            return Optional.empty();
        }
        String string = server = json.has("lastSeenOn") ? json.getString("lastSeenOn") : null;
        if (server == null) {
            return Optional.empty();
        }
        for (Server serverObject : this.servers) {
            if (!serverObject.getId().equalsIgnoreCase(server)) continue;
            return Optional.of(serverObject);
        }
        return Optional.empty();
    }

    public void leave(UUID player) {
        HeartbeatTask.getEventQueue().offer((Map<String, Object>)ImmutableMap.of((Object)"type", (Object)"leave", (Object)"user", (Object)player));
    }

    public List<Server> getServers() {
        return Lists.newArrayList(this.servers);
    }

    public Set<ChatFilterEntry> getChatFilter() {
        return this.chatFilter;
    }

    public Set<ServerGroup> getServerGroups() {
        return this.serverGroups;
    }
}

