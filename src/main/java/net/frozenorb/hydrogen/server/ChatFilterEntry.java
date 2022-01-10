/*
 * Decompiled with CFR 0.150.
 */
package net.frozenorb.hydrogen.server;

public class ChatFilterEntry {
    private final String id;
    private final String regex;

    public ChatFilterEntry(String id, String regex) {
        this.id = id;
        this.regex = regex;
    }

    public String getId() {
        return this.id;
    }

    public String getRegex() {
        return this.regex;
    }
}

