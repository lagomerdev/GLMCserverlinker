package pl.glmc.serverlinker.api.bukkit.rtp;

import pl.glmc.serverlinker.api.common.TransferLocation;

import java.util.concurrent.CompletableFuture;

public interface RandomTeleportService {

    /**
     *
     * @return
     */
    public CompletableFuture<TransferLocation> getRandomCoords();
}
