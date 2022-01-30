package pl.glmc.serverlinker.api.bungee.rtp;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import pl.glmc.serverlinker.api.common.TransferAPI;
import pl.glmc.serverlinker.api.common.TransferLocation;
import pl.glmc.serverlinker.common.sector.SectorData;

import java.util.UUID;
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
    public CompletableFuture<TransferAPI.TransferResult> transferPlayerToRandomCoords(ProxiedPlayer player, String serverTarget, boolean force);
}
