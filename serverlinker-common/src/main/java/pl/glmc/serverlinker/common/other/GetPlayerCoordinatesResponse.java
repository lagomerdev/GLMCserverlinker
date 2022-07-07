package pl.glmc.serverlinker.common.other;

import pl.glmc.api.common.packet.PacketInfo;
import pl.glmc.api.common.packet.ResponsePacket;
import pl.glmc.serverlinker.api.common.TransferLocation;
import pl.glmc.serverlinker.api.common.player.PlayerLocation;
import pl.glmc.serverlinker.common.LocalPacketRegistry;

import java.util.UUID;

public class GetPlayerCoordinatesResponse extends ResponsePacket {
    private static final PacketInfo PACKET_INFO = LocalPacketRegistry.Other.GET_PLAYER_COORDINATES_RESPONSE;

    private final PlayerLocation location;

    public GetPlayerCoordinatesResponse(boolean success, UUID originUniqueId, PlayerLocation location) {
        super(success, originUniqueId);

        this.location = location;
    }

    public PlayerLocation getLocation() {
        return location;
    }

    @Override
    public String getPacketId() {
        return PACKET_INFO.getId();
    }
}
