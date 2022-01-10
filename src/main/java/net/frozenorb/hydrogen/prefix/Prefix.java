/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.md_5.bungee.api.ChatColor
 */
package net.frozenorb.hydrogen.prefix;

import net.md_5.bungee.api.ChatColor;

public class Prefix {
    private String id;
    private String displayName;
    private String prefix;
    private boolean purchaseable;
    private String buttonName;
    private String buttonDescription;

    public String getFormattedPrefix() {
        return ChatColor.translateAlternateColorCodes((char)'&', (String)this.prefix);
    }

    public Prefix(String id, String displayName, String prefix, boolean purchaseable, String buttonName, String buttonDescription) {
        this.id = id;
        this.displayName = displayName;
        this.prefix = prefix;
        this.purchaseable = purchaseable;
        this.buttonName = buttonName;
        this.buttonDescription = buttonDescription;
    }

    public String getId() {
        return this.id;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public boolean isPurchaseable() {
        return this.purchaseable;
    }

    public String getButtonName() {
        return this.buttonName;
    }

    public String getButtonDescription() {
        return this.buttonDescription;
    }
}

