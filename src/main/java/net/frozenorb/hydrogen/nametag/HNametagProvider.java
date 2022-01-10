/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.frozenorb.qlib.nametag.NametagInfo
 *  net.frozenorb.qlib.nametag.NametagProvider
 *  org.bukkit.entity.Player
 */
package net.frozenorb.hydrogen.nametag;

import java.util.Optional;
import net.frozenorb.hydrogen.Hydrogen;
import net.frozenorb.hydrogen.profile.Profile;
import net.frozenorb.hydrogen.rank.Rank;
import net.frozenorb.qlib.nametag.NametagInfo;
import net.frozenorb.qlib.nametag.NametagProvider;
import org.bukkit.entity.Player;

public class HNametagProvider
extends NametagProvider {
    public HNametagProvider() {
        super("Hydrogen Provider", 1);
    }

    public NametagInfo fetchNametag(Player player, Player watcher) {
        Optional<Profile> profileOptional = Hydrogen.getInstance().getProfileHandler().getProfile(player.getUniqueId());
        if (!profileOptional.isPresent()) {
            return HNametagProvider.createNametag((String)"", (String)"");
        }
        Profile profile = profileOptional.get();
        Rank bestDisplayRank = profile.getBestDisplayRank();
        if (bestDisplayRank == null) {
            return HNametagProvider.createNametag((String)"", (String)"");
        }
        return HNametagProvider.createNametag((String)bestDisplayRank.getGameColor(), (String)"");
    }
}

