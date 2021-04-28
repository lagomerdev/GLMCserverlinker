package pl.glmc.serverlinker.common.transfer;

import pl.glmc.api.common.packet.PacketInfo;
import pl.glmc.api.common.packet.ResponsePacket;
import pl.glmc.serverlinker.api.common.TransferAPI;
import pl.glmc.serverlinker.common.LocalPacketRegistry;

import java.util.UUID;

public class TransferApprovalResponse extends ResponsePacket {
    private static final PacketInfo PACKET_INFO = LocalPacketRegistry.Transfer.TRANSFER_APPROVAL_RESPONSE;

    private final TransferAPI.TransferApprovalResult transferApprovalResult;

    public TransferApprovalResponse(boolean success, UUID originUniqueId, TransferAPI.TransferApprovalResult transferApprovalResult) {
        super(success, originUniqueId);

        this.transferApprovalResult = transferApprovalResult;
    }

    public TransferAPI.TransferApprovalResult getTransferApprovalResult() {
        return transferApprovalResult;
    }

    @Override
    public String getPacketId() {
        return PACKET_INFO.getId();
    }
}
