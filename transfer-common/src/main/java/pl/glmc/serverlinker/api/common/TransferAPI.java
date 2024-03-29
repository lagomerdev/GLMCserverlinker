package pl.glmc.serverlinker.api.common;

public class TransferAPI {

    public static class Timeout {
        public static final int TRANSFER_REQUEST = 500;
    }

    /*public enum TransferApprovalResult {
        ACCEPTED,
        REFUSED,
        TIMEOUT;
    }*/

    public enum TransferResult {
        SUCCESS,
        ALREADY_TRANSFERRING,
        INVALID_TARGET_SERVER,
        PLAYER_OFFLINE,
        FAILED_APPROVAL,
        FAILED_PERMISSIONS,
        FAILED_SYNC,
        FAILED_TRANSFERRING;
    }

    public enum TransferType {
        NORMAL(true),
        WITH_DATA(true),
        WITHOUT_DATA(false);


        private final boolean dataRequired;

        TransferType(boolean dataRequired) {
            this.dataRequired = dataRequired;
        }

        public boolean isDataRequired() {
            return dataRequired;
        }
    }

    public enum TransferReason {
        SERVER_TRANSFER,
        SERVER_MATCH,
        FIRST_JOIN,
        PROXY_JOIN;
    }

    public enum TransferDataResult {
        RECEIVED,
        TIMEOUT,
        PROCESS_CANCELED,
        CORRUPTED_DATA,
        SAVING_FAILED,
        PLAYER_OFFLINE;
    }

    public enum JoinResult {
        SUCCESS,
        ALREADY_TRANSFERRING,
        INVALID_TARGET_SERVER,
        FAILED_CONNECTING,
        FAILED_PERMISSIONS,
        FAILED_PLAYER_INFO,
        FAILED_DATA_SYNC,
        FAILED_OFFLINE_SYNC,
        FAILED_TRANSFERRING,
        DENIED;
    }

    public enum TransferApprovalResult {
        ACCEPTED(0, true, null),
        REJECTED(1, false, null),
        SERVER_ORIGIN_FULL(2, true, "serverlinker.bypass_full_server"),
        SERVER_LOCKED(3, true, "serverlinker.bypass_locked_server"),
        TIMEOUT(4, false, null),
        ALREADY_CONNECTED(5, false, null);

        private final int id;
        private final boolean accepted;
        private final String permission;

        TransferApprovalResult(int id, boolean accepted, String permission) {
            this.id = id;
            this.accepted = accepted;
            this.permission = permission;
        }

        public int getId() {
            return id;
        }

        public boolean isAccepted() {
            return accepted;
        }

        public String getPermission() {
            return permission;
        }

        public boolean requiresPermission() {
            return this.permission != null;
        }

        public static TransferApprovalResult fromId(int id) {
            for (TransferApprovalResult transferRequestResult : TransferApprovalResult.values()) {
                if (transferRequestResult.getId() == id) {
                    return transferRequestResult;
                }
            }

            return null;
        }
    }
}
