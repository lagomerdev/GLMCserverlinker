package pl.glmc.serverlinker.bukkit.api.rtp;

import org.bukkit.Material;
import pl.glmc.serverlinker.api.bukkit.rtp.RandomTeleportService;
import pl.glmc.serverlinker.api.common.TransferLocation;
import pl.glmc.serverlinker.bukkit.GlmcServerLinkerBukkit;
import pl.glmc.serverlinker.bukkit.api.rtp.packet.GetRandomCoordinatesListener;
import pl.glmc.serverlinker.bukkit.api.rtp.packet.TeleportPlayerListener;
import pl.glmc.serverlinker.bukkit.api.sector.ApiSectorManager;

import java.util.concurrent.CompletableFuture;

public class ApiRandomTeleportService implements RandomTeleportService {

    private final GlmcServerLinkerBukkit plugin;

    public ApiRandomTeleportService(GlmcServerLinkerBukkit plugin) {
        this.plugin = plugin;

        var getRandomCoordinatesListener = new GetRandomCoordinatesListener(this.plugin, this);
        var teleportPlayerListener = new TeleportPlayerListener(this.plugin);
    }

    public CompletableFuture<TransferLocation> getRandomCoords() {
        var result = new CompletableFuture<TransferLocation>();

        int x = (int) ApiSectorManager.sectorData.getMinX() + (int) (Math.random() * ((ApiSectorManager.sectorData.getMaxX()  - ApiSectorManager.sectorData.getMinX()) + 1));
        int z = (int) ApiSectorManager.sectorData.getMinZ()  + (int) (Math.random() * ((ApiSectorManager.sectorData.getMaxZ()  - ApiSectorManager.sectorData.getMinZ()) + 1));
        int chunkX = x >> 4, chunkZ = z >> 4;

        this.plugin.getGlmcTransferProvider().getSectorManager().getWorld().getChunkAtAsync(chunkX, chunkZ)
                .thenAcceptAsync(chunk -> {
                    var chunkSnapshot = chunk.getChunkSnapshot(true, true, false);
                    int blockX = x - chunkX * 16, blockZ = z - chunkZ * 16;

                    var y = chunkSnapshot.getHighestBlockYAt(blockX, blockZ);
                    var material = chunkSnapshot.getBlockType(blockX, y-1, blockZ);
                    var biome = chunkSnapshot.getBiome(blockX, y-1, blockZ);

                    System.out.println(material);
                    if (!biome.toString().contains("OCEAN") && material != Material.WATER && material != Material.LAVA) {
                        result.complete(new TransferLocation(x, y + 1, z));
                    } else {
                        result.complete(null);
                    }
                });

        return result;
    }
}
