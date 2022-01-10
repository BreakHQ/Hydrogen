/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  net.frozenorb.qlib.qLib
 *  net.minecraft.util.com.google.common.reflect.TypeToken
 *  org.bukkit.Bukkit
 *  org.bukkit.plugin.Plugin
 */
package net.frozenorb.hydrogen.prefix;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.frozenorb.hydrogen.Hydrogen;
import net.frozenorb.hydrogen.connection.RequestHandler;
import net.frozenorb.hydrogen.connection.RequestResponse;
import net.frozenorb.hydrogen.prefix.Prefix;
import net.frozenorb.qlib.qLib;
import net.minecraft.util.com.google.common.reflect.TypeToken;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class PrefixHandler {
    private List<Prefix> prefixes = Lists.newArrayList();
    private Map<String, Prefix> prefixCache;

    public PrefixHandler() {
        this.refresh();
        Bukkit.getScheduler().scheduleAsyncRepeatingTask((Plugin)Hydrogen.getInstance(), this::refresh, 0L, 1200L);
    }

    public void refresh() {
        RequestResponse response = RequestHandler.get("/prefixes");
        if (response.wasSuccessful()) {
            this.prefixes = (List)qLib.PLAIN_GSON.fromJson(response.getResponse(), new TypeToken<List<Prefix>>(){}.getType());
            HashMap newPrefixCache = Maps.newHashMap();
            for (Prefix prefix : this.prefixes) {
                newPrefixCache.put(prefix.getId(), prefix);
            }
            this.prefixCache = newPrefixCache;
        } else {
            Bukkit.getLogger().warning("PrefixHandler - Could not retrieve prefixes from API: " + response.getErrorMessage());
        }
    }

    public Optional<Prefix> getPrefix(String parse) {
        return Optional.ofNullable(this.prefixCache.get(parse));
    }

    public List<Prefix> getPrefixes() {
        return new ArrayList<Prefix>(this.prefixes);
    }
}

