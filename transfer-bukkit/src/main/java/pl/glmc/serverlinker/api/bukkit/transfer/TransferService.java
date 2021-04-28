package pl.glmc.serverlinker.api.bukkit.transfer;

import org.bukkit.entity.Player;
import pl.glmc.serverlinker.api.common.TransferAPI;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface TransferService {

    /**
     *
     * @param playerUniqueId
     * @param serverTarget
     * @param force
     * @return
     */
    CompletableFuture<TransferAPI.TransferResult> transferPlayer(UUID playerUniqueId, String serverTarget, boolean force);
}
