/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.conversations.ConversationContext
 *  org.bukkit.conversations.Prompt
 *  org.bukkit.conversations.StringPrompt
 */
package net.frozenorb.hydrogen.totp.prompt;

import net.frozenorb.hydrogen.Settings;
import net.frozenorb.hydrogen.totp.prompt.ScanMapPrompt;
import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;

public final class InitialDisclaimerPrompt
extends StringPrompt {
    public String getPromptText(ConversationContext context) {
        return ChatColor.RED.toString() + (Object)ChatColor.BOLD + "Take a minute to read over this, it's important. " + (Object)ChatColor.RED + "2FA can be enabled to protect against hackers getting into your Minecraft account. If you enable 2FA, you'll be required to enter a code every time you log in. If you lose your 2FA device, you won't be able to log in to " + Settings.getNetworkName() + ". " + (Object)ChatColor.GRAY + "If you've read the above and would like to proceed, type \"yes\" in chat. Otherwise, type anything else.";
    }

    public Prompt acceptInput(ConversationContext context, String s) {
        if (s.equalsIgnoreCase("yes")) {
            return new ScanMapPrompt();
        }
        context.getForWhom().sendRawMessage((Object)ChatColor.GREEN + "Aborted 2FA setup.");
        return Prompt.END_OF_CONVERSATION;
    }
}

