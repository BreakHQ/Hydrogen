/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.frozenorb.qlib.menu.Button
 *  net.frozenorb.qlib.util.TimeUtils
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
package net.frozenorb.hydrogen.commands.grant.menu;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.frozenorb.hydrogen.Hydrogen;
import net.frozenorb.hydrogen.commands.grant.menu.ScopesMenu;
import net.frozenorb.hydrogen.rank.Rank;
import net.frozenorb.qlib.menu.Button;
import net.frozenorb.qlib.util.TimeUtils;
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

public class RankButton
extends Button {
    private String targetName;
    private UUID targetUUID;
    private Rank rank;

    public String getName(Player player) {
        return this.rank.getFormattedName();
    }

    public List<String> getDescription(Player player) {
        ArrayList description = Lists.newArrayList();
        description.add(ChatColor.GRAY.toString() + (Object)ChatColor.STRIKETHROUGH + StringUtils.repeat((char)'-', (int)30));
        description.add((Object)ChatColor.BLUE + "Click to grant " + (Object)ChatColor.WHITE + this.targetName + (Object)ChatColor.BLUE + " the " + (Object)ChatColor.WHITE + this.rank.getFormattedName() + (Object)ChatColor.BLUE + " rank.");
        description.add(ChatColor.GRAY.toString() + (Object)ChatColor.STRIKETHROUGH + StringUtils.repeat((char)'-', (int)30));
        return description;
    }

    public Material getMaterial(Player player) {
        return Material.WOOL;
    }

    public byte getDamageValue(Player player) {
        return this.getColor().getWoolData();
    }

    public void clicked(final Player player, int i, ClickType clickType) {
        player.closeInventory();
        ConversationFactory factory = new ConversationFactory((Plugin)Hydrogen.getInstance()).withModality(true).withPrefix((ConversationPrefix)new NullConversationPrefix()).withFirstPrompt((Prompt)new StringPrompt(){

            public String getPromptText(ConversationContext context) {
                return (Object)ChatColor.YELLOW + "Please type a reason for this grant to be added, or type " + (Object)ChatColor.RED + "cancel" + (Object)ChatColor.YELLOW + " to cancel.";
            }

            public Prompt acceptInput(ConversationContext context, final String input) {
                if (input.equalsIgnoreCase("cancel")) {
                    context.getForWhom().sendRawMessage((Object)ChatColor.RED + "Granting cancelled.");
                    return Prompt.END_OF_CONVERSATION;
                }
                new BukkitRunnable(){

                    public void run() {
                        RankButton.this.promptTime(player, input);
                    }
                }.runTask((Plugin)Hydrogen.getInstance());
                return Prompt.END_OF_CONVERSATION;
            }
        }).withEscapeSequence("/no").withLocalEcho(false).withTimeout(10).thatExcludesNonPlayersWithMessage("Go away evil console!");
        Conversation con = factory.buildConversation((Conversable)player);
        player.beginConversation(con);
    }

    private void promptTime(final Player player, final String reason) {
        ConversationFactory factory = new ConversationFactory((Plugin)Hydrogen.getInstance()).withModality(true).withPrefix((ConversationPrefix)new NullConversationPrefix()).withFirstPrompt((Prompt)new StringPrompt(){

            public String getPromptText(ConversationContext context) {
                return (Object)ChatColor.YELLOW + "Please type a duration for this grant, (\"perm\" for permanent) or type " + (Object)ChatColor.RED + "cancel" + (Object)ChatColor.YELLOW + " to cancel.";
            }

            public Prompt acceptInput(ConversationContext context, String input) {
                if (input.equalsIgnoreCase("cancel")) {
                    context.getForWhom().sendRawMessage((Object)ChatColor.RED + "Granting cancelled.");
                    return Prompt.END_OF_CONVERSATION;
                }
                int duration = -1;
                if (!StringUtils.startsWithIgnoreCase((CharSequence)input, (CharSequence)"perm")) {
                    duration = TimeUtils.parseTime((String)input);
                }
                if (duration <= 1 && !StringUtils.startsWithIgnoreCase((CharSequence)input, (CharSequence)"perm")) {
                    context.getForWhom().sendRawMessage((Object)ChatColor.RED + "Invalid duration.");
                    return Prompt.END_OF_CONVERSATION;
                }
                final int finalDuration = duration;
                new BukkitRunnable(){

                    public void run() {
                        new ScopesMenu(false, false, RankButton.this.rank, RankButton.this.targetName, RankButton.this.targetUUID, reason, finalDuration).openMenu(player);
                    }
                }.runTask((Plugin)Hydrogen.getInstance());
                return Prompt.END_OF_CONVERSATION;
            }
        }).withEscapeSequence("/no").withLocalEcho(false).withTimeout(10).thatExcludesNonPlayersWithMessage("Go away evil console!");
        Conversation con = factory.buildConversation((Conversable)player);
        player.beginConversation(con);
    }

    private DyeColor getColor() {
        ChatColor color = ChatColor.getByChar((char)this.rank.getGameColor().charAt(1));
        switch (color) {
            case DARK_BLUE: {
                return DyeColor.BLUE;
            }
            case DARK_GREEN: {
                return DyeColor.GREEN;
            }
            case DARK_AQUA: 
            case AQUA: {
                return DyeColor.CYAN;
            }
            case DARK_RED: 
            case RED: {
                return DyeColor.RED;
            }
            case DARK_PURPLE: {
                return DyeColor.PURPLE;
            }
            case GOLD: {
                return DyeColor.ORANGE;
            }
            case GRAY: 
            case DARK_GRAY: {
                return DyeColor.GRAY;
            }
            case BLUE: {
                return DyeColor.BLUE;
            }
            case GREEN: {
                return DyeColor.LIME;
            }
            case LIGHT_PURPLE: {
                return DyeColor.PINK;
            }
            case YELLOW: {
                return DyeColor.YELLOW;
            }
            case WHITE: {
                return DyeColor.WHITE;
            }
        }
        return DyeColor.BLACK;
    }

    public RankButton(String targetName, UUID targetUUID, Rank rank) {
        this.targetName = targetName;
        this.targetUUID = targetUUID;
        this.rank = rank;
    }
}

