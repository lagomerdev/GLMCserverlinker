package pl.glmc.serverlinker.bukkit.api.transfer.listener.packet;

import pl.glmc.api.common.packet.listener.PacketListener;
import pl.glmc.serverlinker.api.common.TransferAPI;
import pl.glmc.serverlinker.bukkit.GlmcServerLinkerBukkit;
import pl.glmc.serverlinker.bukkit.api.transfer.ApiTransferService;
import pl.glmc.serverlinker.common.LocalPacketRegistry;
import pl.glmc.serverlinker.common.transfer.TransferApprovalRequest;
import pl.glmc.serverlinker.common.transfer.TransferApprovalResponse;

public class TransferApprovalListener extends PacketListener<TransferApprovalRequest> {
    private final GlmcServerLinkerBukkit plugin;
    private final ApiTransferService transferService;

    public TransferApprovalListener(GlmcServerLinkerBukkit plugin, ApiTransferService transferService) {
        super(LocalPacketRegistry.Transfer.TRANSFER_APPROVAL_REQUEST, TransferApprovalRequest.class);

        this.plugin = plugin;
        this.transferService = transferService;

        this.plugin.getGlmcApiBukkit().getPacketService().registerListener(this, this.plugin);
    }

    @Override
    public void received(TransferApprovalRequest transferApprovalRequest) {
        TransferApprovalResponse transferApprovalResponse = new TransferApprovalResponse(true,
                transferApprovalRequest.getUniqueId(), TransferAPI.TransferApprovalResult.ACCEPTED);

        this.transferService.incomingTransfer(transferApprovalRequest.getPlayerUniqueId(), transferApprovalRequest.getTransferMetaData());

        this.plugin.getGlmcApiBukkit().getPacketService().sendPacket(transferApprovalResponse, transferApprovalRequest.getSender());
    }
}
