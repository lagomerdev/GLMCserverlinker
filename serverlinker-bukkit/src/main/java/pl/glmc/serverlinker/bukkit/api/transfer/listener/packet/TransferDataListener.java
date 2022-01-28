package pl.glmc.serverlinker.bukkit.api.transfer.listener.packet;

import net.querz.nbt.io.NBTUtil;
import net.querz.nbt.io.SNBTUtil;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.LongTag;
import net.querz.nbt.tag.StringTag;
import net.querz.nbt.tag.Tag;
import org.bukkit.ChatColor;
import org.bukkit.World;
import pl.glmc.api.common.packet.listener.PacketListener;
import pl.glmc.serverlinker.api.bukkit.transfer.TransferProcessEvent;
import pl.glmc.serverlinker.api.common.TransferAPI;
import pl.glmc.serverlinker.api.common.TransferMetaData;
import pl.glmc.serverlinker.bukkit.GlmcServerLinkerBukkit;
import pl.glmc.serverlinker.bukkit.api.transfer.ApiTransferService;
import pl.glmc.serverlinker.common.LocalPacketRegistry;
import pl.glmc.serverlinker.common.transfer.TransferData;
import pl.glmc.serverlinker.common.transfer.TransferDataResponse;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

public class TransferDataListener extends PacketListener<TransferData> {
    private final GlmcServerLinkerBukkit plugin;
    private final ApiTransferService transferService;
    private final CompoundTag defaultPlayerdata;

    public TransferDataListener(GlmcServerLinkerBukkit plugin, ApiTransferService transferService) {
        super(LocalPacketRegistry.Transfer.TRANSFER_DATA, TransferData.class);

        this.plugin = plugin;
        this.transferService = transferService;

        World world = this.plugin.getServer().getWorld("world");
        assert world != null;

        this.defaultPlayerdata = new CompoundTag();
        this.defaultPlayerdata.put("WorldUUIDMost", new LongTag(world.getUID().getMostSignificantBits()));
        this.defaultPlayerdata.put("WorldUUIDLeast", new LongTag(world.getUID().getLeastSignificantBits()));
        this.defaultPlayerdata.put("Dimension", new StringTag("minecraft:overworld"));

        this.plugin.getGlmcApiBukkit().getPacketService().registerListener(this, this.plugin);
    }


    @Override
    public void received(TransferData transferData) {
        this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> this.processTransferData(transferData));
    }

    private void processTransferData(TransferData transferData) {
        if (!this.transferService.isTransferred(transferData.getPlayerUniqueId())) {
            this.plugin.getLogger().warning(ChatColor.RED + "Received unexpected transfer data for player " + transferData.getPlayerData() + " from " + transferData.getOriginSender());

            return;
        }

        CompoundTag playerDataTag;
        CompoundTag optionalDataTag;
        try {
            playerDataTag = (CompoundTag) SNBTUtil.fromSNBT(transferData.getPlayerData());
            optionalDataTag = transferData.getOptionalData().isEmpty() ? new CompoundTag() : (CompoundTag) SNBTUtil.fromSNBT(transferData.getOptionalData());
        } catch (IOException exception) {
            this.sendTransferDataResponse(transferData, TransferAPI.TransferDataResult.CORRUPTED_DATA, false);

            return;
        }

        UUID playerUniqueId = transferData.getPlayerUniqueId();

        for (String defaultTagKey : defaultPlayerdata.keySet()) {
            if (!playerDataTag.containsKey(defaultTagKey)) {
                playerDataTag.put(defaultTagKey, defaultPlayerdata.get(defaultTagKey));
            }
        }

        TransferMetaData transferMetaData = this.transferService.getTransferMetaData(playerUniqueId);
        TransferProcessEvent playerDataProcessEvent = new TransferProcessEvent(playerUniqueId, transferMetaData, optionalDataTag);
        this.plugin.getServer().getPluginManager().callEvent(playerDataProcessEvent);

        if (playerDataProcessEvent.isCanceledProcessing()) {
            this.sendTransferDataResponse(transferData, TransferAPI.TransferDataResult.PROCESS_CANCELED, false);

            return;
        }

        CompoundTag applyTag = playerDataProcessEvent.getApplyTag();
        for (Map.Entry<String, Tag<?>> applyEntry : applyTag.entrySet()) {
            playerDataTag.put(applyEntry.getKey(), applyEntry.getValue());
        }

        File playerFile = new File(this.plugin.getRootDirectory() + "/world/playerdata/", playerUniqueId + ".dat");

        try {
            NBTUtil.write(playerDataTag, playerFile);
        } catch (IOException exception) {
            this.sendTransferDataResponse(transferData, TransferAPI.TransferDataResult.SAVING_FAILED, false);

            return;
        }

        this.sendTransferDataResponse(transferData, TransferAPI.TransferDataResult.RECEIVED, true);
    }

    private void sendTransferDataResponse(TransferData transferData, TransferAPI.TransferDataResult transferDataResult, boolean success) {
        TransferDataResponse transferDataResponse = new TransferDataResponse(success, transferData.getOriginPacketUniqueId(), transferDataResult);

        this.plugin.getGlmcApiBukkit().getPacketService().sendPacket(transferDataResponse, transferData.getOriginSender());
    }
}
