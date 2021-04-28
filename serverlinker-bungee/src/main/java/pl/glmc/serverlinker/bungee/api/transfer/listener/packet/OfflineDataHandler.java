package pl.glmc.serverlinker.bungee.api.transfer.listener.packet;

import pl.glmc.api.common.packet.listener.ResponseHandlerListener;
import pl.glmc.serverlinker.common.LocalPacketRegistry;
import pl.glmc.serverlinker.common.transfer.OfflineDataResponse;

public class OfflineDataHandler extends ResponseHandlerListener<OfflineDataResponse, String> {

    public OfflineDataHandler() {
        super(LocalPacketRegistry.Transfer.OFFLINE_DATA_RESPONSE, OfflineDataResponse.class);
    }

    @Override
    public void received(OfflineDataResponse offlineDataResponse) {
        this.complete(offlineDataResponse.getOriginUniqueId(), offlineDataResponse.getData());
    }
}
