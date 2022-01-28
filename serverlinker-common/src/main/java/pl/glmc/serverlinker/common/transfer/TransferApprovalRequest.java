package pl.glmc.serverlinker.common.transfer;

import pl.glmc.api.common.packet.PacketInfo;
import pl.glmc.api.common.packet.RequestPacket;
import pl.glmc.serverlinker.api.common.TransferAPI;
import pl.glmc.serverlinker.api.common.TransferMetaData;
import pl.glmc.serverlinker.common.LocalPacketRegistry;

import java.util.UUID;

public class TransferApprovalRequest extends RequestPacket {
    private static final PacketInfo PACKET_INFO = LocalPacketRegistry.Transfer.TRANSFER_APPROVAL_REQUEST;

    private final UUID playerUniqueId;
    private final TransferMetaData transferMetaData;
    private final TransferAPI.TransferReason transferReason;
    private final TransferAPI.TransferType transferType;
    private final boolean force;

    public TransferApprovalRequest(UUID playerUniqueId, TransferMetaData transferMetaData, TransferAPI.TransferReason transferReason, TransferAPI.TransferType transferType, boolean force) {
        this.playerUniqueId = playerUniqueId;
        this.transferMetaData = transferMetaData;
        this.transferReason = transferReason;
        this.transferType = transferType;
        this.force = force;
    }

    public UUID getPlayerUniqueId() {
        return playerUniqueId;
    }

    public TransferMetaData getTransferMetaData() {
        return transferMetaData;
    }

    public TransferAPI.TransferReason getTransferReason() {
        return transferReason;
    }

    public TransferAPI.TransferType getTransferType() {
        return transferType;
    }

    public boolean isForce() {
        return force;
    }

    @Override
    public String getPacketId() {
        return PACKET_INFO.getId();
    }
}
