package pl.glmc.serverlinker.bungee.api.transfer.listener.packet;

import pl.glmc.api.common.packet.listener.PacketListener;
import pl.glmc.serverlinker.api.common.TransferMetaData;
import pl.glmc.serverlinker.bungee.GlmcServerLinkerBungee;
import pl.glmc.serverlinker.bungee.api.transfer.ApiTransferService;
import pl.glmc.serverlinker.common.LocalPacketRegistry;
import pl.glmc.serverlinker.common.transfer.TransferRequest;
import pl.glmc.serverlinker.common.transfer.TransferResponse;

public class TransferRequestListener extends PacketListener<TransferRequest> {
    private final GlmcServerLinkerBungee plugin;
    private final ApiTransferService transferService;

    public TransferRequestListener(GlmcServerLinkerBungee plugin, ApiTransferService transferService) {
        super(LocalPacketRegistry.Transfer.TRANSFER_REQUEST, TransferRequest.class);

        this.plugin = plugin;
        this.transferService = transferService;

        this.plugin.getGlmcApiBungee().getPacketService().registerListener(this, this.plugin);
    }

    @Override
    public void received(TransferRequest transferRequest) {
        this.transferService.transferPlayer(transferRequest.getPlayerUniqueId(), transferRequest.getServerTarget(),
                transferRequest.getTransferMetaData(), transferRequest.getTransferReason(), transferRequest.isForce())
                .thenAccept(transferResult -> {
                    boolean success = true;

                    TransferResponse transferResponse = new TransferResponse(success, transferRequest.getUniqueId(), transferResult);

                    this.plugin.getGlmcApiBungee().getPacketService().sendPacket(transferResponse, transferRequest.getSender());
                });
    }
}
