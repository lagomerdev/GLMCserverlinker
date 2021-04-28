package pl.glmc.serverlinker.bungee.api.transfer.listener.packet;

import pl.glmc.api.common.packet.listener.ResponseHandlerListener;
import pl.glmc.serverlinker.api.common.TransferAPI;
import pl.glmc.serverlinker.common.LocalPacketRegistry;
import pl.glmc.serverlinker.common.transfer.TransferDataResponse;

public class TransferDataHandler extends ResponseHandlerListener<TransferDataResponse, TransferAPI.TransferDataResult> {
    public TransferDataHandler() {
        super(LocalPacketRegistry.Transfer.TRANSFER_DATA_RESPONSE, TransferDataResponse.class);
    }

    @Override
    public void received(TransferDataResponse transferDataResponse) {
        this.complete(transferDataResponse.getOriginUniqueId(), transferDataResponse.getTransferDataResult());
    }
}
