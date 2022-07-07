package pl.glmc.serverlinker.bungee.api.rtp.packet;

import pl.glmc.api.common.packet.listener.ResponseHandlerListener;
import pl.glmc.serverlinker.common.LocalPacketRegistry;
import pl.glmc.serverlinker.common.rtp.TeleportPlayerResponse;

public class TeleportPlayerHandler extends ResponseHandlerListener<TeleportPlayerResponse, Boolean> {

    public TeleportPlayerHandler() {
        super(LocalPacketRegistry.Teleport.TELEPORT_PLAYER_RESPONSE, TeleportPlayerResponse.class);
    }

    @Override
    public void received(TeleportPlayerResponse teleportPlayerResponse) {
        this.complete(teleportPlayerResponse.getOriginUniqueId(), teleportPlayerResponse.isSuccess());
    }
}
