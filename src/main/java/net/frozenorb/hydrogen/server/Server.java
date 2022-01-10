/*
 * Decompiled with CFR 0.150.
 */
package net.frozenorb.hydrogen.server;

import java.util.Optional;
import net.frozenorb.hydrogen.Hydrogen;
import net.frozenorb.hydrogen.server.ServerGroup;

public class Server {
    private String id;
    private String displayName;
    private String serverGroup;
    private String serverIp;
    private long lastUpdatedAt;
    private double lastTps;

    public ServerGroup resolveGroup() {
        Optional<ServerGroup> groupOptional = Hydrogen.getInstance().getServerHandler().getServerGroup(this.serverGroup);
        return groupOptional.orElse(null);
    }

    public String getId() {
        return this.id;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public String getServerGroup() {
        return this.serverGroup;
    }

    public String getServerIp() {
        return this.serverIp;
    }

    public long getLastUpdatedAt() {
        return this.lastUpdatedAt;
    }

    public double getLastTps() {
        return this.lastTps;
    }
}

