/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.frozenorb.qlib.qLib
 *  net.minecraft.util.com.google.common.reflect.TypeToken
 *  org.bukkit.Bukkit
 *  org.bukkit.plugin.Plugin
 */
package net.frozenorb.hydrogen.rank;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.frozenorb.hydrogen.Hydrogen;
import net.frozenorb.hydrogen.connection.RequestHandler;
import net.frozenorb.hydrogen.connection.RequestResponse;
import net.frozenorb.hydrogen.rank.Rank;
import net.frozenorb.qlib.qLib;
import net.minecraft.util.com.google.common.reflect.TypeToken;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class RankHandler {
    private List<Rank> ranks = Lists.newArrayList();

    public RankHandler() {
        this.refresh();
        Bukkit.getScheduler().scheduleAsyncRepeatingTask((Plugin)Hydrogen.getInstance(), this::refresh, 0L, 6000L);
    }

    public void refresh() {
        RequestResponse response = RequestHandler.get("/ranks");
        if (response.wasSuccessful()) {
            this.ranks = (List)qLib.PLAIN_GSON.fromJson(response.getResponse(), new TypeToken<List<Rank>>(){}.getType());
            this.ranks.sort(Rank.DISPLAY_WEIGHT_COMPARATOR);
        } else {
            Bukkit.getLogger().warning("RankHandler - Could not retrieve ranks from API: " + response.getErrorMessage());
        }
    }

    public Optional<Rank> getRank(String parse) {
        for (Rank rank : this.ranks) {
            if (rank.getId().equalsIgnoreCase(parse)) {
                return Optional.of(rank);
            }
            if (!rank.getDisplayName().equalsIgnoreCase(parse)) continue;
            return Optional.of(rank);
        }
        return Optional.empty();
    }

    public List<Rank> getRanks() {
        return new ArrayList<Rank>(this.ranks);
    }
}

