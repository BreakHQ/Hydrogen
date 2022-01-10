/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.frozenorb.qlib.command.Command
 *  org.bukkit.ChatColor
 *  org.bukkit.conversations.Conversable
 *  org.bukkit.conversations.Conversation
 *  org.bukkit.conversations.ConversationFactory
 *  org.bukkit.conversations.Prompt
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.Plugin
 */
package net.frozenorb.hydrogen.commands.auth;

import net.frozenorb.hydrogen.Hydrogen;
import net.frozenorb.hydrogen.totp.prompt.InitialDisclaimerPrompt;
import net.frozenorb.qlib.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.conversations.Conversable;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public final class Setup2faCommand {
    @Command(names={"setup2fa", "2fasetup"}, permission="minehq.totp.setup", description="Sign up to use 2FA to verify your identity")
    public static void setup2fa(Player sender) {
        if (Hydrogen.getInstance().getProfileHandler().getTotpEnabled().contains(sender.getUniqueId())) {
            sender.sendMessage((Object)ChatColor.RED + "You already have 2FA setup!");
            return;
        }
        ConversationFactory factory = new ConversationFactory((Plugin)Hydrogen.getInstance()).withFirstPrompt((Prompt)new InitialDisclaimerPrompt()).withLocalEcho(false).thatExcludesNonPlayersWithMessage("Go away evil console!");
        Conversation con = factory.buildConversation((Conversable)sender);
        sender.beginConversation(con);
    }
}

