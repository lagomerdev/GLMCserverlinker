package pl.glmc.serverlinker.api.bukkit.transfer;

import org.bukkit.entity.Player;
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
}
