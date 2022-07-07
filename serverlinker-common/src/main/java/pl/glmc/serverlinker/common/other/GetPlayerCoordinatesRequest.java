package pl.glmc.serverlinker.common.other;

import pl.glmc.api.common.packet.PacketInfo;
import pl.glmc.api.common.packet.RequestPacket;
import pl.glmc.serverlinker.common.LocalPacketRegistry;

import java.util.UUID;

public class GetPlayerCoordinatesRequest extends RequestPacket {
    private static final PacketInfo PACKET_INFO = LocalPacketRegistry.Other.GET_PLAYER_COORDINATES_REQUEST;

    private final UUID playerUniqueId;

    public GetPlayerCoordinatesRequest(UUID playerUniqueId) {
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
