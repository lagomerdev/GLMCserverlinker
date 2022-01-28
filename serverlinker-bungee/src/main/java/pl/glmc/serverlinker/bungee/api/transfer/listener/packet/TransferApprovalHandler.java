package pl.glmc.serverlinker.bungee.api.transfer.listener.packet;

import pl.glmc.api.common.packet.listener.ResponseHandlerListener;
import pl.glmc.serverlinker.api.common.TransferAPI;
import pl.glmc.serverlinker.common.LocalPacketRegistry;
import pl.glmc.serverlinker.common.transfer.TransferApprovalResponse;

public class TransferApprovalHandler extends ResponseHandlerListener<TransferApprovalResponse, TransferAPI.TransferApprovalResult> {

    public TransferApprovalHandler() {
        super(LocalPacketRegistry.Transfer.TRANSFER_APPROVAL_RESPONSE, TransferApprovalResponse.class);
    }

    @Override
    public void received(TransferApprovalResponse transferApprovalResponse) {
        this.complete(transferApprovalResponse.getOriginUniqueId(), transferApprovalResponse.getTransferApprovalResult());
    }
}
