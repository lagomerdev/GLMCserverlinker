package pl.glmc.serverlinker.common.rtp;

import pl.glmc.api.common.packet.PacketInfo;
import pl.glmc.api.common.packet.RequestPacket;
import pl.glmc.serverlinker.api.common.TransferLocation;
import pl.glmc.serverlinker.common.LocalPacketRegistry;

import java.util.UUID;

public class TeleportPlayerRequest extends RequestPacket {
    private static final PacketInfo PACKET_INFO = LocalPacketRegistry.Teleport.TELEPORT_PLAYER_REQUEST;

    private final UUID playerUniqueId;
    private final UUID playerTargetUniqueId;
    private final TransferLocation transferLocation;

    public TeleportPlayerRequest(UUID playerUniqueId, UUID playerTargetUniqueId) {
        this.playerUniqueId = playerUniqueId;
        this.playerTargetUniqueId = playerTargetUniqueId;
        this.transferLocation = null;
    }

    public TeleportPlayerRequest(UUID playerUniqueId, TransferLocation transferLocation) {
        this.playerUniqueId = playerUniqueId;
        this.playerTargetUniqueId = null;
        this.transferLocation = transferLocation;
    }

    public UUID getPlayerUniqueId() {
        return playerUniqueId;
    }

    public TransferLocation getTransferLocation() {
        return transferLocation;
    }

    public UUID getPlayerTargetUniqueId() {
        return playerTargetUniqueId;
    }

    public boolean isToPlayer() {
        return this.transferLocation == null;
    }

    public boolean isToCoords() {
        return this.playerTargetUniqueId == null;
    }

    @Override
    public String getPacketId() {
        return PACKET_INFO.getId();
    }
}
