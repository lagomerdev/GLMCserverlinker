package pl.glmc.serverlinker.bungee.api;

import pl.glmc.serverlinker.api.bungee.GlmcTransferBungee;
import pl.glmc.serverlinker.api.bungee.GlmcTransferBungeeProvider;
import pl.glmc.serverlinker.api.bungee.player.PlayerService;
import pl.glmc.serverlinker.api.bungee.rtp.RandomTeleportService;
import pl.glmc.serverlinker.api.bungee.sector.SectorManager;
import pl.glmc.serverlinker.api.bungee.transfer.TransferHelper;
import pl.glmc.serverlinker.api.bungee.transfer.TransferService;
import pl.glmc.serverlinker.bungee.GlmcServerLinkerBungee;
import pl.glmc.serverlinker.bungee.api.player.ApiPlayerService;
import pl.glmc.serverlinker.bungee.api.rtp.ApiRandomTeleportService;
import pl.glmc.serverlinker.bungee.api.sector.ApiSectorManager;
import pl.glmc.serverlinker.bungee.api.player.PlayerManager;
import pl.glmc.serverlinker.bungee.api.transfer.ApiTransferHelper;
import pl.glmc.serverlinker.bungee.api.transfer.ApiTransferService;

public class GlmcTransferProvider implements GlmcTransferBungee {

    private final PlayerManager playerManager;

    private final ApiTransferService transferService;
    private final ApiTransferHelper transferHelper;
    private final ApiSectorManager sectorManager;
    private final ApiRandomTeleportService randomTeleportService;
    private final ApiPlayerService playerService;

    public GlmcTransferProvider(GlmcServerLinkerBungee plugin) {

        this.transferService = new ApiTransferService(plugin);
        this.transferHelper = new ApiTransferHelper(plugin);

        this.playerManager = new PlayerManager(plugin, this.transferService);

        this.sectorManager = new ApiSectorManager(plugin);
        this.randomTeleportService = new ApiRandomTeleportService(plugin);
        this.playerService = new ApiPlayerService(plugin);

        GlmcTransferBungeeProvider.register(this);
    }

    @Override
    public TransferHelper getTransferHelper() {
        return this.transferHelper;
    }
    @Override
    public TransferService getTransferService() {
        return this.transferService;
    }

    @Override
    public SectorManager getSectorManager() {
        return this.sectorManager;
    }

    @Override
    public RandomTeleportService getRandomTeleportService() {
        return this.randomTeleportService;
    }

    @Override
    public PlayerService getPlayerService() {
        return this.playerService;
    }
}
