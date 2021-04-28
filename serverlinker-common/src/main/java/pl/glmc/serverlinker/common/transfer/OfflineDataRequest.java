package pl.glmc.serverlinker.common.transfer;

import pl.glmc.api.common.packet.PacketInfo;
import pl.glmc.api.common.packet.RequestPacket;
import pl.glmc.serverlinker.common.LocalPacketRegistry;

import java.util.UUID;

public class OfflineDataRequest extends RequestPacket {
    private static final PacketInfo PACKET_INFO = LocalPacketRegistry.Transfer.OFFLINE_DATA_REQUEST;

    private final UUID playerUniqueId;

    public OfflineDataRequest(UUID playerUniqueId) {
        this.playerUniqueId = playerUniqueId;

    }

    public UUID getPlayerUniqueId() {
        return playerUniqueId;
    }

    @Override
    public String getPacketId() {
        return PACKET_INFO.getId();
    }
}
