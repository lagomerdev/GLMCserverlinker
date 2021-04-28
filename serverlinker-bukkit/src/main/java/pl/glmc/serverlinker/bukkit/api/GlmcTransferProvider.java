package pl.glmc.serverlinker.bukkit.api;

import pl.glmc.serverlinker.api.bukkit.GlmcTransferBukkit;
import pl.glmc.serverlinker.api.bukkit.GlmcTransferBukkitProvider;
import pl.glmc.serverlinker.api.bukkit.transfer.TransferService;
import pl.glmc.serverlinker.bukkit.GlmcServerLinkerBukkit;
import pl.glmc.serverlinker.bukkit.api.transfer.ApiTransferService;

public class GlmcTransferProvider implements GlmcTransferBukkit {

    private final GlmcServerLinkerBukkit plugin;
    private final ApiTransferService transferService;

    public GlmcTransferProvider(GlmcServerLinkerBukkit plugin) {
        this.plugin = plugin;

        this.transferService = new ApiTransferService(this.plugin);

        GlmcTransferBukkitProvider.register(this);
    }

    @Override
    public TransferService getTransferService() {
        return this.transferService;
    }
}
