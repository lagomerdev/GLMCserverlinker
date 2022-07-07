package pl.glmc.serverlinker.api.bungee.transfer;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import pl.glmc.serverlinker.api.common.TransferLocation;
import pl.glmc.serverlinker.api.common.sector.SectorData;
import pl.glmc.serverlinker.api.common.sector.SectorType;

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
    public CompletableFuture<Boolean> teleportPlayerToCoords(UUID playerUniqueId, SectorType sectorType, TransferLocation transferLocation, boolean force);

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
    public CompletableFuture<Boolean> teleportPlayerToCoords(UUID playerUniqueId, SectorData sectorData, TransferLocation transferLocation, boolean force);

    /**
     *
     * @param player
     * @param playerTarget
     * @param force
     * @return
     */
    public CompletableFuture<Boolean> teleportPlayerToPlayer(ProxiedPlayer player, ProxiedPlayer playerTarget, boolean force);

}
