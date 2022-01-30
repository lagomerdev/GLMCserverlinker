package pl.glmc.serverlinker.bukkit.api.transfer.listener.packet;

import pl.glmc.api.common.packet.listener.ResponseHandlerListener;
import pl.glmc.serverlinker.api.common.TransferAPI;
import pl.glmc.serverlinker.common.LocalPacketRegistry;
import pl.glmc.serverlinker.common.transfer.TransferResponse;

public class TransferHandler extends ResponseHandlerListener<TransferResponse, TransferAPI.TransferResult> {
    public TransferHandler() {
        super(LocalPacketRegistry.Transfer.TRANSFER_RESPONSE, TransferResponse.class);
    }

    @Override
    public void received(TransferResponse transferResponse) {
        this.complete(transferResponse.getOriginUniqueId(), transferResponse.getTransferResult());
    }
}
