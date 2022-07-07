package pl.glmc.serverlinker.api.bukkit.transfer;

import pl.glmc.serverlinker.api.common.TransferLocation;
import pl.glmc.serverlinker.api.common.sector.SectorType;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface TransferHelper {

    /**
     *
     * @param playerUniqueId
     * @param sectorType
     * @param transferLocation
     * @param force
     * @return
     */
    public CompletableFuture<Boolean> teleportPlayerToCoords(UUID playerUniqueId, String sectorType, TransferLocation transferLocation, boolean force);
}
