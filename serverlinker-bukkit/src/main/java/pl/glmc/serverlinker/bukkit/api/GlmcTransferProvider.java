package pl.glmc.serverlinker.bukkit.api;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import pl.glmc.serverlinker.api.bukkit.GlmcTransferBukkit;
import pl.glmc.serverlinker.api.bukkit.GlmcTransferBukkitProvider;
import pl.glmc.serverlinker.api.bukkit.sector.SectorManager;
import pl.glmc.serverlinker.api.bukkit.transfer.TransferService;
import pl.glmc.serverlinker.api.common.TransferAPI;
import pl.glmc.serverlinker.api.common.TransferMetaData;
import pl.glmc.serverlinker.bukkit.GlmcServerLinkerBukkit;
import pl.glmc.serverlinker.bukkit.api.sector.ApiSectorManager;
import pl.glmc.serverlinker.bukkit.api.transfer.ApiTransferService;
import pl.glmc.serverlinker.common.sector.SectorData;

public class GlmcTransferProvider implements GlmcTransferBukkit {

    private final GlmcServerLinkerBukkit plugin;

    private final ApiTransferService transferService;
    private final ApiSectorManager sectorManager;

    public GlmcTransferProvider(GlmcServerLinkerBukkit plugin) {
        this.plugin = plugin;

        this.sectorManager = new ApiSectorManager(this.plugin);
        this.transferService = new ApiTransferService(this.plugin);

        GlmcTransferBukkitProvider.register(this);
    }

    @Override
    public TransferService getTransferService() {
        return this.transferService;
    }

    @Override
    public SectorManager getSectorManager() {
        return this.sectorManager;
    }

    public SectorData getSectorData() {
        return this.sectorManager.getSectorData();
    }

}
