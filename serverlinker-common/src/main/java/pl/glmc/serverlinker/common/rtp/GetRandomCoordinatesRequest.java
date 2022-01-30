package pl.glmc.serverlinker.common.rtp;

import pl.glmc.api.common.packet.PacketInfo;
import pl.glmc.api.common.packet.RequestPacket;
import pl.glmc.serverlinker.common.LocalPacketRegistry;

public class GetRandomCoordinatesRequest extends RequestPacket {
    private static final PacketInfo PACKET_INFO = LocalPacketRegistry.RandomTeleport.GET_RANDOM_COORDINATES_REQUEST;

    @Override
    public String getPacketId() {
        return PACKET_INFO.getId();
    }
}
