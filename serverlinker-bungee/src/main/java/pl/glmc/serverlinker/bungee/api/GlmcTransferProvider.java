package pl.glmc.serverlinker.bungee.api;

import pl.glmc.serverlinker.api.bungee.GlmcTransferBungee;
import pl.glmc.serverlinker.api.bungee.GlmcTransferBungeeProvider;
import pl.glmc.serverlinker.api.bungee.transfer.TransferService;
import pl.glmc.serverlinker.bungee.GlmcServerLinkerBungee;
import pl.glmc.serverlinker.bungee.api.transfer.ApiTransferService;

public class GlmcTransferProvider implements GlmcTransferBungee {

    private final GlmcServerLinkerBungee plugin;
    private final ApiTransferService transferService;

    public GlmcTransferProvider(GlmcServerLinkerBungee plugin) {
        this.plugin = plugin;

        this.transferService = new ApiTransferService(this.plugin);

        GlmcTransferBungeeProvider.register(this);
    }

    @Override
    public TransferService getTransferService() {
        return this.transferService;
    }
}
