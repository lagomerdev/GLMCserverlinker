package pl.glmc.serverlinker.common.transfer;

import pl.glmc.api.common.packet.PacketInfo;
import pl.glmc.api.common.packet.ResponsePacket;
import pl.glmc.serverlinker.common.LocalPacketRegistry;

import java.util.UUID;

public class OfflineDataResponse extends ResponsePacket {
    private static final PacketInfo PACKET_INFO = LocalPacketRegistry.Transfer.OFFLINE_DATA_RESPONSE;

    private final String data;

    public OfflineDataResponse(boolean success, UUID originUniqueId, String data) {
        super(success, originUniqueId);

        this.data = data;
    }

    public String getData() {
        return data;
    }

    @Override
    public String getPacketId() {
        return PACKET_INFO.getId();
    }
}
