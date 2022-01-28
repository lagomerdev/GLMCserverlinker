package pl.glmc.serverlinker.common.transfer;

import pl.glmc.api.common.packet.PacketInfo;
import pl.glmc.api.common.packet.RequestPacket;
import pl.glmc.serverlinker.common.LocalPacketRegistry;

import java.util.UUID;

public class TransferDataRequest extends RequestPacket {
    private static final PacketInfo PACKET_INFO = LocalPacketRegistry.Transfer.TRANSFER_DATA_REQUEST;

    private final UUID playerUniqueId;
    private final String serverTarget;

    public TransferDataRequest(UUID playerUniqueId, String serverTarget) {
        this.playerUniqueId = playerUniqueId;
        this.serverTarget = serverTarget;
    }

    public UUID getPlayerUniqueId() {
        return playerUniqueId;
    }

    public String getServerTarget() {
        return serverTarget;
    }

    @Override
    public String getPacketId() {
        return PACKET_INFO.getId();
    }
}
