package pl.glmc.serverlinker.bungee.api.transfer;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectEvent;
import pl.glmc.serverlinker.api.bungee.event.SpecifyJoinServerEvent;
import pl.glmc.serverlinker.api.bungee.transfer.TransferService;
import pl.glmc.serverlinker.api.common.TransferAPI;
import pl.glmc.serverlinker.bungee.GlmcServerLinkerBungee;
import pl.glmc.serverlinker.bungee.api.transfer.listener.packet.OfflineDataHandler;
import pl.glmc.serverlinker.bungee.api.transfer.listener.packet.TransferApprovalHandler;
import pl.glmc.serverlinker.bungee.api.transfer.listener.packet.TransferDataHandler;
import pl.glmc.serverlinker.bungee.api.transfer.listener.packet.TransferRequestListener;
import pl.glmc.serverlinker.common.transfer.*;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class ApiTransferService implements TransferService {
    private static final String JOIN_QUIT_STATEMENT = "INSERT INTO `proxy_connection_logs`(`player_uuid`, `action`) VALUES (?, ?)";
    private static final String PLAYER_DATA_QUERY = "SELECT `last_server`, `last_server_type`, `player_data`, `player_data_server` FROM `player_info` WHERE `player_uuid` = ?";

    private final GlmcServerLinkerBungee plugin;

    private final HashMap<UUID, CompletableFuture<TransferAPI.TransferResult>> transferredPlayers;

    private final OfflineDataHandler offlineDataHandler;
    private final TransferApprovalHandler transferApprovalHandler;
    private final TransferDataHandler transferDataHandler;

    private final HashMap<UUID, Long> transferTimes = new HashMap<>();

    public ApiTransferService(GlmcServerLinkerBungee plugin) {
        this.plugin = plugin;

        this.transferredPlayers = new HashMap<>();

        this.offlineDataHandler = new OfflineDataHandler();
        this.transferApprovalHandler = new TransferApprovalHandler();
        this.transferDataHandler = new TransferDataHandler();

        this.init();

        this.plugin.getGlmcApiBungee().getPacketService().registerListener(this.offlineDataHandler);
        this.plugin.getGlmcApiBungee().getPacketService().registerListener(this.transferApprovalHandler);
        this.plugin.getGlmcApiBungee().getPacketService().registerListener(this.transferDataHandler);

        TransferRequestListener transferRequestListener = new TransferRequestListener(this.plugin, this);
    }

    private void init() {
        final String playerInfoStatement = "CREATE TABLE IF NOT EXISTS `player_info` (" +
                " `uuid` char(40) NOT NULL, " +
                " `last_connection` timestamp NOT NULL DEFAULT current_timestamp(), " +
                " `proxy_connection_status` tinyint(1) NOT NULL DEFAULT 0, " +
                " `server_connected` varchar(50) DEFAULT NULL, " +
                " `server_connection_status` tinyint(1) NOT NULL DEFAULT 0, " +
                " `last_server` varchar(50) NOT NULL, " +
                " `last_server_type` varchar(50) NOT NULL DEFAULT '0', " +
                " `player_data` mediumtext DEFAULT NULL, " +
                " `player_data_server` varchar(50) NOT NULL, " +
                " `player_data_timestamp` timestamp NULL DEFAULT NULL, " +
                " PRIMARY KEY (`uuid`), " +
                " UNIQUE KEY `uuid` (`uuid`)" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";

        final String proxyConnectionLogsStatement = "CREATE TABLE `proxy_connection_logs` (" +
                " `id` int(10) unsigned NOT NULL AUTO_INCREMENT, " +
                " `player_uuid` char(36) NOT NULL, " +
                " `action` tinyint(1) NOT NULL, " +
                " `timestamp` timestamp NOT NULL DEFAULT current_timestamp(), " +
                " PRIMARY KEY (`id`)" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";

        final String proxyConnectionLogsTrigger = "CREATE DEFINER=`root`@`localhost` " +
                "TRIGGER IF NOT EXISTS update_proxy_player_info " +
                "BEFORE INSERT ON proxy_connection_logs " +
                "FOR EACH ROW " +
                "BEGIN " +
                "IF (new.action = 0) " +
                "THEN UPDATE `player_info` SET `proxy_connection_status` = false WHERE uuid = new.player_uuid; " +
                "ELSE INSERT INTO `player_info` (`uuid`, `last_connection`, `proxy_connection_status`) VALUES (new.player_uuid, new.timestamp, true) ON DUPLICATE KEY UPDATE `proxy_connection_status` = true, `last_connection` = new.timestamp; " +
                "END IF; " +
                "END";

        final String serverConnectionLogsStatement = "CREATE TABLE `server_connection_logs` (" +
                " `id` int(10) unsigned NOT NULL AUTO_INCREMENT, " +
                " `player_uuid` varchar(36) NOT NULL, " +
                " `server` varchar(50) NOT NULL, " +
                " `server_type` varchar(50) NOT NULL, " +
                " `action` tinyint(1) NOT NULL, " +
                " `timestamp` timestamp NOT NULL DEFAULT current_timestamp(), " +
                " PRIMARY KEY (`id`)" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";

        final String serverConnectionLogsTrigger = "CREATE DEFINER=`root`@`localhost` " +
                "TRIGGER IF NOT EXISTS update_server_player_info " +
                "BEFORE INSERT ON server_connection_logs " +
                "FOR EACH ROW BEGIN " +
                "IF (new.action = 0) " +
                "THEN UPDATE player_info SET `server_connected` = null, `server_connection_status` = false WHERE uuid = new.player_uuid; " +
                "ELSE UPDATE player_info SET `server_connected` = new.server, `server_connection_status` = true, `last_server` = new.server, `last_server_type` = new.server_type WHERE uuid = new.player_uuid; " +
                "END IF; END";

        this.plugin.getDatabaseProvider().updateSync(playerInfoStatement);
        this.plugin.getDatabaseProvider().updateSync(proxyConnectionLogsStatement);
        this.plugin.getDatabaseProvider().updateSync(proxyConnectionLogsTrigger);
        this.plugin.getDatabaseProvider().updateSync(serverConnectionLogsStatement);
        this.plugin.getDatabaseProvider().updateSync(serverConnectionLogsTrigger);
    }

    private void specifyJoinServer(ProxiedPlayer player, String playerData, String lastServer, String lastServerType, CompletableFuture<TransferAPI.JoinResult> result) {
        this.plugin.getDatabaseProvider().updateAsync(JOIN_QUIT_STATEMENT, player.getUniqueId().toString(), 1);

        if (this.transferredPlayers.containsKey(player.getUniqueId())) {
            result.complete(TransferAPI.JoinResult.FAILED);

            return;
        }

        SpecifyJoinServerEvent specifyJoinServerEvent = new SpecifyJoinServerEvent(lastServer, lastServerType, false);
        this.plugin.getProxy().getPluginManager().callEvent(specifyJoinServerEvent);

        String targetServer = specifyJoinServerEvent.getJoinServer();

        ServerInfo serverTargetInfo = this.plugin.getProxy().getServerInfo(targetServer);
        if (serverTargetInfo == null) {
            result.complete(TransferAPI.JoinResult.FAILED);

            return;
        }

        UUID transferUniqueId = UUID.randomUUID();
        TransferData transferData = new TransferData(player.getUniqueId(), transferUniqueId, playerData, "", "proxy");

        this.transferDataHandler.create(transferUniqueId)
                .completeOnTimeout(TransferAPI.TransferDataResult.TIMEOUT, 1, TimeUnit.SECONDS)
                .thenAccept(dataResult -> {
                    if (dataResult == TransferAPI.TransferDataResult.RECEIVED) {
                        player.connect(serverTargetInfo, (success, exception) -> {
                            result.complete(success ? TransferAPI.JoinResult.SUCCESS : TransferAPI.JoinResult.FAILED);
                        }, false, ServerConnectEvent.Reason.PLUGIN, 1000);
                    } else {
                        result.complete(TransferAPI.JoinResult.FAILED);
                    }
                });

        this.plugin.getGlmcApiBungee().getPacketService().sendPacket(transferData, targetServer);
    }

    private void firstJoin(ProxiedPlayer player, CompletableFuture<TransferAPI.JoinResult> result) {
        SpecifyJoinServerEvent specifyJoinServerEvent = new SpecifyJoinServerEvent(null, null, false);
        this.plugin.getProxy().getPluginManager().callEvent(specifyJoinServerEvent);

        ServerInfo serverTargetInfo = specifyJoinServerEvent.getJoinServer() == null ? this.plugin.getProxy().getServerInfo("survival") : //todo remove hard-coded server
                this.plugin.getProxy().getServerInfo(specifyJoinServerEvent.getJoinServer());
        if (serverTargetInfo == null) {
            result.complete(TransferAPI.JoinResult.FAILED);

            return;
        }

        TransferApprovalRequest transferApprovalRequest = new TransferApprovalRequest(player.getUniqueId(), TransferAPI.TransferReason.FIRST_JOIN, true);

        this.transferApprovalHandler.create(transferApprovalRequest.getUniqueId())
                .completeOnTimeout(TransferAPI.TransferApprovalResult.TIMEOUT, 1, TimeUnit.SECONDS)
                .thenAccept(approvalResult -> {
                    if (approvalResult.requiresPermission() && !player.hasPermission(approvalResult.getPermission())) {
                        result.complete(TransferAPI.JoinResult.FAILED);
                    } else if (approvalResult.accepted()) {
                        player.connect(serverTargetInfo, (success, exception) -> {
                            result.complete(success ? TransferAPI.JoinResult.SUCCESS : TransferAPI.JoinResult.FAILED);
                        }, false, ServerConnectEvent.Reason.PLUGIN, 1000);
                    } else {
                        result.complete(TransferAPI.JoinResult.FAILED);
                    }
                });

        this.plugin.getGlmcApiBungee().getPacketService().sendPacket(transferApprovalRequest, serverTargetInfo.getName());
    }

    private void syncData(ProxiedPlayer player, String lastServer, String lastServerType, CompletableFuture<TransferAPI.JoinResult> result) {
        OfflineDataRequest offlineDataRequest = new OfflineDataRequest(player.getUniqueId());

        this.offlineDataHandler.create(offlineDataRequest.getUniqueId())
                .completeOnTimeout(null, 1, TimeUnit.SECONDS)
                .thenAccept(data -> {
                    if (data == null) {
                        result.complete(TransferAPI.JoinResult.FAILED);

                        return;
                    }



                    this.specifyJoinServer(player, data, lastServer, lastServerType, result);
                });

        this.plugin.getGlmcApiBungee().getPacketService().sendPacket(offlineDataRequest, lastServer);
    }

    @Override
    public CompletableFuture<TransferAPI.JoinResult> processJoin(ProxiedPlayer player) {
        CompletableFuture<TransferAPI.JoinResult> result = new CompletableFuture<>();

        this.plugin.getDatabaseProvider().getAsync(resultSet -> {
            try {
                if (resultSet.next()) {
                    String lastServer = resultSet.getString("last_server");
                    String lastServerType = resultSet.getString("last_server_type");
                    String playerData = resultSet.getString("player_data");
                    String playerDataServer = resultSet.getString("player_data_server");

                    boolean dataSynced = lastServer.equals(playerDataServer);

                    if (!dataSynced) { //data sync check
                        this.syncData(player, lastServer, lastServerType, result);
                    } else {
                        this.specifyJoinServer(player, playerData, lastServer, lastServerType, result);
                    }
                } else { //new player
                    this.firstJoin(player, result);
                }
            } catch (SQLException e) {
                result.complete(TransferAPI.JoinResult.FAILED);

                e.printStackTrace();
            }
        }, PLAYER_DATA_QUERY, player.getUniqueId().toString());

        return result;
    }

    @Override
    public void processDisconnect(ProxiedPlayer player) {
        this.plugin.getDatabaseProvider().updateAsync(JOIN_QUIT_STATEMENT, player.getUniqueId().toString(), 0);
    }

    @Override
    public CompletableFuture<TransferAPI.TransferResult> transferPlayer(UUID playerUniqueId, String serverTarget, TransferAPI.TransferReason transferReason, boolean force) {
        Objects.requireNonNull(playerUniqueId);
        Objects.requireNonNull(serverTarget);

        CompletableFuture<TransferAPI.TransferResult> result = new CompletableFuture<>();

        ProxiedPlayer player = this.plugin.getProxy().getPlayer(playerUniqueId);

        if (player == null || !player.isConnected()) {
            result.complete(TransferAPI.TransferResult.PLAYER_OFFLINE);

            return result;
        }

        if (this.transferredPlayers.containsKey(playerUniqueId)) {
            result.complete(TransferAPI.TransferResult.ALREADY_TRANSFERRING);

            return result;
        }

        ServerInfo serverTargetInfo = this.plugin.getProxy().getServerInfo(serverTarget);
        if (serverTargetInfo == null) {
            result.complete(TransferAPI.TransferResult.INVALID_TARGET_SERVER);

            return result;
        }
        String serverOrigin = player.getServer().getInfo().getName();

        this.transferTimes.put(player.getUniqueId(), System.currentTimeMillis());
        this.transferredPlayers.put(player.getUniqueId(), result);

        this.sendTransferApprovalRequest(player, serverOrigin, serverTarget, transferReason, force);

        return result;
    }

    private void sendTransferApprovalRequest(ProxiedPlayer player, String serverOrigin, String serverTarget, TransferAPI.TransferReason transferReason, boolean force) {
        TransferApprovalRequest transferApprovalRequest = new TransferApprovalRequest(player.getUniqueId(), transferReason, force);

        this.transferApprovalHandler.create(transferApprovalRequest.getUniqueId())
                .completeOnTimeout(TransferAPI.TransferApprovalResult.TIMEOUT, 500, TimeUnit.MILLISECONDS)
                .thenAccept(result -> {
                    if (result.accepted() || (force && (result == TransferAPI.TransferApprovalResult.REJECTED
                            || result == TransferAPI.TransferApprovalResult.SERVER_LOCKED
                            || result == TransferAPI.TransferApprovalResult.SERVER_ORIGIN_FULL))) {
                        if (result.requiresPermission() && !player.hasPermission(result.getPermission())) {
                            this.complete(player.getUniqueId(), TransferAPI.TransferResult.FAILED_PERMISSIONS);
                        } else {
                            if (transferReason.isDataRequired()) {
                                this.sendDataTransferRequest(player, serverOrigin, serverTarget, transferReason, force);
                            } else {
                                this.finalTransfer(player, serverOrigin, serverTarget, transferReason, force);
                            }
                        }
                    } else {
                        this.complete(player.getUniqueId(), TransferAPI.TransferResult.FAILED_APPROVAL);
                    }
                });

        this.plugin.getGlmcApiBungee().getPacketService().sendPacket(transferApprovalRequest, serverTarget);
    }

    private void sendDataTransferRequest(ProxiedPlayer player, String serverOrigin, String serverTarget, TransferAPI.TransferReason transferReason, boolean force) {
        TransferDataRequest transferDataRequest = new TransferDataRequest(player.getUniqueId(), serverTarget);

        this.transferDataHandler.create(transferDataRequest.getUniqueId())
                .completeOnTimeout(TransferAPI.TransferDataResult.TIMEOUT, 1, TimeUnit.SECONDS)
                .thenAccept(result -> {
                    if (result == TransferAPI.TransferDataResult.RECEIVED) {
                        this.finalTransfer(player, serverOrigin, serverTarget, transferReason, force);
                    } else {
                        TransferUnfreeze transferUnfreeze = new TransferUnfreeze(player.getUniqueId());

                        this.plugin.getGlmcApiBungee().getPacketService().sendPacket(transferUnfreeze, serverOrigin);

                        this.complete(player.getUniqueId(), TransferAPI.TransferResult.FAILED_SYNC);
                    }
                });

        this.plugin.getGlmcApiBungee().getPacketService().sendPacket(transferDataRequest, serverOrigin);
    }

    private void finalTransfer(ProxiedPlayer player, String serverOrigin, String serverTarget, TransferAPI.TransferReason transferReason, boolean force) {
        if (!player.isConnected()) {
            this.complete(player.getUniqueId(), TransferAPI.TransferResult.PLAYER_OFFLINE);

            return;
        }

        ServerInfo serverTargetInfo = this.plugin.getProxy().getServerInfo(serverTarget);
        player.connect(serverTargetInfo, (success, exception) -> {
            this.complete(player.getUniqueId(), success ? TransferAPI.TransferResult.SUCCESS : TransferAPI.TransferResult.FAILED_TRANSFERRING);
        }, false, ServerConnectEvent.Reason.PLUGIN, 1000);
    }

    private void complete(UUID playerUniqueId, TransferAPI.TransferResult transferResult) {
        if (transferResult == TransferAPI.TransferResult.SUCCESS) {
            this.plugin.getLogger().info(ChatColor.GREEN + "Successfully transferred player " + playerUniqueId + "!");
        } else {
            this.plugin.getLogger().info(ChatColor.RED + "Failed to transfer player " + playerUniqueId + "! (" + transferResult + ")");
        }

        long transferTime = System.currentTimeMillis() - this.transferTimes.remove(playerUniqueId);

        this.plugin.getLogger().info(ChatColor.YELLOW + "Transferring " + playerUniqueId + " took " + transferTime + "ms!");

        this.transferredPlayers.remove(playerUniqueId).complete(transferResult);
    }
}
