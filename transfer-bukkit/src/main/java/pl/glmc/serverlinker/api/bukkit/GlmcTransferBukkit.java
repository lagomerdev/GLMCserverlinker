package pl.glmc.serverlinker.api.bukkit;

import pl.glmc.serverlinker.api.bukkit.rtp.RandomTeleportService;
import pl.glmc.serverlinker.api.bukkit.sector.SectorManager;
import pl.glmc.serverlinker.api.bukkit.transfer.TransferService;

public interface GlmcTransferBukkit {

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
}
