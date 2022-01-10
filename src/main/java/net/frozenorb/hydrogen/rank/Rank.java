/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.common.primitives.Ints
 *  net.frozenorb.qlib.qLib
 *  org.bukkit.ChatColor
 */
package net.frozenorb.hydrogen.rank;

import com.google.common.primitives.Ints;
import java.util.Comparator;
import net.frozenorb.hydrogen.Settings;
import net.frozenorb.qlib.qLib;
import org.bukkit.ChatColor;

public final class Rank {
    private String id;
    private String inheritsFromId;
    private int generalWeight;
    private int displayWeight;
    private String displayName;
    private String gamePrefix;
    private String gameColor;
    private boolean staffRank;
    private boolean grantRequiresTotp;
    private String queueMessage;
    public static final Comparator<Rank> GENERAL_WEIGHT_COMPARATOR = (a, b) -> Ints.compare((int)b.getGeneralWeight(), (int)a.getGeneralWeight());
    public static final Comparator<Rank> DISPLAY_WEIGHT_COMPARATOR = (a, b) -> Ints.compare((int)b.getDisplayWeight(), (int)a.getDisplayWeight());

    public String getFormattedName() {
        return this.getGameColor() + this.getDisplayName();
    }

    public String toString() {
        return qLib.GSON.toJson((Object)this);
    }

    public String getGameColor() {
        if (Settings.isClean()) {
            return ChatColor.translateAlternateColorCodes((char)'&', (String)this.gameColor.replace("&l", ""));
        }
        return ChatColor.translateAlternateColorCodes((char)'&', (String)this.gameColor);
    }

    public String getGamePrefix() {
        if (Settings.isClean()) {
            return ChatColor.translateAlternateColorCodes((char)'&', (String)this.gamePrefix.replace("&l", ""));
        }
        return ChatColor.translateAlternateColorCodes((char)'&', (String)this.gamePrefix);
    }

    public boolean equals(Object obj) {
        return obj instanceof Rank && ((Rank)obj).getId().equals(this.id);
    }

    public int hashCode() {
        return this.id.hashCode();
    }

    public Rank(String id, String inheritsFromId, int generalWeight, int displayWeight, String displayName, String gamePrefix, String gameColor, boolean staffRank, boolean grantRequiresTotp, String queueMessage) {
        this.id = id;
        this.inheritsFromId = inheritsFromId;
        this.generalWeight = generalWeight;
        this.displayWeight = displayWeight;
        this.displayName = displayName;
        this.gamePrefix = gamePrefix;
        this.gameColor = gameColor;
        this.staffRank = staffRank;
        this.grantRequiresTotp = grantRequiresTotp;
        this.queueMessage = queueMessage;
    }

    public String getId() {
        return this.id;
    }

    public String getInheritsFromId() {
        return this.inheritsFromId;
    }

    public int getGeneralWeight() {
        return this.generalWeight;
    }

    public int getDisplayWeight() {
        return this.displayWeight;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public boolean isStaffRank() {
        return this.staffRank;
    }

    public boolean isGrantRequiresTotp() {
        return this.grantRequiresTotp;
    }

    public String getQueueMessage() {
        return this.queueMessage;
    }
}

