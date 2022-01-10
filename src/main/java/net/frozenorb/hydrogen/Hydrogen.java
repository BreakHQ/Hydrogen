/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.frozenorb.qlib.command.FrozenCommandHandler
 *  net.frozenorb.qlib.command.ParameterType
 *  net.frozenorb.qlib.nametag.FrozenNametagHandler
 *  net.frozenorb.qlib.nametag.NametagProvider
 *  net.frozenorb.qlib.util.ClassUtils
 *  okhttp3.OkHttpClient
 *  okhttp3.OkHttpClient$Builder
 *  org.bukkit.Bukkit
 *  org.bukkit.event.Listener
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.java.JavaPlugin
 */
package net.frozenorb.hydrogen;

import java.util.concurrent.TimeUnit;
import net.frozenorb.hydrogen.Settings;
import net.frozenorb.hydrogen.commands.punishment.parameter.PunishmentTarget;
import net.frozenorb.hydrogen.nametag.HNametagProvider;
import net.frozenorb.hydrogen.permission.PermissionHandler;
import net.frozenorb.hydrogen.prefix.PrefixHandler;
import net.frozenorb.hydrogen.profile.ProfileHandler;
import net.frozenorb.hydrogen.punishment.PunishmentHandler;
import net.frozenorb.hydrogen.rank.RankHandler;
import net.frozenorb.hydrogen.server.ServerHandler;
import net.frozenorb.qlib.command.FrozenCommandHandler;
import net.frozenorb.qlib.command.ParameterType;
import net.frozenorb.qlib.nametag.FrozenNametagHandler;
import net.frozenorb.qlib.nametag.NametagProvider;
import net.frozenorb.qlib.util.ClassUtils;
import okhttp3.OkHttpClient;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class Hydrogen
extends JavaPlugin {
    private static Hydrogen instance;
    private static final OkHttpClient okHttpClient;
    private RankHandler rankHandler;
    private ServerHandler serverHandler;
    private ProfileHandler profileHandler;
    private PermissionHandler permissionHandler;
    private PunishmentHandler punishmentHandler;
    private PrefixHandler prefixHandler;

    public void onEnable() {
        instance = this;
        Settings.load();
        this.setupHandlers();
        this.setupCommands();
        this.setupListeners();
        FrozenNametagHandler.registerProvider((NametagProvider)new HNametagProvider());
        this.getServer().getMessenger().registerOutgoingPluginChannel((Plugin)this, "MHQ-Queue");
    }

    public void onDisable() {
        instance = null;
    }

    private void setupHandlers() {
        this.rankHandler = new RankHandler();
        this.serverHandler = new ServerHandler();
        this.profileHandler = new ProfileHandler();
        this.permissionHandler = new PermissionHandler();
        this.punishmentHandler = new PunishmentHandler();
        this.prefixHandler = new PrefixHandler();
    }

    private void setupCommands() {
        FrozenCommandHandler.registerPackage((Plugin)this, (String)"net.frozenorb.hydrogen.commands");
        FrozenCommandHandler.registerParameterType(PunishmentTarget.class, (ParameterType)new PunishmentTarget.Type());
    }

    private void setupListeners() {
        ClassUtils.getClassesInPackage((Plugin)this, (String)"net.frozenorb.hydrogen.listener").stream().filter(Listener.class::isAssignableFrom).forEach(clazz -> {
            try {
                Bukkit.getPluginManager().registerEvents((Listener)clazz.newInstance(), (Plugin)this);
            }
            catch (IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
            }
        });
    }

    public static Hydrogen getInstance() {
        return instance;
    }

    public static OkHttpClient getOkHttpClient() {
        return okHttpClient;
    }

    public RankHandler getRankHandler() {
        return this.rankHandler;
    }

    public ServerHandler getServerHandler() {
        return this.serverHandler;
    }

    public ProfileHandler getProfileHandler() {
        return this.profileHandler;
    }

    public PermissionHandler getPermissionHandler() {
        return this.permissionHandler;
    }

    public PunishmentHandler getPunishmentHandler() {
        return this.punishmentHandler;
    }

    public PrefixHandler getPrefixHandler() {
        return this.prefixHandler;
    }

    static {
        okHttpClient = new OkHttpClient.Builder().connectTimeout(2L, TimeUnit.SECONDS).writeTimeout(2L, TimeUnit.SECONDS).readTimeout(2L, TimeUnit.SECONDS).build();
    }
}

