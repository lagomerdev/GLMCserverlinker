package pl.glmc.serverlinker.api.bungee.transfer;

import pl.glmc.serverlinker.api.common.TransferAPI;
import pl.glmc.serverlinker.common.sector.SectorType;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface TransferHelper {

    /**
     *
     * @param playerUniqueId
     * @param sectorType
     * @param x
     * @param y
     * @param z
     * @param force
     * @return
     */
    public CompletableFuture<TransferAPI.TransferResult> transferPlayerToCoords(UUID playerUniqueId, SectorType sectorType, double x, double y, double z , boolean force);
}
