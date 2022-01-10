/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.map.MapRenderer
 *  org.bukkit.map.MapView
 */
package net.frozenorb.hydrogen.totp;

import java.awt.image.BufferedImage;
import net.frozenorb.hydrogen.Hydrogen;
import net.frozenorb.hydrogen.totp.TotpMapRenderer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

public final class TotpMapCreator {
    public ItemStack createMap(Player player, BufferedImage bufferedImage) {
        MapView mapView = Hydrogen.getInstance().getServer().createMap(player.getWorld());
        mapView.getRenderers().forEach(((MapView)mapView)::removeRenderer);
        mapView.addRenderer((MapRenderer)new TotpMapRenderer(player.getUniqueId(), bufferedImage));
        player.sendMap(mapView);
        return new ItemStack(Material.MAP, 0, mapView.getId());
    }
}

