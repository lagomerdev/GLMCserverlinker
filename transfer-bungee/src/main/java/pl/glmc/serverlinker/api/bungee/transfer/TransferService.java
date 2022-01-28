package pl.glmc.serverlinker.api.bungee.transfer;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import pl.glmc.serverlinker.api.common.TransferAPI;
import pl.glmc.serverlinker.api.common.TransferMetaData;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface TransferService {

    /**
     *
     * @param playerUniqueId
     * @param serverTarget
     * @param transferMetaData
     * @param transferReason
     * @param force
     * @return
     */
    CompletableFuture<TransferAPI.TransferResult> transferPlayer(UUID playerUniqueId, String serverTarget, TransferMetaData transferMetaData, TransferAPI.TransferReason transferReason, boolean force);

    /**
     *
     * @param player
     * @param serverTarget
     * @param playerData
     * @param transferMetaData
     * @param transferReason
     * @return
     */
    CompletableFuture<TransferAPI.TransferResult> transferPlayerWithData(ProxiedPlayer player, String serverTarget, String playerData, TransferMetaData transferMetaData, TransferAPI.TransferReason transferReason);

    /**
     *
     * @param player
     * @param serverTarget
     * @param transferMetaData
     * @param transferReason
     * @return
     */
    CompletableFuture<TransferAPI.TransferResult> transferPlayerWithoutData(ProxiedPlayer player, String serverTarget, TransferMetaData transferMetaData, TransferAPI.TransferReason transferReason);

}
