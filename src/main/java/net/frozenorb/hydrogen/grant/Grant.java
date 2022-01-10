/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.frozenorb.qlib.util.UUIDUtils
 *  org.json.JSONObject
 */
package net.frozenorb.hydrogen.grant;

import java.util.Set;
import java.util.UUID;
import net.frozenorb.hydrogen.connection.RequestHandler;
import net.frozenorb.hydrogen.connection.RequestResponse;
import net.frozenorb.qlib.util.UUIDUtils;
import org.json.JSONObject;

public final class Grant {
    private String id;
    private UUID user;
    private String reason;
    private Set<String> scopes;
    private String rank;
    private long expiresAt;
    private UUID addedBy;
    private long addedAt;
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

    public Grant(String id, UUID user, String reason, Set<String> scopes, String rank, long expiresAt, UUID addedBy, long addedAt, UUID removedBy, long removedAt, String removalReason) {
        this.id = id;
        this.user = user;
        this.reason = reason;
        this.scopes = scopes;
        this.rank = rank;
        this.expiresAt = expiresAt;
        this.addedBy = addedBy;
        this.addedAt = addedAt;
        this.removedBy = removedBy;
        this.removedAt = removedAt;
        this.removalReason = removalReason;
    }

    public String getId() {
        return this.id;
    }

    public UUID getUser() {
        return this.user;
    }

    public String getReason() {
        return this.reason;
    }

    public Set<String> getScopes() {
        return this.scopes;
    }

    public String getRank() {
        return this.rank;
    }

    public long getExpiresAt() {
        return this.expiresAt;
    }

    public UUID getAddedBy() {
        return this.addedBy;
    }

    public long getAddedAt() {
        return this.addedAt;
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
}

