package pl.glmc.serverlinker.bukkit.api.transfer.listener.packet;

import net.querz.nbt.io.NBTUtil;
import net.querz.nbt.io.NamedTag;
import net.querz.nbt.io.SNBTUtil;
import net.querz.nbt.tag.CompoundTag;
import org.bukkit.entity.Player;
import pl.glmc.api.common.packet.listener.PacketListener;
import pl.glmc.serverlinker.api.common.TransferAPI;
import pl.glmc.serverlinker.bukkit.GlmcServerLinkerBukkit;
import pl.glmc.serverlinker.bukkit.api.transfer.ApiTransferService;
import pl.glmc.serverlinker.bukkit.config.ConfigData;
import pl.glmc.serverlinker.common.LocalPacketRegistry;
import pl.glmc.serverlinker.common.transfer.TransferData;
import pl.glmc.serverlinker.common.transfer.TransferDataRequest;
import pl.glmc.serverlinker.common.transfer.TransferDataResponse;

import java.io.File;
import java.io.IOException;

public class TransferDataRequestListener extends PacketListener<TransferDataRequest> {
    private final GlmcServerLinkerBukkit plugin;
    private final ApiTransferService transferService;

    public TransferDataRequestListener(GlmcServerLinkerBukkit plugin, ApiTransferService transferService) {
        super(LocalPacketRegistry.Transfer.TRANSFER_DATA_REQUEST, TransferDataRequest.class);

        this.plugin = plugin;
        this.transferService = transferService;

        this.plugin.getGlmcApiBukkit().getPacketService().registerListener(this);
    }

    @Override
    public void received(TransferDataRequest transferDataRequest) {
        this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> this.processTransferDataRequest(transferDataRequest));
    }

    private void processTransferDataRequest(TransferDataRequest transferDataRequest) {
        final Player player = this.plugin.getServer().getPlayer(transferDataRequest.getPlayerUniqueId());

        if (player == null || !player.isOnline()) {
            this.sendTransferDataResponse(transferDataRequest, TransferAPI.TransferDataResult.PLAYER_OFFLINE, false);

            return;
        }

        final ConfigData configData = this.plugin.getConfigProvider().getConfigData();

        this.transferService.freezePlayer(player.getUniqueId());

        this.plugin.getServer().getScheduler().runTask(this.plugin, (Runnable) player::closeInventory);

        player.saveData();

        File playerFile = new File(this.plugin.getRootDirectory() + "/world/playerdata/", player.getUniqueId() + ".dat");

        NamedTag namedTag;
        try {
            namedTag = NBTUtil.read(playerFile);
        } catch (IOException e) {
            this.sendTransferDataResponse(transferDataRequest, TransferAPI.TransferDataResult.CORRUPTED_DATA, false);

            e.printStackTrace();

            return;
        }

        CompoundTag playerTag = (CompoundTag) namedTag.getTag();
        CompoundTag optionalTag = new CompoundTag();

        for (String blacklistedTransferTag : configData.getBlacklistedTransferTags()) {
            playerTag.remove(blacklistedTransferTag);
        }

        for (String optionalTransferTag : configData.getOptionalTransferTags()) {
            if (playerTag.containsKey(optionalTransferTag)) {
                optionalTag.put(optionalTransferTag, playerTag.get(optionalTransferTag));

                playerTag.remove(optionalTransferTag);
            }
        }

        String playerData, optionalData;
        try {
            playerData = SNBTUtil.toSNBT(playerTag);
            optionalData = SNBTUtil.toSNBT(optionalTag);
        } catch (IOException e) {
            this.sendTransferDataResponse(transferDataRequest, TransferAPI.TransferDataResult.CORRUPTED_DATA, false);

            e.printStackTrace();

            return;
        }

        TransferData transferData = new TransferData(player.getUniqueId(), transferDataRequest.getUniqueId(), playerData, optionalData, transferDataRequest.getSender());

        this.plugin.getGlmcApiBukkit().getPacketService().sendPacket(transferData, transferDataRequest.getServerTarget());
    }

    private void sendTransferDataResponse(TransferDataRequest transferDataRequest, TransferAPI.TransferDataResult transferDataResult, boolean success) {
        TransferDataResponse transferDataResponse = new TransferDataResponse(success, transferDataRequest.getUniqueId(), transferDataResult);

        this.plugin.getGlmcApiBukkit().getPacketService().sendPacket(transferDataResponse, transferDataRequest.getSender());
    }
}
