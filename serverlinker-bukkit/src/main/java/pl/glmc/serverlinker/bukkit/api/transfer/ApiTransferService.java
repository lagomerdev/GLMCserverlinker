package pl.glmc.serverlinker.bukkit.api.transfer;

import net.querz.nbt.io.NBTUtil;
import net.querz.nbt.io.NamedTag;
import net.querz.nbt.io.SNBTUtil;
import net.querz.nbt.tag.CompoundTag;
import org.apache.logging.log4j.util.Base64Util;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;
import pl.glmc.serverlinker.api.bukkit.transfer.TransferService;
import pl.glmc.serverlinker.api.common.TransferAPI;
import pl.glmc.serverlinker.api.common.TransferMetaData;
import pl.glmc.serverlinker.bukkit.GlmcServerLinkerBukkit;
import pl.glmc.serverlinker.bukkit.api.transfer.listener.event.PlayerBorderListener;
import pl.glmc.serverlinker.bukkit.api.transfer.listener.event.PlayerFreezeListener;
import pl.glmc.serverlinker.bukkit.api.transfer.listener.event.PlayerJoinQuitListener;
import pl.glmc.serverlinker.bukkit.api.transfer.listener.event.TransferProcessListener;
import pl.glmc.serverlinker.bukkit.api.transfer.listener.packet.*;
import pl.glmc.serverlinker.common.Compression;
import pl.glmc.serverlinker.common.transfer.TransferRequest;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class ApiTransferService implements TransferService {
    private static final String JOIN_QUIT_STATEMENT = "INSERT INTO `server_connection_logs`(`player_uuid`, `server`, `server_type`, `action`) VALUES (?,?,?,?)";
    private static final String DATA_UPDATE_STATEMENT = "UPDATE `player_info` SET player_data = ?, player_data_server = ? WHERE uuid = ?";

    private final GlmcServerLinkerBukkit plugin;

    private final TransferHandler transferHandler;

    private final HashMap<UUID, TransferMetaData> incomingTransfers = new HashMap<>();
    private final HashSet<UUID> frozenPlayers = new HashSet<>();

    public ApiTransferService(GlmcServerLinkerBukkit plugin) {
        this.plugin = plugin;

        this.transferHandler = new TransferHandler();

        this.plugin.getGlmcApiBukkit().getPacketService().registerListener(transferHandler, this.plugin);

        PlayerBorderListener playerBorderListener = new PlayerBorderListener(this.plugin);
        PlayerJoinQuitListener playerJoinGuard = new PlayerJoinQuitListener(this.plugin, this);
        PlayerFreezeListener playerFreezeListener = new PlayerFreezeListener(this.plugin, this);
        TransferProcessListener transferProcessListener = new TransferProcessListener(this.plugin);
        this.plugin.getLogger().warning("234124312");

        OfflineDataListener offlineDataListener = new OfflineDataListener(this.plugin, this);
        TransferDataListener transferDataListener = new TransferDataListener(this.plugin, this);
        TransferUnfreezeListener transferUnfreezeListener =     new TransferUnfreezeListener(this.plugin, this);
        TransferDataRequestListener transferDataRequestListener = new TransferDataRequestListener(this.plugin, this);
        TransferApprovalListener transferApprovalRequest = new TransferApprovalListener(this.plugin, this);
    }

    @Override
    public CompletableFuture<TransferAPI.TransferResult> transferPlayer(UUID playerUniqueId, String serverTarget, TransferMetaData transferMetaData, TransferAPI.TransferReason transferReason, boolean force) {
        Objects.requireNonNull(playerUniqueId);
        Objects.requireNonNull(serverTarget);
        Objects.requireNonNull(transferMetaData);
        Objects.requireNonNull(transferReason);

        if (this.plugin.getGlmcAntylogout().hasActiveAntyLogout(playerUniqueId)) {
            this.plugin.getGlmcAntylogout().removeFromAntyLogout(playerUniqueId);
        }

        CompletableFuture<TransferAPI.TransferResult> result = new CompletableFuture<>();

        TransferRequest transferRequest = new TransferRequest(playerUniqueId, serverTarget, transferMetaData, transferReason, force);

        this.plugin.getLogger().warning(transferMetaData.getTransferMetaData().toString());
        this.transferHandler.create(transferRequest.getUniqueId())
                .thenAccept(transferResult -> {
                    this.plugin.getLogger().info(ChatColor.YELLOW + "Transferring " + playerUniqueId + " to " + serverTarget + " - result:" + transferResult);
                });

        this.plugin.getGlmcApiBukkit().getPacketService().sendPacket(transferRequest, "proxy");

        return result;
    }

    public void processJoin(Player player) { //todo server name and type in main api
        this.plugin.getDatabaseProvider().updateAsync(JOIN_QUIT_STATEMENT, player.getUniqueId().toString(), this.plugin.getGlmcApiBukkit().getServerId(), "hub", 1);

    }

    public void processDisconnect(Player player) { //todo server name and type in main api
        this.plugin.getDatabaseProvider().updateAsync(JOIN_QUIT_STATEMENT, player.getUniqueId().toString(), this.plugin.getGlmcApiBukkit().getServerId(), "hub", 0);

        boolean isFrozen = this.isFrozen(player.getUniqueId());

        if (isFrozen) {
            player.closeInventory(); //todo: prevent lead duplication by monitoring event
            player.setLeashHolder(null);

            player.releaseLeftShoulderEntity();
            player.releaseRightShoulderEntity();

            for (Entity passenger : player.getPassengers()) {
                player.removePassenger(passenger);
            }

           // if (!(player.getVehicle() instanceof Horse)) {
           //     player.leaveVehicle();
          //  }

            this.unfreeze(player.getUniqueId());
        }

        player.saveData();

        if (isFrozen && player.getVehicle() instanceof Horse horse) {
            for (Entity passenger : horse.getPassengers()) {
               // horse.removePassenger(passenger);
            }

           // horse.remove();
        }

        File playerFile = new File(this.plugin.getRootDirectory() + "/world/playerdata/", player.getUniqueId() + ".dat");
        try {
            NamedTag namedTag = NBTUtil.read(playerFile);
            CompoundTag playerTag = (CompoundTag) namedTag.getTag();

            for (String blacklistedTransferTag : this.plugin.getConfigData().getBlacklistedTransferTags()) {
                playerTag.remove(blacklistedTransferTag);
            }

            String playerData = SNBTUtil.toSNBT(playerTag);

            this.plugin.getDatabaseProvider().updateAsync(DATA_UPDATE_STATEMENT, Compression.compress(playerData), this.plugin.getGlmcApiBukkit().getServerId(), player.getUniqueId().toString());
        } catch (IOException e) {
            e.printStackTrace();

            this.plugin.getLogger().warning("Player " + player.getUniqueId() + " data update failed!");
        }
    }

    public void freezePlayer(UUID playerUniqueId) {
        if (this.plugin.getGlmcAntylogout().hasActiveAntyLogout(playerUniqueId)) {
            this.plugin.getGlmcAntylogout().removeFromAntyLogout(playerUniqueId);
        }

        this.frozenPlayers.add(playerUniqueId);
    }

    public void unfreeze(UUID playerUniqueId) {
        this.frozenPlayers.remove(playerUniqueId);
    }

    public void incomingTransfer(UUID playerUniqueId, TransferMetaData transferMetaData) {
        this.incomingTransfers.put(playerUniqueId, transferMetaData);

        this.plugin.getServer().getScheduler().runTaskLater(this.plugin, () -> {
            if (incomingTransfers.containsKey(playerUniqueId)) {
                this.incomingTransfers.remove(playerUniqueId);

                this.plugin.getLogger().warning("Transfer of player " + playerUniqueId + " was not completed (possible timeout)");
            }
        }, 20);
    }

    public void transferCompleted(UUID playerUniqueId) {
        this.incomingTransfers.remove(playerUniqueId);
    }

    public TransferMetaData getTransferMetaData(UUID playerUniqueId) {
        return this.incomingTransfers.get(playerUniqueId);
    }

    public boolean isTransferred(UUID playerUniqueId) {
        return this.incomingTransfers.containsKey(playerUniqueId);
    }

    public boolean isFrozen(UUID playerUniqueId) {
        return this.frozenPlayers.contains(playerUniqueId);
    }
}
