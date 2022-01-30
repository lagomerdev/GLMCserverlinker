package pl.glmc.serverlinker.bungee.api.transfer;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectEvent;
import pl.glmc.serverlinker.api.bungee.transfer.TransferService;
import pl.glmc.serverlinker.api.common.TransferAPI;
import pl.glmc.serverlinker.api.common.TransferLocation;
import pl.glmc.serverlinker.api.common.TransferMetaData;
import pl.glmc.serverlinker.api.common.TransferMetaKey;
import pl.glmc.serverlinker.bungee.GlmcServerLinkerBungee;
import pl.glmc.serverlinker.bungee.api.transfer.listener.packet.TransferApprovalHandler;
import pl.glmc.serverlinker.bungee.api.transfer.listener.packet.TransferDataHandler;
import pl.glmc.serverlinker.bungee.api.transfer.listener.packet.TransferRequestListener;
import pl.glmc.serverlinker.common.TransferProperties;
import pl.glmc.serverlinker.common.sector.SectorType;
import pl.glmc.serverlinker.common.transfer.*;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class ApiTransferService implements TransferService {
    private final GlmcServerLinkerBungee plugin;

    private final TransferApprovalHandler transferApprovalHandler;
    private final TransferDataHandler transferDataHandler;

    private final HashMap<UUID, CompletableFuture<TransferAPI.TransferResult>> transferredPlayers = new HashMap<>();
    private final HashMap<UUID, Long> transferTimes = new HashMap<>();

    public ApiTransferService(GlmcServerLinkerBungee plugin) {
        this.plugin = plugin;

        this.transferApprovalHandler = new TransferApprovalHandler();
        this.transferDataHandler = new TransferDataHandler();

        this.plugin.getGlmcApiBungee().getPacketService().registerListener(this.transferApprovalHandler, this.plugin);
        this.plugin.getGlmcApiBungee().getPacketService().registerListener(this.transferDataHandler, this.plugin);

        TransferRequestListener transferRequestListener = new TransferRequestListener(this.plugin, this);
    }

    @Override
    public CompletableFuture<TransferAPI.TransferResult> transferPlayer(UUID playerUniqueId, String serverTarget, TransferMetaData transferMetaData, TransferAPI.TransferReason transferReason, boolean force) {
        Objects.requireNonNull(playerUniqueId);
        Objects.requireNonNull(serverTarget);
        Objects.requireNonNull(transferMetaData);
        Objects.requireNonNull(transferReason);

        CompletableFuture<TransferAPI.TransferResult> result = new CompletableFuture<>();

        ProxiedPlayer player = this.plugin.getProxy().getPlayer(playerUniqueId);

        if (validate(player, serverTarget, result)) {
            return result;
        }
        String serverOrigin = player.getServer().getInfo().getName();

        this.transferTimes.put(player.getUniqueId(), System.currentTimeMillis());
        this.transferredPlayers.put(player.getUniqueId(), result);

        TransferProperties transferProperties = new TransferProperties(serverOrigin, serverTarget, transferMetaData, transferReason, TransferAPI.TransferType.NORMAL, force);

        this.sendTransferApprovalRequest(player, transferProperties, null);

        return result;
    }

    @Override
    public CompletableFuture<TransferAPI.TransferResult> transferPlayerWithData(ProxiedPlayer player, String serverTarget, String playerData, TransferMetaData transferMetaData, TransferAPI.TransferReason transferReason) {
        Objects.requireNonNull(player);
        Objects.requireNonNull(serverTarget);
        Objects.requireNonNull(playerData);
        Objects.requireNonNull(transferMetaData);
        Objects.requireNonNull(transferReason);

        CompletableFuture<TransferAPI.TransferResult> result = new CompletableFuture<>();

        if (validate(player, serverTarget, result)) {
            return result;
        }

        this.transferTimes.put(player.getUniqueId(), System.currentTimeMillis());
        this.transferredPlayers.put(player.getUniqueId(), result);

        TransferProperties transferProperties = new TransferProperties(serverTarget, transferMetaData, transferReason, TransferAPI.TransferType.WITH_DATA);

        this.sendTransferApprovalRequest(player, transferProperties, playerData);

        return result;
    }

    @Override
    public CompletableFuture<TransferAPI.TransferResult> transferPlayerWithoutData(ProxiedPlayer player, String serverTarget, TransferMetaData transferMetaData, TransferAPI.TransferReason transferReason) {
        Objects.requireNonNull(player);
        Objects.requireNonNull(serverTarget);
        Objects.requireNonNull(transferMetaData);
        Objects.requireNonNull(transferReason);

        CompletableFuture<TransferAPI.TransferResult> result = new CompletableFuture<>();

        if (validate(player, serverTarget, result)) {
            return result;
        }

        this.transferTimes.put(player.getUniqueId(), System.currentTimeMillis());
        this.transferredPlayers.put(player.getUniqueId(), result);

        TransferProperties transferProperties = new TransferProperties(serverTarget, transferMetaData, transferReason, TransferAPI.TransferType.WITHOUT_DATA);

        this.sendTransferApprovalRequest(player, transferProperties, null);

        return result;
    }

    private boolean validate(ProxiedPlayer player, String serverTarget, CompletableFuture<TransferAPI.TransferResult> result) {
        if (player == null || !player.isConnected()) {
            result.complete(TransferAPI.TransferResult.PLAYER_OFFLINE);

            return true;
        }

        if (this.transferredPlayers.containsKey(player.getUniqueId())) {
            result.complete(TransferAPI.TransferResult.ALREADY_TRANSFERRING);

            return true;
        }

        ServerInfo serverTargetInfo = this.plugin.getProxy().getServerInfo(serverTarget);
        if (serverTargetInfo == null) {
            result.complete(TransferAPI.TransferResult.INVALID_TARGET_SERVER);

            return true;
        }

        return false;
    }

    private void sendTransferApprovalRequest(ProxiedPlayer player, TransferProperties transferProperties, String playerData) {
        TransferApprovalRequest transferApprovalRequest = new TransferApprovalRequest(player.getUniqueId(), transferProperties.getTransferMetaData(),
                transferProperties.getTransferReason(), transferProperties.getTransferType(), transferProperties.isForce());

        this.transferApprovalHandler.create(transferApprovalRequest.getUniqueId())
                .completeOnTimeout(TransferAPI.TransferApprovalResult.TIMEOUT, 500, TimeUnit.MILLISECONDS)
                .thenAccept(result -> {
                    if (result.isAccepted() || (transferProperties.isForce() && (result == TransferAPI.TransferApprovalResult.REJECTED
                            || result == TransferAPI.TransferApprovalResult.SERVER_LOCKED
                            || result == TransferAPI.TransferApprovalResult.SERVER_ORIGIN_FULL))) {
                        if (result.requiresPermission() && !player.hasPermission(result.getPermission())) {
                            this.complete(player.getUniqueId(), TransferAPI.TransferResult.FAILED_PERMISSIONS);
                        } else {
                            TransferAPI.TransferType transferType = transferProperties.getTransferType();

                            if (transferType == TransferAPI.TransferType.NORMAL) {
                                this.sendDataTransferRequest(player, transferProperties);
                            } else if (transferType == TransferAPI.TransferType.WITH_DATA) {
                                this.sendDataTransferData(player, playerData, transferProperties);
                            } else if (transferType == TransferAPI.TransferType.WITHOUT_DATA) {
                                this.connect(player, transferProperties);
                            }
                        }
                    } else {
                        this.complete(player.getUniqueId(), TransferAPI.TransferResult.FAILED_APPROVAL);
                    }
                });

        this.plugin.getGlmcApiBungee().getPacketService().sendPacket(transferApprovalRequest, transferProperties.getServerTarget());
    }

    private void sendDataTransferRequest(ProxiedPlayer player, TransferProperties transferProperties) {
        TransferDataRequest transferDataRequest = new TransferDataRequest(player.getUniqueId(), transferProperties.getServerTarget());

        this.transferDataHandler.create(transferDataRequest.getUniqueId())
                .completeOnTimeout(TransferAPI.TransferDataResult.TIMEOUT, 1, TimeUnit.SECONDS)
                .thenAccept(result -> {
                    if (result == TransferAPI.TransferDataResult.RECEIVED) {
                        this.connect(player, transferProperties);
                    } else {
                        TransferUnfreeze transferUnfreeze = new TransferUnfreeze(player.getUniqueId());

                        this.plugin.getGlmcApiBungee().getPacketService().sendPacket(transferUnfreeze, transferProperties.getServerOrigin());

                        this.complete(player.getUniqueId(), TransferAPI.TransferResult.FAILED_SYNC);
                    }
                });

        this.plugin.getGlmcApiBungee().getPacketService().sendPacket(transferDataRequest, transferProperties.getServerOrigin());
    }

    private void sendDataTransferData(ProxiedPlayer player, String playerData, TransferProperties transferProperties) {
        UUID transferUniqueId = UUID.randomUUID(); //transfer request unique id
        TransferData transferData = new TransferData(player.getUniqueId(), transferUniqueId, playerData, "", "proxy");

        this.transferDataHandler.create(transferUniqueId)
                .completeOnTimeout(TransferAPI.TransferDataResult.TIMEOUT, 3, TimeUnit.SECONDS)
                .thenAccept(dataResult -> {
                    if (dataResult == TransferAPI.TransferDataResult.RECEIVED) {
                        this.connect(player, transferProperties);
                    } else {
                        this.complete(player.getUniqueId(), TransferAPI.TransferResult.FAILED_SYNC);
                    }
                });

        this.plugin.getGlmcApiBungee().getPacketService().sendPacket(transferData, transferProperties.getServerTarget());
    }

    private void connect(ProxiedPlayer player, TransferProperties transferProperties) {
        if (!player.isConnected()) {
            this.complete(player.getUniqueId(), TransferAPI.TransferResult.PLAYER_OFFLINE);

            return;
        }

        ServerInfo serverTargetInfo = this.plugin.getProxy().getServerInfo(transferProperties.getServerTarget());
        player.connect(serverTargetInfo, (success, exception) -> {
            this.complete(player.getUniqueId(), success ? TransferAPI.TransferResult.SUCCESS : TransferAPI.TransferResult.FAILED_TRANSFERRING);
        }, false, ServerConnectEvent.Reason.PLUGIN, 1000);
    }

    private void complete(UUID playerUniqueId, TransferAPI.TransferResult transferResult) {
        if (transferResult == TransferAPI.TransferResult.SUCCESS) {
            this.plugin.getLogger().info(ChatColor.GREEN + "Successfully transferred player " + playerUniqueId + "!");
        } else {
            this.plugin.getLogger().info(ChatColor.RED + "Failed to transfer player " + playerUniqueId + "! (" + transferResult + ")");
        }

        long transferTime = System.currentTimeMillis() - this.transferTimes.remove(playerUniqueId);

        this.plugin.getLogger().info(ChatColor.YELLOW + "Transferring " + playerUniqueId + " took " + transferTime + "ms!");

        this.transferredPlayers.remove(playerUniqueId).complete(transferResult);
    }
}
