package pl.glmc.serverlinker.bungee.api.player.packet;

import pl.glmc.api.common.packet.listener.ResponseHandlerListener;
import pl.glmc.serverlinker.api.common.player.PlayerLocation;
import pl.glmc.serverlinker.common.LocalPacketRegistry;
import pl.glmc.serverlinker.common.other.GetPlayerCoordinatesResponse;
import pl.glmc.serverlinker.common.other.StunPlayerResponse;

public class StunPlayerHandler extends ResponseHandlerListener<StunPlayerResponse, Boolean> {

    public StunPlayerHandler() {
        super(LocalPacketRegistry.Other.STUN_PLAYER_RESPONSE, StunPlayerResponse.class);
    }

    @Override
    public void received(StunPlayerResponse stunPlayerResponse) {
        this.complete(stunPlayerResponse.getOriginUniqueId(), stunPlayerResponse.isSuccess());
    }
}
