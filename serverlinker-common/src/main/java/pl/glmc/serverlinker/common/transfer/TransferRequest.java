package pl.glmc.serverlinker.common.transfer;

import pl.glmc.api.common.packet.PacketInfo;
import pl.glmc.api.common.packet.RequestPacket;
import pl.glmc.serverlinker.api.common.TransferAPI;
import pl.glmc.serverlinker.common.LocalPacketRegistry;

import java.util.UUID;

public class TransferRequest extends RequestPacket {
    private static final PacketInfo PACKET_INFO = LocalPacketRegistry.Transfer.TRANSFER_REQUEST;

    private final UUID playerUniqueId;
    private final String serverTarget;
    private final TransferAPI.TransferReason transferReason;
    private final boolean force;

    public TransferRequest(UUID playerUniqueId, String serverTarget, TransferAPI.TransferReason transferReason, boolean force) {
        this.playerUniqueId = playerUniqueId;
        this.serverTarget = serverTarget;
        this.transferReason = transferReason;
        this.force = force;
    }

    @Override
    public String getPacketId() {
        return PACKET_INFO.getId();
    }

    public UUID getPlayerUniqueId() {
        return playerUniqueId;
    }

    public String getServerTarget() {
        return serverTarget;
    }

    public TransferAPI.TransferReason getTransferReason() {
        return transferReason;
    }

    public boolean isForce() {
        return force;
    }
}
