package pl.glmc.serverlinker.bungee.api.transfer;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import pl.glmc.serverlinker.api.bungee.event.SpecifyJoinServerEvent;
import pl.glmc.serverlinker.api.bungee.transfer.PlayerManager;
import pl.glmc.serverlinker.api.common.TransferAPI;
import pl.glmc.serverlinker.api.common.TransferMetaData;
import pl.glmc.serverlinker.bungee.GlmcServerLinkerBungee;
import pl.glmc.serverlinker.bungee.api.transfer.listener.event.PlayerJoinQuitListener;
import pl.glmc.serverlinker.bungee.api.transfer.listener.packet.OfflineDataHandler;
import pl.glmc.serverlinker.common.transfer.OfflineDataRequest;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class ApiPlayerManager implements PlayerManager {
    private static final String JOIN_QUIT_STATEMENT = "INSERT INTO `proxy_connection_logs`(`player_uuid`, `action`) VALUES (?, ?)";
    private static final String PLAYER_DATA_QUERY = "SELECT `last_server`, `last_server_type`, `player_data`, `player_data_server` FROM `player_info` WHERE `uuid` = ?";

    private final HashMap<UUID, CompletableFuture<TransferAPI.JoinResult>> joiningPlayers = new HashMap<>();
    private final HashMap<UUID, Long> joiningTimes = new HashMap<>();

    private final OfflineDataHandler offlineDataHandler;

    private final GlmcServerLinkerBungee plugin;
    private final ApiTransferService transferService;

    public ApiPlayerManager(GlmcServerLinkerBungee plugin, ApiTransferService transferService) {
        this.plugin = plugin;
        this.transferService = transferService;

        this.offlineDataHandler = new OfflineDataHandler();

        this.init();

        this.plugin.getGlmcApiBungee().getPacketService().registerListener(this.offlineDataHandler, this.plugin);

        PlayerJoinQuitListener playerJoinQuitListener = new PlayerJoinQuitListener(this.plugin, this);
    }

    @Override
    public CompletableFuture<TransferAPI.JoinResult> processJoin(ProxiedPlayer player) {
        CompletableFuture<TransferAPI.JoinResult> result = new CompletableFuture<>();

        if (this.joiningPlayers.containsKey(player.getUniqueId())) {
            this.complete(player.getUniqueId(), TransferAPI.JoinResult.ALREADY_TRANSFERRING);

            return result;
        }

        this.joiningPlayers.put(player.getUniqueId(), result);
        this.joiningTimes.put(player.getUniqueId(), System.currentTimeMillis());

        this.plugin.getDatabaseProvider().getAsync(resultSet -> {
            try {
                if (resultSet.next()) {
                    String lastServer = resultSet.getString("last_server");
                    String lastServerType = resultSet.getString("last_server_type");
                    String playerData = resultSet.getString("player_data");
                    String playerDataServer = resultSet.getString("player_data_server");

                    if (lastServer == null || playerDataServer == null) {
                        this.syncData(player, lastServer, lastServerType);

                        return;
                    }

                    boolean dataSynced = lastServer.equals(playerDataServer);

                    if (!dataSynced) { //data sync check
                        this.syncData(player, lastServer, lastServerType);
                    } else {
                        this.specifyServer(player, playerData, lastServer, lastServerType);
                    }
                } else { //new player
                    this.firstJoin(player);
                }
            } catch (SQLException e) {
                this.complete(player.getUniqueId(), TransferAPI.JoinResult.FAILED_PLAYER_INFO);

                e.printStackTrace();
            }
        }, PLAYER_DATA_QUERY, player.getUniqueId().toString());

        return result;
    }

    @Override
    public void processDisconnect(ProxiedPlayer player) {
        this.plugin.getDatabaseProvider().updateAsync(JOIN_QUIT_STATEMENT, player.getUniqueId().toString(), 0);
    }

    private void init() {
        final String playerInfoStatement = "CREATE TABLE IF NOT EXISTS `player_info` (" +
                " `uuid` char(40) NOT NULL, " +
                " `last_connection` timestamp NOT NULL DEFAULT current_timestamp(), " +
                " `proxy_connection_status` tinyint(1) NOT NULL DEFAULT 0, " +
                " `server_connected` varchar(50) DEFAULT NULL, " +
                " `server_connection_status` tinyint(1) NOT NULL DEFAULT 0, " +
                " `last_server` varchar(50) DEFAULT NULL, " +
                " `last_server_type` varchar(50) DEFAULT NULL, " +
                " `player_data` mediumtext DEFAULT NULL, " +
                " `player_data_server` varchar(50) DEFAULT NULL, " +
                " `player_data_timestamp` timestamp NULL DEFAULT NULL, " +
                " PRIMARY KEY (`uuid`), " +
                " UNIQUE KEY `uuid` (`uuid`)" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";

        final String proxyConnectionLogsStatement = "CREATE TABLE IF NOT EXISTS `proxy_connection_logs` (" +
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

        final String serverConnectionLogsStatement = "CREATE TABLE IF NOT EXISTS `server_connection_logs` (" +
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

    private void specifyServer(ProxiedPlayer player, String playerData, String lastServer, String lastServerType) {
        this.plugin.getDatabaseProvider().updateAsync(JOIN_QUIT_STATEMENT, player.getUniqueId().toString(), 1);

        SpecifyJoinServerEvent specifyJoinServerEvent = new SpecifyJoinServerEvent(lastServer, lastServerType, false); //todo fail transfer if no server chosen
        this.plugin.getProxy().getPluginManager().callEvent(specifyJoinServerEvent);

        ServerInfo serverTargetInfo = specifyJoinServerEvent.getJoinServer() == null ? this.plugin.getProxy().getServerInfo("survival") : //todo remove hard-coded server
                this.plugin.getProxy().getServerInfo( specifyJoinServerEvent.getJoinServer());

        if (serverTargetInfo == null) {
            this.complete(player.getUniqueId(), TransferAPI.JoinResult.INVALID_TARGET_SERVER);

            return;
        }

        TransferMetaData transferMetaData = new TransferMetaData();
        this.transferService.transferPlayerWithData(player, serverTargetInfo.getName(), playerData, transferMetaData, TransferAPI.TransferReason.FIRST_JOIN)
                .thenAccept(transferResult -> {
                    System.out.println(transferResult);
                    if (transferResult == TransferAPI.TransferResult.SUCCESS) {
                        this.complete(player.getUniqueId(), TransferAPI.JoinResult.SUCCESS);
                    } else {
                        this.complete(player.getUniqueId(), TransferAPI.JoinResult.FAILED_TRANSFERRING);
                    }
                });
    }

    private void syncData(ProxiedPlayer player, String lastServer, String lastServerType) {
        OfflineDataRequest offlineDataRequest = new OfflineDataRequest(player.getUniqueId());

        this.offlineDataHandler.create(offlineDataRequest.getUniqueId())
                .completeOnTimeout(null, 1, TimeUnit.SECONDS)
                .thenAccept(data -> {
                    if (data == null) {
                        this.complete(player.getUniqueId(), TransferAPI.JoinResult.FAILED_OFFLINE_SYNC);

                        return;
                    }

                    this.specifyServer(player, data, lastServer, lastServerType);
                });

        this.plugin.getGlmcApiBungee().getPacketService().sendPacket(offlineDataRequest, lastServer);
    }

    private void firstJoin(ProxiedPlayer player) {
        this.plugin.getDatabaseProvider().updateAsync(JOIN_QUIT_STATEMENT, player.getUniqueId().toString(), 1);

        SpecifyJoinServerEvent specifyJoinServerEvent = new SpecifyJoinServerEvent(null, null, false);
        this.plugin.getProxy().getPluginManager().callEvent(specifyJoinServerEvent);

        ServerInfo serverTargetInfo = specifyJoinServerEvent.getJoinServer() == null ? this.plugin.getProxy().getServerInfo("spawn") : //todo remove hard-coded server
                this.plugin.getProxy().getServerInfo( specifyJoinServerEvent.getJoinServer());

        if (serverTargetInfo == null) {
            this.complete(player.getUniqueId(), TransferAPI.JoinResult.INVALID_TARGET_SERVER);

            return;
        }

        TransferMetaData transferMetaData = new TransferMetaData();
        this.transferService.transferPlayerWithoutData(player, serverTargetInfo.getName(), transferMetaData, TransferAPI.TransferReason.FIRST_JOIN) //todo stuck on first join
                .thenAccept(transferResult -> {
                    if (transferResult == TransferAPI.TransferResult.SUCCESS) {
                        this.complete(player.getUniqueId(), TransferAPI.JoinResult.SUCCESS);
                    } else {
                        this.complete(player.getUniqueId(), TransferAPI.JoinResult.FAILED_TRANSFERRING);
                    }
                });
    }

    private void complete(UUID playerUniqueId, TransferAPI.JoinResult joinResult) {
        long joinTime = System.currentTimeMillis() - this.joiningTimes.remove(playerUniqueId);

        this.plugin.getLogger().info(ChatColor.YELLOW + "Joining player " + playerUniqueId + " " + joinResult + " took " + joinTime + " ms");

        this.joiningPlayers.remove(playerUniqueId).complete(joinResult);
    }
}
