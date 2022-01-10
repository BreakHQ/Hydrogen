/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.command.CommandSender
 */
package net.frozenorb.hydrogen.util.pagination;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public abstract class PaginatedResult<T> {
    private final int resultsPerPage;

    public PaginatedResult() {
        this(9);
    }

    public PaginatedResult(int resultsPerPage) {
        assert (resultsPerPage > 0);
        this.resultsPerPage = resultsPerPage;
    }

    public final void display(CommandSender sender, Collection<? extends T> results, int page) {
        this.display(sender, (List<? extends T>)new ArrayList<T>(results), page);
    }

    public final void display(CommandSender sender, List<? extends T> results, int page) {
        if (results.size() == 0) {
            sender.sendMessage((Object)ChatColor.RED + "No entries were found.");
            return;
        }
        int maxPages = results.size() / this.resultsPerPage + 1;
        if (page <= 0 || page > maxPages) {
            sender.sendMessage((Object)ChatColor.RED + "Page '" + page + "' not found. (" + (Object)ChatColor.YELLOW + "1 - " + maxPages + (Object)ChatColor.RED + ")");
            return;
        }
        sender.sendMessage(this.getHeader(page, maxPages));
        for (int i = this.resultsPerPage * (page - 1); i < this.resultsPerPage * page && i < results.size(); ++i) {
            sender.sendMessage(this.format(results.get(i), i));
        }
    }

    public abstract String getHeader(int var1, int var2);

    public abstract String format(T var1, int var2);
}

