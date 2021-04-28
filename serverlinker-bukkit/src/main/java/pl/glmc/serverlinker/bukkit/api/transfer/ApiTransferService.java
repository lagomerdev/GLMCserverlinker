package pl.glmc.serverlinker.bukkit.api.transfer;

import org.bukkit.entity.Player;
import pl.glmc.serverlinker.api.bukkit.transfer.TransferService;
import pl.glmc.serverlinker.api.common.TransferAPI;
import pl.glmc.serverlinker.bukkit.GlmcServerLinkerBukkit;
import pl.glmc.serverlinker.bukkit.api.transfer.listener.bukkit.PlayerFreezeListener;
import pl.glmc.serverlinker.bukkit.api.transfer.listener.bukkit.PlayerJoinQuitListener;
import pl.glmc.serverlinker.bukkit.api.transfer.listener.packet.TransferApprovalListener;
import pl.glmc.serverlinker.bukkit.api.transfer.listener.packet.TransferDataListener;
import pl.glmc.serverlinker.bukkit.api.transfer.listener.packet.TransferDataRequestListener;
import pl.glmc.serverlinker.bukkit.api.transfer.listener.packet.TransferUnfreezeListener;

import java.util.HashSet;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class ApiTransferService implements TransferService {
    private final String JOIN_QUIT_STATEMENT = "INSERT INTO `server_connection_logs`(`player_uuid`, `server`, `server_type`, `action`) VALUES (?,?,?,?)";

    private final GlmcServerLinkerBukkit plugin;

    private final HashSet<UUID> incomingTransfers, frozenPlayers;

    public ApiTransferService(GlmcServerLinkerBukkit plugin) {
        this.plugin = plugin;

        this.incomingTransfers = new HashSet<>();
        this.frozenPlayers = new HashSet<>();

        PlayerJoinQuitListener playerJoinGuard = new PlayerJoinQuitListener(this.plugin, this);
        PlayerFreezeListener playerFreezeListener = new PlayerFreezeListener(this.plugin, this);

        TransferDataListener transferDataListener = new TransferDataListener(this.plugin, this);
        TransferUnfreezeListener transferUnfreezeListener = new TransferUnfreezeListener(this.plugin, this);
        TransferDataRequestListener transferDataRequestListener = new TransferDataRequestListener(this.plugin, this);
        TransferApprovalListener transferApprovalRequest = new TransferApprovalListener(this.plugin, this);
    }

    public void processJoin(Player player) { //todo server name and type in main api
        this.plugin.getDatabaseProvider().updateAsync(JOIN_QUIT_STATEMENT, player.getUniqueId().toString(), this.plugin.getConfigData().getServerId(), "hub", 1);

    }

    public void processDisconnect(Player player) { //todo server name and type in main api
        this.plugin.getDatabaseProvider().updateAsync(JOIN_QUIT_STATEMENT, player.getUniqueId().toString(), this.plugin.getConfigData().getServerId(), "hub", 0);

    }

    public void freezePlayer(UUID playerUniqueId) {
        this.frozenPlayers.add(playerUniqueId);
    }

    public void unfreeze(UUID playerUniqueId) {
        this.frozenPlayers.remove(playerUniqueId);
    }

    public void incomingTransfer(UUID playerUniqueId) {
        this.incomingTransfers.add(playerUniqueId);

        this.plugin.getServer().getScheduler().runTaskLater(this.plugin, () -> {
            if (incomingTransfers.contains(playerUniqueId)) {
                this.incomingTransfers.remove(playerUniqueId);

                this.plugin.getLogger().warning("Transfer " + playerUniqueId + " was not completed (possible timeout)");
            }
        }, 20);
    }

    public void transferCompleted(UUID playerUniqueId) {
        this.incomingTransfers.remove(playerUniqueId);
    }

    public boolean isAllowed(UUID playerUniqueId) {
        return this.incomingTransfers.contains(playerUniqueId);
    }

    public boolean isFrozen(UUID playerUniqueId) {
        return this.frozenPlayers.contains(playerUniqueId);
    }

    @Override
    public CompletableFuture<TransferAPI.TransferResult> transferPlayer(UUID playerUniqueId, String serverTarget, boolean force) {
        Objects.requireNonNull(playerUniqueId);
        Objects.requireNonNull(serverTarget);

        return null;
    }
}
