/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 */
package net.frozenorb.hydrogen;

import net.frozenorb.hydrogen.Hydrogen;
import org.bukkit.Bukkit;

public final class Settings {
    private static String networkName;
    private static String networkWebsite;
    private static String apiHost;
    private static String apiKey;
    private static boolean forceStaffTotp;
    private static boolean bannedJoinsEnabled;
    private static boolean clean;

    public static String getApiKey() {
        return Hydrogen.getInstance().getServer().getServerName();
    }

    protected static void load() {
        Hydrogen.getInstance().saveDefaultConfig();
        networkName = Hydrogen.getInstance().getConfig().getString("text.network-name");
        networkWebsite = Hydrogen.getInstance().getConfig().getString("text.network-website");
        apiHost = Hydrogen.getInstance().getConfig().getString("APIHost");
        apiKey = Hydrogen.getInstance().getConfig().getString("APIKey");
        if (!Settings.getApiKey().equals(apiKey)) {
            Bukkit.getLogger().info("Hydrogen API key & server name mismatch! Using server name.");
        }
        forceStaffTotp = Hydrogen.getInstance().getConfig().getBoolean("2FARequired");
        bannedJoinsEnabled = Hydrogen.getInstance().getConfig().getBoolean("BannedJoinsEnabled");
    }

    public static void save() {
        Hydrogen.getInstance().getConfig().set("APIHost", (Object)apiHost);
        Hydrogen.getInstance().getConfig().set("APIKey", (Object)apiKey);
        Hydrogen.getInstance().getConfig().set("2FARequired", (Object)forceStaffTotp);
        Hydrogen.getInstance().getConfig().set("BannedJoinsEnabled", (Object)bannedJoinsEnabled);
        Hydrogen.getInstance().saveConfig();
    }

    private Settings() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static String getNetworkName() {
        return networkName;
    }

    public static void setNetworkName(String networkName) {
        Settings.networkName = networkName;
    }

    public static String getNetworkWebsite() {
        return networkWebsite;
    }

    public static void setNetworkWebsite(String networkWebsite) {
        Settings.networkWebsite = networkWebsite;
    }

    public static String getApiHost() {
        return apiHost;
    }

    public static void setApiHost(String apiHost) {
        Settings.apiHost = apiHost;
    }

    public static boolean isForceStaffTotp() {
        return forceStaffTotp;
    }

    public static void setForceStaffTotp(boolean forceStaffTotp) {
        Settings.forceStaffTotp = forceStaffTotp;
    }

    public static boolean isBannedJoinsEnabled() {
        return bannedJoinsEnabled;
    }

    public static void setBannedJoinsEnabled(boolean bannedJoinsEnabled) {
        Settings.bannedJoinsEnabled = bannedJoinsEnabled;
    }

    public static boolean isClean() {
        return clean;
    }

    public static void setClean(boolean clean) {
        Settings.clean = clean;
    }

    static {
        clean = false;
    }
}

