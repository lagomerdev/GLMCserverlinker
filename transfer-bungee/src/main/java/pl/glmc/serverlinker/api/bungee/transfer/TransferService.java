package pl.glmc.serverlinker.api.bungee.transfer;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import pl.glmc.serverlinker.api.common.TransferAPI;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface TransferService {

    /**
     *
     * @param player
     * @return
     */
    CompletableFuture<TransferAPI.JoinResult> processJoin(ProxiedPlayer player);

    /**
     *
     * @param player
     */
    void processDisconnect(ProxiedPlayer player);

    /**
     *
     * @param playerUniqueId
     * @param serverTarget
     * @param force
     * @return
     */
    CompletableFuture<TransferAPI.TransferResult> transferPlayer(UUID playerUniqueId, String serverTarget, TransferAPI.TransferReason transferReason, boolean force);
}
