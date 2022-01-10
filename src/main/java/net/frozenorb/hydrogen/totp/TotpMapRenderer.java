/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 *  org.bukkit.map.MapCanvas
 *  org.bukkit.map.MapRenderer
 *  org.bukkit.map.MapView
 */
package net.frozenorb.hydrogen.totp;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.UUID;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

final class TotpMapRenderer
extends MapRenderer {
    private final UUID targetPlayer;
    private BufferedImage image;

    public void render(MapView mapView, MapCanvas mapCanvas, Player player) {
        if (player.getUniqueId().equals(this.targetPlayer)) {
            mapCanvas.drawImage(0, 0, (Image)this.image);
            this.image = null;
        }
    }

    public TotpMapRenderer(UUID targetPlayer, BufferedImage image) {
        this.targetPlayer = targetPlayer;
        this.image = image;
    }
}

