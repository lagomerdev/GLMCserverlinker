package pl.glmc.serverlinker.common.rtp;

import pl.glmc.api.common.packet.PacketInfo;
import pl.glmc.api.common.packet.ResponsePacket;
import pl.glmc.serverlinker.api.common.TransferLocation;
import pl.glmc.serverlinker.common.LocalPacketRegistry;

import java.util.UUID;

public class GetRandomCoordinatesResponse extends ResponsePacket {
    private static final PacketInfo PACKET_INFO = LocalPacketRegistry.Teleport.GET_RANDOM_COORDINATES_RESPONSE;

    private final TransferLocation transferLocation;

    public GetRandomCoordinatesResponse(boolean success, UUID originUniqueId, TransferLocation transferLocation) {
        super(success, originUniqueId);
        this.transferLocation = transferLocation;
    }

    public TransferLocation getTransferLocation() {
        return transferLocation;
    }

    @Override
    public String getPacketId() {
        return PACKET_INFO.getId();
    }
}
