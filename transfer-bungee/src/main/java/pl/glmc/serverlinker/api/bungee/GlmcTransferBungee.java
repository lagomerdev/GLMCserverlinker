package pl.glmc.serverlinker.api.bungee;

import pl.glmc.serverlinker.api.bungee.sector.SectorManager;
import pl.glmc.serverlinker.api.bungee.transfer.TransferService;

public interface GlmcTransferBungee {

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
}
