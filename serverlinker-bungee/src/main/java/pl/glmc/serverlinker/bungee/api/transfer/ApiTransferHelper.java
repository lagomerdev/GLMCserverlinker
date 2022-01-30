package pl.glmc.serverlinker.bungee.api.transfer;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import pl.glmc.serverlinker.api.bungee.transfer.TransferHelper;
import pl.glmc.serverlinker.api.common.TransferAPI;
import pl.glmc.serverlinker.api.common.TransferLocation;
import pl.glmc.serverlinker.api.common.TransferMetaData;
import pl.glmc.serverlinker.api.common.TransferMetaKey;
import pl.glmc.serverlinker.bungee.GlmcServerLinkerBungee;
import pl.glmc.serverlinker.common.sector.SectorType;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class ApiTransferHelper implements TransferHelper {

    private final GlmcServerLinkerBungee plugin;

    public ApiTransferHelper(GlmcServerLinkerBungee plugin) {
        this.plugin = plugin;
    }

    @Override
    public CompletableFuture<TransferAPI.TransferResult> transferPlayerToCoords(UUID playerUniqueId, SectorType sectorType, double x, double y, double z , boolean force) {
        CompletableFuture<TransferAPI.TransferResult> result = new CompletableFuture<>();

        Objects.requireNonNull(playerUniqueId);

        var sectorData = this.plugin.getGlmcTransferProvider().getSectorManager().getSectorFromLocation(sectorType, x, z);
        if (sectorData.isEmpty()) {
            var response = new CompletableFuture<TransferAPI.TransferResult>();
            response.complete(TransferAPI.TransferResult.INVALID_TARGET_SERVER);

            return response;
        }

        TransferMetaData transferMetaData = new TransferMetaData();
        var transferLocationJson = this.plugin.getGson().toJson(new TransferLocation(x, y, z), TransferLocation.class);
        transferMetaData.setItem(TransferMetaKey.TELEPORT_ON_COORDS, transferLocationJson);

        return this.plugin.getGlmcTransferProvider().getTransferService().transferPlayer(playerUniqueId, sectorData.get().getId(), transferMetaData, TransferAPI.TransferReason.SERVER_MATCH, false);
    }
}
