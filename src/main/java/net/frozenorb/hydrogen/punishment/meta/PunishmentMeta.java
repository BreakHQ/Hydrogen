/*
 * Decompiled with CFR 0.150.
 */
package net.frozenorb.hydrogen.punishment.meta;

import java.util.Map;

public class PunishmentMeta {
    private Map<String, Object> info;

    private PunishmentMeta(Map<String, Object> info) {
        this.info = info;
    }

    public static PunishmentMeta of(Map<String, Object> info) {
        return new PunishmentMeta(info);
    }

    public Map<String, Object> getInfo() {
        return this.info;
    }
}

