/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  net.minecraft.util.com.google.common.escape.Escaper
 *  net.minecraft.util.com.google.common.net.UrlEscapers
 *  net.minecraft.util.org.apache.commons.codec.binary.Base32
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.conversations.ConversationContext
 *  org.bukkit.conversations.Prompt
 *  org.bukkit.conversations.StringPrompt
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.plugin.Plugin
 */
package net.frozenorb.hydrogen.totp.prompt;

import com.google.common.collect.ImmutableMap;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Map;
import javax.imageio.ImageIO;
import net.frozenorb.hydrogen.Hydrogen;
import net.frozenorb.hydrogen.Settings;
import net.frozenorb.hydrogen.connection.RequestHandler;
import net.frozenorb.hydrogen.connection.RequestResponse;
import net.frozenorb.hydrogen.totp.TotpMapCreator;
import net.minecraft.util.com.google.common.escape.Escaper;
import net.minecraft.util.com.google.common.net.UrlEscapers;
import net.minecraft.util.org.apache.commons.codec.binary.Base32;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

final class ScanMapPrompt
extends StringPrompt {
    private static final String totpImageUrlFormat = "https://www.google.com/chart?chs=130x130&chld=M%%7C0&cht=qr&chl=%s";
    private static final TotpMapCreator totpMapCreator = new TotpMapCreator();
    private static final Base32 base32Encoder = new Base32();
    private static SecureRandom secureRandom;
    int failures = 0;

    ScanMapPrompt() {
    }

    public String getPromptText(ConversationContext context) {
        Player player = (Player)context.getForWhom();
        if (this.failures == 0) {
            Bukkit.getScheduler().runTaskAsynchronously((Plugin)Hydrogen.getInstance(), () -> {
                String totpSecret = ScanMapPrompt.generateTotpSecret();
                BufferedImage totpImage = ScanMapPrompt.createTotpImage(player, totpSecret);
                Bukkit.getScheduler().runTask((Plugin)Hydrogen.getInstance(), () -> {
                    ItemStack map = totpMapCreator.createMap(player, totpImage);
                    context.setSessionData((Object)"totpSecret", (Object)totpSecret);
                    context.setSessionData((Object)"totpMap", (Object)map);
                    player.getInventory().addItem(new ItemStack[]{map});
                });
            });
        }
        return (Object)ChatColor.RED + "On your 2FA device, scan the map given to you. Once you've scanned the map, type the code displayed on your device in chat.";
    }

    public Prompt acceptInput(ConversationContext context, String s) {
        int totpCode;
        Player player = (Player)context.getForWhom();
        ItemStack totpMap = (ItemStack)context.getSessionData((Object)"totpMap");
        player.getInventory().remove(totpMap);
        String totpSecret = (String)context.getSessionData((Object)"totpSecret");
        try {
            totpCode = Integer.parseInt(s.replaceAll(" ", ""));
        }
        catch (NumberFormatException ex) {
            if (this.failures++ >= 3) {
                context.getForWhom().sendRawMessage((Object)ChatColor.RED + "Cancelling 2FA setup due to too many incorrect codes.");
                context.getForWhom().sendRawMessage((Object)ChatColor.RED + "Contact the " + Settings.getNetworkName() + " staff team for any questions you have about 2FA.");
                return Prompt.END_OF_CONVERSATION;
            }
            context.getForWhom().sendRawMessage("");
            context.getForWhom().sendRawMessage((Object)ChatColor.RED + s + " isn't a valid totp code. Let's try that again.");
            return this;
        }
        Bukkit.getScheduler().runTaskAsynchronously((Plugin)Hydrogen.getInstance(), () -> {
            RequestResponse response = RequestHandler.post("/users/" + player.getUniqueId() + "/setupTotp", (Map<String, Object>)ImmutableMap.of((Object)"userIp", (Object)player.getAddress().getAddress().getHostAddress(), (Object)"secret", (Object)totpSecret, (Object)"totpCode", (Object)totpCode));
            if (response.wasSuccessful()) {
                player.sendMessage((Object)ChatColor.GREEN + "2FA setup completed successfully.");
            } else {
                player.sendMessage((Object)ChatColor.RED + "Failed to setup 2FA. " + response.getErrorMessage());
            }
        });
        return Prompt.END_OF_CONVERSATION;
    }

    private static String generateTotpSecret() {
        byte[] secretKey = new byte[10];
        secureRandom.nextBytes(secretKey);
        return base32Encoder.encodeToString(secretKey);
    }

    private static BufferedImage createTotpImage(Player player, String totpSecret) {
        Escaper urlEscaper = UrlEscapers.urlFragmentEscaper();
        String totpUrl = "otpauth://totp/" + urlEscaper.escape(player.getName()) + "?secret=" + totpSecret + "&issuer=" + urlEscaper.escape(Settings.getNetworkName() + " Network");
        String totpImageUrl = String.format(totpImageUrlFormat, URLEncoder.encode(totpUrl));
        try {
            return ImageIO.read(new URL(totpImageUrl));
        }
        catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    static {
        try {
            secureRandom = SecureRandom.getInstance("SHA1PRNG", "SUN");
        }
        catch (GeneralSecurityException generalSecurityException) {
            // empty catch block
        }
    }
}

