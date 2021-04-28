package pl.glmc.serverlinker.common.transfer;

import pl.glmc.api.common.packet.PacketInfo;
import pl.glmc.api.common.packet.ResponsePacket;
import pl.glmc.serverlinker.api.common.TransferAPI;
import pl.glmc.serverlinker.common.LocalPacketRegistry;

import java.util.UUID;

public class TransferResponse extends ResponsePacket {
    private static final PacketInfo PACKET_INFO = LocalPacketRegistry.Transfer.TRANSFER_RESPONSE;

    private final TransferAPI.TransferResult transferResult;

    public TransferResponse(boolean success, UUID originUniqueId, TransferAPI.TransferResult transferResult) {
        super(success, originUniqueId);

        this.transferResult = transferResult;
    }

    public TransferAPI.TransferResult getTransferResult() {
        return transferResult;
    }

    @Override
    public String getPacketId() {
        return PACKET_INFO.getId();
    }
}
