/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.frozenorb.qlib.util.UUIDUtils
 *  net.minecraft.util.org.apache.commons.lang3.text.WordUtils
 *  org.json.JSONObject
 */
package net.frozenorb.hydrogen.punishment;

import java.util.UUID;
import net.frozenorb.hydrogen.connection.RequestHandler;
import net.frozenorb.hydrogen.connection.RequestResponse;
import net.frozenorb.qlib.util.UUIDUtils;
import net.minecraft.util.org.apache.commons.lang3.text.WordUtils;
import org.json.JSONObject;

public final class Punishment {
    private String id;
    private String publicReason;
    private String privateReason;
    private PunishmentType type;
    private UUID addedBy;
    private long addedAt;
    private long expiresAt;
    private String actorName;
    private String actorType;
    private UUID removedBy;
    private long removedAt;
    private String removalReason;

    public boolean isActive() {
        return !this.isExpired() && !this.isRemoved();
    }

    public boolean isExpired() {
        return this.expiresAt != 0L && System.currentTimeMillis() > this.expiresAt;
    }

    public boolean isRemoved() {
        return this.removedAt > 0L;
    }

    public String getActorType() {
        return WordUtils.capitalize((String)this.actorType.toLowerCase());
    }

    public String resolveAddedBy() {
        if (this.addedBy == null) {
            return null;
        }
        if (UUIDUtils.name((UUID)this.addedBy) != null) {
            return UUIDUtils.name((UUID)this.addedBy);
        }
        RequestResponse response = RequestHandler.get("/users/" + this.addedBy);
        if (!response.wasSuccessful()) {
            return "Internal error";
        }
        JSONObject json = response.asJSONObject();
        return json.getString("lastUsername");
    }

    public Punishment(String id, String publicReason, String privateReason, PunishmentType type, UUID addedBy, long addedAt, long expiresAt, String actorName, String actorType, UUID removedBy, long removedAt, String removalReason) {
        this.id = id;
        this.publicReason = publicReason;
        this.privateReason = privateReason;
        this.type = type;
        this.addedBy = addedBy;
        this.addedAt = addedAt;
        this.expiresAt = expiresAt;
        this.actorName = actorName;
        this.actorType = actorType;
        this.removedBy = removedBy;
        this.removedAt = removedAt;
        this.removalReason = removalReason;
    }

    public String getId() {
        return this.id;
    }

    public String getPublicReason() {
        return this.publicReason;
    }

    public String getPrivateReason() {
        return this.privateReason;
    }

    public PunishmentType getType() {
        return this.type;
    }

    public UUID getAddedBy() {
        return this.addedBy;
    }

    public long getAddedAt() {
        return this.addedAt;
    }

    public long getExpiresAt() {
        return this.expiresAt;
    }

    public String getActorName() {
        return this.actorName;
    }

    public UUID getRemovedBy() {
        return this.removedBy;
    }

    public long getRemovedAt() {
        return this.removedAt;
    }

    public String getRemovalReason() {
        return this.removalReason;
    }

    public static enum PunishmentType {
        BLACKLIST("Blacklist"),
        BAN("Ban"),
        MUTE("Mute"),
        WARN("Warning");

        private String name;

        private PunishmentType(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }
    }
}

