package pl.glmc.serverlinker.bungee.api.transfer;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import pl.glmc.serverlinker.api.bungee.transfer.TransferHelper;
import pl.glmc.serverlinker.api.common.TransferAPI;
import pl.glmc.serverlinker.api.common.TransferLocation;
import pl.glmc.serverlinker.api.common.TransferMetaData;
import pl.glmc.serverlinker.api.common.TransferMetaKey;
import pl.glmc.serverlinker.api.common.sector.SectorType;
import pl.glmc.serverlinker.bungee.GlmcServerLinkerBungee;
import pl.glmc.serverlinker.bungee.api.rtp.packet.TeleportPlayerHandler;
import pl.glmc.serverlinker.common.rtp.TeleportPlayerRequest;
import pl.glmc.serverlinker.api.common.sector.SectorData;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class ApiTransferHelper implements TransferHelper {

    private final GlmcServerLinkerBungee plugin;
    private final TeleportPlayerHandler teleportPlayerHandler;

    public ApiTransferHelper(GlmcServerLinkerBungee plugin) {
        this.plugin = plugin;

        this.teleportPlayerHandler = new TeleportPlayerHandler();
        this.plugin.getGlmcApiBungee().getPacketService().registerListener(teleportPlayerHandler, this.plugin);
    }

    @Override
    public CompletableFuture<Boolean> teleportPlayerToCoords(UUID playerUniqueId, SectorType sectorType, TransferLocation transferLocation, boolean force) {
        Objects.requireNonNull(playerUniqueId);
        Objects.requireNonNull(sectorType);
        Objects.requireNonNull(transferLocation);

        ProxiedPlayer player = this.plugin.getProxy().getPlayer(playerUniqueId);
        if (player == null || !player.isConnected()) {
            return CompletableFuture.completedFuture(false);
        }

        var sectorData = this.plugin.getGlmcTransferProvider().getSectorManager().getSectorFromLocation(sectorType, transferLocation.getX(), transferLocation.getZ());
        if (sectorData.isEmpty()) {
            return CompletableFuture.completedFuture(false);
        } else {
            return this.teleportPlayerToCoords(playerUniqueId, sectorData.get(), transferLocation, force);
        }
    }

    @Override
    public CompletableFuture<Boolean> teleportPlayerToCoords(UUID playerUniqueId, SectorData sectorData, TransferLocation transferLocation, boolean force) {
        Objects.requireNonNull(playerUniqueId);
        Objects.requireNonNull(sectorData);
        Objects.requireNonNull(transferLocation);

        ProxiedPlayer player = this.plugin.getProxy().getPlayer(playerUniqueId);
        if (player == null || !player.isConnected()) {
            return CompletableFuture.completedFuture(false);
        }

        var sectorId = sectorData.getId();

        if (player.getServer().getInfo().getName().equals(sectorId)) {
            var teleportPlayerRequest = new TeleportPlayerRequest(playerUniqueId, transferLocation);
            var result = this.teleportPlayerHandler.create(teleportPlayerRequest.getUniqueId());
            this.plugin.getGlmcApiBungee().getPacketService().sendPacket(teleportPlayerRequest, sectorId);

            return result;
        } else {
            TransferMetaData transferMetaData = new TransferMetaData();
            var transferLocationJson = this.plugin.getGson().toJson(transferLocation, TransferLocation.class);
            transferMetaData.setItem(TransferMetaKey.TELEPORT_ON_COORDS, transferLocationJson);

            var result = new CompletableFuture<Boolean>();
            this.plugin.getGlmcTransferProvider().getTransferService().transferPlayer(playerUniqueId, sectorId, transferMetaData, TransferAPI.TransferReason.SERVER_MATCH, false)
                    .thenAccept(transferResult -> result.complete(transferResult == TransferAPI.TransferResult.SUCCESS));

            return result;
        }
    }

    @Override
    public CompletableFuture<Boolean> teleportPlayerToPlayer(ProxiedPlayer player, ProxiedPlayer playerTarget, boolean force) {
        Objects.requireNonNull(player);
        Objects.requireNonNull(playerTarget);

        if (!player.isConnected() || !playerTarget.isConnected()) {
            return CompletableFuture.completedFuture(false);
        }

        var targetServer = playerTarget.getServer().getInfo().getName();
        if (!this.plugin.getGlmcTransferProvider().getSectorManager().isServerSector(targetServer)) {
            return CompletableFuture.completedFuture(false);
        }

        if (player.getServer().getInfo().getName().equals(targetServer)) {
            var teleportPlayerRequest = new TeleportPlayerRequest(player.getUniqueId(), playerTarget.getUniqueId());
            var result = this.teleportPlayerHandler.create(teleportPlayerRequest.getUniqueId());
            this.plugin.getGlmcApiBungee().getPacketService().sendPacket(teleportPlayerRequest, targetServer);

            return result;
        } else {
            TransferMetaData transferMetaData = new TransferMetaData();
            transferMetaData.setItem(TransferMetaKey.TELEPORT_TO_PLAYER, playerTarget.getUniqueId().toString());

            var result = new CompletableFuture<Boolean>();
            this.plugin.getGlmcTransferProvider().getTransferService().transferPlayer(player.getUniqueId(), targetServer, transferMetaData, TransferAPI.TransferReason.SERVER_MATCH, false)
                    .thenAccept(transferResult -> result.complete(transferResult == TransferAPI.TransferResult.SUCCESS));

            return result;
        }
    }
}
