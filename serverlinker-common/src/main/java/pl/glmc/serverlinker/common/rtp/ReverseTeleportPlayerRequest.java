package pl.glmc.serverlinker.common.rtp;

import pl.glmc.api.common.packet.PacketInfo;
import pl.glmc.api.common.packet.RequestPacket;
import pl.glmc.serverlinker.api.common.TransferLocation;
import pl.glmc.serverlinker.common.LocalPacketRegistry;

import java.util.UUID;

public class ReverseTeleportPlayerRequest extends RequestPacket {
    private static final PacketInfo PACKET_INFO = LocalPacketRegistry.Teleport.REVERSE_TELEPORT_PLAYER_REQUEST;

    private final UUID playerUniqueId;
    private final String sectorType;
    private final TransferLocation transferLocation;
    private final boolean force;

    public ReverseTeleportPlayerRequest(UUID playerUniqueId, String sectorType, TransferLocation transferLocation, boolean force) {
        this.playerUniqueId = playerUniqueId;
        this.sectorType = sectorType;
        this.transferLocation = transferLocation;
        this.force = force;
    }

    public UUID getPlayerUniqueId() {
        return playerUniqueId;
    }

    public String getSectorType() {
        return sectorType;
    }

    public TransferLocation getTransferLocation() {
        return transferLocation;
    }

    public boolean isForce() {
        return force;
    }

    @Override
    public String getPacketId() {
        return PACKET_INFO.getId();
    }
}
