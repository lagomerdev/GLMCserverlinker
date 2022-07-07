package pl.glmc.serverlinker.bungee.api.player.packet;

import pl.glmc.api.common.packet.listener.ResponseHandlerListener;
import pl.glmc.serverlinker.api.common.player.PlayerLocation;
import pl.glmc.serverlinker.common.LocalPacketRegistry;
import pl.glmc.serverlinker.common.other.GetPlayerCoordinatesRequest;
import pl.glmc.serverlinker.common.other.GetPlayerCoordinatesResponse;
import pl.glmc.serverlinker.common.rtp.TeleportPlayerResponse;

public class GetPlayerCoordinatesHandler extends ResponseHandlerListener<GetPlayerCoordinatesResponse, PlayerLocation> {

    public GetPlayerCoordinatesHandler() {
        super(LocalPacketRegistry.Other.GET_PLAYER_COORDINATES_RESPONSE, GetPlayerCoordinatesResponse.class);
    }

    @Override
    public void received(GetPlayerCoordinatesResponse getPlayerCoordinatesResponse) {
        this.complete(getPlayerCoordinatesResponse.getOriginUniqueId(), getPlayerCoordinatesResponse.getLocation());
    }
}
