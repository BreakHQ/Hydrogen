/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Lists
 *  net.frozenorb.qlib.menu.Button
 *  net.frozenorb.qlib.util.TimeUtils
 *  net.frozenorb.qlib.util.UUIDUtils
 *  net.minecraft.util.org.apache.commons.lang3.StringUtils
 *  org.bukkit.ChatColor
 *  org.bukkit.DyeColor
 *  org.bukkit.Material
 *  org.bukkit.conversations.Conversable
 *  org.bukkit.conversations.Conversation
 *  org.bukkit.conversations.ConversationContext
 *  org.bukkit.conversations.ConversationFactory
 *  org.bukkit.conversations.ConversationPrefix
 *  org.bukkit.conversations.NullConversationPrefix
 *  org.bukkit.conversations.Prompt
 *  org.bukkit.conversations.StringPrompt
 *  org.bukkit.entity.Player
 *  org.bukkit.event.inventory.ClickType
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scheduler.BukkitRunnable
 */
package net.frozenorb.hydrogen.prefix.menu;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.frozenorb.hydrogen.Hydrogen;
import net.frozenorb.hydrogen.connection.RequestHandler;
import net.frozenorb.hydrogen.connection.RequestResponse;
import net.frozenorb.hydrogen.prefix.PrefixGrant;
import net.frozenorb.qlib.menu.Button;
import net.frozenorb.qlib.util.TimeUtils;
import net.frozenorb.qlib.util.UUIDUtils;
import net.minecraft.util.org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.conversations.Conversable;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.ConversationPrefix;
import org.bukkit.conversations.NullConversationPrefix;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class PrefixGrantButton
extends Button {
    private PrefixGrant prefixGrant;
    private String addedByResolved;

    public String getName(Player player) {
        return (Object)ChatColor.YELLOW + TimeUtils.formatIntoCalendarString((Date)new Date(this.prefixGrant.getAddedAt()));
    }

    public List<String> getDescription(Player player) {
        ArrayList description = Lists.newArrayList();
        description.add(ChatColor.GRAY.toString() + (Object)ChatColor.STRIKETHROUGH + StringUtils.repeat((char)'-', (int)25));
        int seconds = this.prefixGrant.getExpiresAt() > 0L ? TimeUtils.getSecondsBetween((Date)new Date(), (Date)new Date(this.prefixGrant.getExpiresAt())) : 0;
        String by = this.prefixGrant.getAddedBy() == null ? "Console" : this.addedByResolved;
        description.add((Object)ChatColor.YELLOW + "By: " + (Object)ChatColor.RED + by);
        description.add((Object)ChatColor.YELLOW + "Reason: " + (Object)ChatColor.RED + this.prefixGrant.getReason());
        description.add((Object)ChatColor.YELLOW + "Scopes: " + (Object)ChatColor.RED + (this.prefixGrant.getScopes().isEmpty() ? "Global" : this.prefixGrant.getScopes()));
        description.add((Object)ChatColor.YELLOW + "Prefix: " + (Object)ChatColor.RED + this.prefixGrant.getPrefix());
        if (this.prefixGrant.isActive()) {
            if (this.prefixGrant.getExpiresAt() != 0L) {
                description.add(ChatColor.GRAY.toString() + (Object)ChatColor.STRIKETHROUGH + StringUtils.repeat((char)'-', (int)25));
                description.add((Object)ChatColor.YELLOW + "Time remaining: " + (Object)ChatColor.RED + TimeUtils.formatIntoDetailedString((int)seconds));
            } else {
                description.add(ChatColor.GRAY.toString() + (Object)ChatColor.STRIKETHROUGH + StringUtils.repeat((char)'-', (int)25));
                description.add((Object)ChatColor.YELLOW + "This is a permanent prefix grant.");
            }
            if (player.hasPermission("minehq.prefixgrant.remove." + this.prefixGrant.getPrefix())) {
                description.add("");
                description.add(ChatColor.RED.toString() + (Object)ChatColor.BOLD + "Click to remove");
                description.add(ChatColor.RED.toString() + (Object)ChatColor.BOLD + "this prefix grant");
            }
        } else if (this.prefixGrant.isRemoved()) {
            String removedBy = this.prefixGrant.getRemovedBy() == null ? "Console" : UUIDUtils.name((UUID)this.prefixGrant.getRemovedBy());
            description.add(ChatColor.GRAY.toString() + (Object)ChatColor.STRIKETHROUGH + StringUtils.repeat((char)'-', (int)25));
            description.add((Object)ChatColor.RED + "Removed:");
            description.add((Object)ChatColor.YELLOW + removedBy + ": " + (Object)ChatColor.RED + this.prefixGrant.getRemovalReason());
            description.add((Object)ChatColor.RED + "at " + (Object)ChatColor.YELLOW + TimeUtils.formatIntoCalendarString((Date)new Date(this.prefixGrant.getRemovedAt())));
            if (this.prefixGrant.getExpiresAt() != 0L) {
                description.add("");
                description.add((Object)ChatColor.YELLOW + "Duration: " + TimeUtils.formatIntoDetailedString((int)((int)((this.prefixGrant.getExpiresAt() - this.prefixGrant.getAddedAt()) / 1000L) + 1)));
            }
        } else if (this.prefixGrant.isExpired()) {
            description.add((Object)ChatColor.YELLOW + "Duration: " + TimeUtils.formatIntoDetailedString((int)((int)((this.prefixGrant.getExpiresAt() - this.prefixGrant.getAddedAt()) / 1000L) + 1)));
            description.add((Object)ChatColor.GREEN + "Expired");
        }
        description.add(ChatColor.GRAY.toString() + (Object)ChatColor.STRIKETHROUGH + StringUtils.repeat((char)'-', (int)25));
        return description;
    }

    public Material getMaterial(Player player) {
        return Material.WOOL;
    }

    public byte getDamageValue(Player player) {
        return this.prefixGrant.isActive() ? DyeColor.LIME.getWoolData() : DyeColor.RED.getWoolData();
    }

    public void clicked(final Player player, int i, ClickType clickType) {
        if (!player.hasPermission("minehq.prefixGrant.remove." + this.prefixGrant.getPrefix()) || !this.prefixGrant.isActive()) {
            return;
        }
        player.closeInventory();
        ConversationFactory factory = new ConversationFactory((Plugin)Hydrogen.getInstance()).withModality(true).withPrefix((ConversationPrefix)new NullConversationPrefix()).withFirstPrompt((Prompt)new StringPrompt(){

            public String getPromptText(ConversationContext context) {
                return "\u00a7aType a reason to be used when removing this prefix grant. Type \u00a7cno\u00a7a to quit.";
            }

            public Prompt acceptInput(ConversationContext cc, final String s) {
                if (s.equalsIgnoreCase("no")) {
                    cc.getForWhom().sendRawMessage((Object)ChatColor.GREEN + "Prefix grant removal aborted.");
                } else {
                    new BukkitRunnable(){

                        public void run() {
                            RequestResponse response = RequestHandler.delete("/prefixes/" + PrefixGrantButton.this.prefixGrant.getId(), (Map<String, Object>)ImmutableMap.of((Object)"removedBy", (Object)player.getUniqueId(), (Object)"removedByIp", (Object)player.getAddress().getAddress().getHostAddress(), (Object)"reason", (Object)s));
                            if (response.wasSuccessful()) {
                                player.sendMessage((Object)ChatColor.GREEN + "Removed prefix grant successfully.");
                            } else {
                                player.sendMessage((Object)ChatColor.RED + response.getErrorMessage());
                            }
                        }
                    }.runTaskAsynchronously((Plugin)Hydrogen.getInstance());
                }
                return Prompt.END_OF_CONVERSATION;
            }
        }).withLocalEcho(false).withEscapeSequence("/no").withTimeout(60).thatExcludesNonPlayersWithMessage("Go away evil console!");
        Conversation con = factory.buildConversation((Conversable)player);
        player.beginConversation(con);
    }

    public PrefixGrantButton(PrefixGrant prefixGrant, String addedByResolved) {
        this.prefixGrant = prefixGrant;
        this.addedByResolved = addedByResolved;
    }
}

