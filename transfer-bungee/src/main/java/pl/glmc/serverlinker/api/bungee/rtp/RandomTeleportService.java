package pl.glmc.serverlinker.api.bungee.rtp;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import pl.glmc.serverlinker.api.common.TransferLocation;

import java.util.concurrent.CompletableFuture;

public interface RandomTeleportService {

    /**
     *
     * @param serverTarget
     * @return
     */
    public CompletableFuture<TransferLocation> getRandomCoords(String serverTarget);

    /**
     *
     * @param player
     * @param serverTarget
     * @param force
     * @return
     */
    public CompletableFuture<Boolean> teleportPlayerToRandomCoords(ProxiedPlayer player, String serverTarget, boolean force);
}
