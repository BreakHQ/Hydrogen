/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  mkremins.fanciful.FancyMessage
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.player.AsyncPlayerChatEvent
 *  org.bukkit.event.player.PlayerQuitEvent
 */
package net.frozenorb.hydrogen.listener;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import mkremins.fanciful.FancyMessage;
import net.frozenorb.hydrogen.Hydrogen;
import net.frozenorb.hydrogen.Settings;
import net.frozenorb.hydrogen.profile.Profile;
import net.frozenorb.hydrogen.server.ChatFilterEntry;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ChatFilterListener
implements Listener {
    private static final FancyMessage DELAY_MESSAGE = new FancyMessage("Purchase a rank at ").color(ChatColor.RED).then("store." + Settings.getNetworkWebsite()).link("http://store." + Settings.getNetworkWebsite()).tooltip((Object)ChatColor.GREEN + "Click to go to our Store!").color(ChatColor.YELLOW).style(new ChatColor[]{ChatColor.UNDERLINE}).then(" to bypass this restriction.").color(ChatColor.RED);
    private static final int DEFAULT_CHAT_DELAY = 3;
    private Map<UUID, Long> lastChatted = Maps.newConcurrentMap();
    private Map<UUID, String> badMessages = Maps.newConcurrentMap();

    @EventHandler(priority=EventPriority.LOWEST)
    public void onPlayerChatLowest(AsyncPlayerChatEvent event) {
        Set<ChatFilterEntry> chatFilter = Hydrogen.getInstance().getServerHandler().getChatFilter();
        for (ChatFilterEntry chatFilterEntry : chatFilter) {
            if (!Pattern.matches(chatFilterEntry.getRegex(), event.getMessage())) continue;
            this.badMessages.put(event.getPlayer().getUniqueId(), event.getMessage());
            break;
        }
    }

    @EventHandler(priority=EventPriority.HIGH)
    public void onPlayerChatHigh(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (player.hasMetadata("NoSpamCheck")) {
            return;
        }
        Profile profile = Hydrogen.getInstance().getProfileHandler().getProfile(player.getUniqueId()).orElse(null);
        if (profile == null || profile.getBestGeneralRank().getId().equalsIgnoreCase("default")) {
            long delta;
            if (this.lastChatted.containsKey(player.getUniqueId()) && (delta = System.currentTimeMillis() - this.lastChatted.get(event.getPlayer().getUniqueId())) < TimeUnit.SECONDS.toMillis(3L)) {
                event.setCancelled(true);
                player.sendMessage((Object)ChatColor.RED + "Default players can only talk in chat every " + (Object)ChatColor.YELLOW + 3 + " seconds" + (Object)ChatColor.RED + "!");
                DELAY_MESSAGE.send(player);
                return;
            }
            this.lastChatted.put(event.getPlayer().getUniqueId(), System.currentTimeMillis());
        } else if (profile.getBestGeneralRank().getId().equals("registered")) {
            long delta;
            if (this.lastChatted.containsKey(player.getUniqueId()) && (delta = System.currentTimeMillis() - this.lastChatted.get(event.getPlayer().getUniqueId())) < TimeUnit.SECONDS.toMillis(5L)) {
                event.setCancelled(true);
                player.sendMessage((Object)ChatColor.RED + "Registered players can only talk in chat every " + (Object)ChatColor.YELLOW + 5 + " seconds" + (Object)ChatColor.RED + "!");
                DELAY_MESSAGE.send(player);
                return;
            }
            this.lastChatted.put(event.getPlayer().getUniqueId(), System.currentTimeMillis());
        }
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onPlayerChatMonitor(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (this.badMessages.containsKey(player.getUniqueId())) {
            if (event.getPlayer().hasMetadata("NoSpamCheck")) {
                return;
            }
            event.getRecipients().clear();
            event.getRecipients().add(event.getPlayer());
            String message = this.badMessages.get(player.getUniqueId());
            FancyMessage toSend = new FancyMessage(" \u2717 ").color(ChatColor.DARK_RED).style(new ChatColor[]{ChatColor.BOLD}).tooltip((Object)ChatColor.YELLOW + "This message was hidden from public chat.").then(String.format(event.getFormat(), event.getPlayer().getDisplayName(), "")).then(message).color(ChatColor.LIGHT_PURPLE);
            for (Player other : Bukkit.getOnlinePlayers()) {
                if (!other.hasPermission("basic.staff")) continue;
                toSend.send(other);
            }
            this.badMessages.remove(player.getUniqueId());
        }
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event) {
        this.lastChatted.remove(event.getPlayer().getUniqueId());
        this.badMessages.remove(event.getPlayer().getUniqueId());
    }
}

