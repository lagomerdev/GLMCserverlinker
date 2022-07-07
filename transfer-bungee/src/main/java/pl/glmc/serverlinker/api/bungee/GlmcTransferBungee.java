package pl.glmc.serverlinker.api.bungee;

import pl.glmc.serverlinker.api.bungee.player.PlayerService;
import pl.glmc.serverlinker.api.bungee.rtp.RandomTeleportService;
import pl.glmc.serverlinker.api.bungee.sector.SectorManager;
import pl.glmc.serverlinker.api.bungee.transfer.TransferHelper;
import pl.glmc.serverlinker.api.bungee.transfer.TransferService;

public interface GlmcTransferBungee {

    /**
     *
     * @return
     */
    TransferHelper getTransferHelper();

    /**
     *
     * @return
     */
    TransferService getTransferService();

    /**
     *
     * @return
     */
    SectorManager getSectorManager();

    /**
     *
     * @return
     */
    RandomTeleportService getRandomTeleportService();

    /**
     *
     * @return
     */
    public PlayerService getPlayerService();
}
