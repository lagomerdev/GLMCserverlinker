package pl.glmc.serverlinker.common.transfer;

import pl.glmc.api.common.packet.PacketInfo;
import pl.glmc.api.common.packet.ResponsePacket;
import pl.glmc.serverlinker.api.common.TransferAPI;
import pl.glmc.serverlinker.common.LocalPacketRegistry;

import java.util.UUID;

public class TransferDataResponse extends ResponsePacket {
    private static final PacketInfo PACKET_INFO = LocalPacketRegistry.Transfer.TRANSFER_DATA_RESPONSE;

    private final TransferAPI.TransferDataResult transferDataResult;

    public TransferDataResponse(boolean success, UUID originUniqueId, TransferAPI.TransferDataResult transferDataResult) {
        super(success, originUniqueId);

        this.transferDataResult = transferDataResult;
    }

    public TransferAPI.TransferDataResult getTransferDataResult() {
        return transferDataResult;
    }

    @Override
    public String getPacketId() {
        return PACKET_INFO.getId();
    }
}
