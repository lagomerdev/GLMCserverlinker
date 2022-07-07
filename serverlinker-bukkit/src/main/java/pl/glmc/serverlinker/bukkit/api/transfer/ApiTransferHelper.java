package pl.glmc.serverlinker.bukkit.api.transfer;

import pl.glmc.serverlinker.api.bukkit.transfer.TransferHelper;
import pl.glmc.serverlinker.api.common.TransferLocation;
import pl.glmc.serverlinker.api.common.sector.SectorType;
import pl.glmc.serverlinker.bukkit.GlmcServerLinkerBukkit;
import pl.glmc.serverlinker.bukkit.api.transfer.listener.packet.ReverseTeleportPlayerHandler;
import pl.glmc.serverlinker.common.rtp.ReverseTeleportPlayerRequest;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class ApiTransferHelper implements TransferHelper {

    private final GlmcServerLinkerBukkit plugin;

    private final ReverseTeleportPlayerHandler reverseTeleportPlayerHandler;

    private final Set<UUID> queue = new HashSet<>();

    public ApiTransferHelper(GlmcServerLinkerBukkit plugin) {
        this.plugin = plugin;

        this.reverseTeleportPlayerHandler = new ReverseTeleportPlayerHandler();
        this.plugin.getGlmcApiBukkit().getPacketService().registerListener(reverseTeleportPlayerHandler, this.plugin);
    }


    @Override
    public CompletableFuture<Boolean> teleportPlayerToCoords(UUID playerUniqueId, String sectorType, TransferLocation transferLocation, boolean force) {
        Objects.requireNonNull(playerUniqueId);
        Objects.requireNonNull(sectorType);
        Objects.requireNonNull(transferLocation);

        if (queue.contains(playerUniqueId)) return CompletableFuture.completedFuture(false);
        queue.add(playerUniqueId);

        var result = new CompletableFuture<Boolean>();

        var request = new ReverseTeleportPlayerRequest(playerUniqueId, sectorType, transferLocation, force);
        this.reverseTeleportPlayerHandler.create(request.getUniqueId())
                .completeOnTimeout(false, 10, TimeUnit.SECONDS)
                .thenAccept(result::complete);

        result.thenAccept(success -> queue.remove(playerUniqueId));

        this.plugin.getGlmcApiBukkit().getPacketService().sendPacket(request, "proxy");

        return result;
    }
}
