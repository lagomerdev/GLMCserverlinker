package pl.glmc.serverlinker.bungee.api.player.packet;

import pl.glmc.api.common.packet.listener.ResponseHandlerListener;
import pl.glmc.serverlinker.api.common.player.StunResult;
import pl.glmc.serverlinker.common.LocalPacketRegistry;
import pl.glmc.serverlinker.common.other.StunPlayerConfirmation;

public class StunPlayerConfirmationHandler extends ResponseHandlerListener<StunPlayerConfirmation, StunResult> {

    public StunPlayerConfirmationHandler() {
        super(LocalPacketRegistry.Other.STUN_PLAYER_CONFIRMATION, StunPlayerConfirmation.class);
    }

    @Override
    public void received(StunPlayerConfirmation stunPlayerConfirmation) {
        this.complete(stunPlayerConfirmation.getOriginUniqueId(), stunPlayerConfirmation.getStunResult());
    }
}
