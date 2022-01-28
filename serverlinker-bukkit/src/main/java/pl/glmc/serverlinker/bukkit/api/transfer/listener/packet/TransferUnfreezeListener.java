package pl.glmc.serverlinker.bukkit.api.transfer.listener.packet;

import pl.glmc.api.common.packet.listener.PacketListener;
import pl.glmc.serverlinker.bukkit.GlmcServerLinkerBukkit;
import pl.glmc.serverlinker.bukkit.api.transfer.ApiTransferService;
import pl.glmc.serverlinker.common.LocalPacketRegistry;
import pl.glmc.serverlinker.common.transfer.TransferUnfreeze;

public class TransferUnfreezeListener extends PacketListener<TransferUnfreeze> {
    private final GlmcServerLinkerBukkit plugin;
    private final ApiTransferService transferService;

    public TransferUnfreezeListener(GlmcServerLinkerBukkit plugin, ApiTransferService transferService) {
        super(LocalPacketRegistry.Transfer.TRANSFER_UNFREEZE, TransferUnfreeze.class);

        this.plugin = plugin;
        this.transferService = transferService;

        this.plugin.getGlmcApiBukkit().getPacketService().registerListener(this, this.plugin);
    }

    @Override
    public void received(TransferUnfreeze transferUnfreeze) {
        if (this.transferService.isFrozen(transferUnfreeze.getPlayerUniqueId())) {
            this.transferService.unfreeze(transferUnfreeze.getPlayerUniqueId());
        } else {
            this.plugin.getLogger().warning("Received unexpected TransferUnfreeze " + transferUnfreeze.getPlayerUniqueId() + "(possible timeout) ");
        }
    }
}
