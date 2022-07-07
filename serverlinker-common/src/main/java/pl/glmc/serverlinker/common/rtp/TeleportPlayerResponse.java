package pl.glmc.serverlinker.common.rtp;

import pl.glmc.api.common.packet.PacketInfo;
import pl.glmc.api.common.packet.ResponsePacket;
import pl.glmc.serverlinker.common.LocalPacketRegistry;

import java.util.UUID;

public class TeleportPlayerResponse extends ResponsePacket {
    private static final PacketInfo PACKET_INFO = LocalPacketRegistry.Teleport.TELEPORT_PLAYER_RESPONSE;

    public TeleportPlayerResponse(boolean success, UUID originUniqueId) {
        super(success, originUniqueId);
    }

    @Override
    public String getPacketId() {
        return PACKET_INFO.getId();
    }
}
