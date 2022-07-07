package pl.glmc.serverlinker.bungee.api.rtp.packet;

import pl.glmc.api.common.packet.listener.ResponseHandlerListener;
import pl.glmc.serverlinker.api.common.TransferLocation;
import pl.glmc.serverlinker.common.LocalPacketRegistry;
import pl.glmc.serverlinker.common.rtp.GetRandomCoordinatesResponse;

public class GetRandomCoordinatesHandler extends ResponseHandlerListener<GetRandomCoordinatesResponse, TransferLocation> {

    public GetRandomCoordinatesHandler() {
        super(LocalPacketRegistry.Teleport.GET_RANDOM_COORDINATES_RESPONSE, GetRandomCoordinatesResponse.class);
    }

    @Override
    public void received(GetRandomCoordinatesResponse getRandomCoordinatesResponse) {
        this.complete(getRandomCoordinatesResponse.getOriginUniqueId(), getRandomCoordinatesResponse.getTransferLocation());
    }
}
