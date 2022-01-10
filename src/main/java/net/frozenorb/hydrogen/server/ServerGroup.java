/*
 * Decompiled with CFR 0.150.
 */
package net.frozenorb.hydrogen.server;

import java.util.List;

public class ServerGroup {
    private final String id;
    private String image;
    private List<String> announcements;

    public ServerGroup(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public String getImage() {
        return this.image;
    }

    public List<String> getAnnouncements() {
        return this.announcements;
    }
}

