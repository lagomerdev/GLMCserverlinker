package pl.glmc.serverlinker.common;

import pl.glmc.serverlinker.api.common.TransferAPI;
import pl.glmc.serverlinker.api.common.TransferMetaData;

public class TransferProperties {
    private final String serverOrigin, serverTarget;
    private final TransferMetaData transferMetaData;
    private final TransferAPI.TransferReason transferReason;
    private final TransferAPI.TransferType transferType;
    private final boolean force;

    public TransferProperties(String serverOrigin, String serverTarget, TransferMetaData transferMetaData, TransferAPI.TransferReason transferReason, TransferAPI.TransferType transferType, boolean force) {
        this.serverOrigin = serverOrigin;
        this.serverTarget = serverTarget;
        this.transferMetaData = transferMetaData;
        this.transferReason = transferReason;
        this.transferType = transferType;
        this.force = force;
    }

    public TransferProperties(String serverTarget, TransferMetaData transferMetaData, TransferAPI.TransferReason transferReason, TransferAPI.TransferType transferType) {
        this.serverOrigin = "null";
        this.serverTarget = serverTarget;
        this.transferMetaData = transferMetaData;
        this.transferReason = transferReason;
        this.transferType = transferType;
        this.force = false;
    }

    public String getServerOrigin() {
        return serverOrigin;
    }

    public String getServerTarget() {
        return serverTarget;
    }

    public TransferMetaData getTransferMetaData() {
        return transferMetaData;
    }

    public TransferAPI.TransferReason getTransferReason() {
        return transferReason;
    }

    public TransferAPI.TransferType getTransferType() {
        return transferType;
    }

    public boolean isForce() {
        return force;
    }

}
