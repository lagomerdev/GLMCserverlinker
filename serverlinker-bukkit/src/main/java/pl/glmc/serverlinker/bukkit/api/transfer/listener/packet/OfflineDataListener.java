package pl.glmc.serverlinker.bukkit.api.transfer.listener.packet;

import net.querz.nbt.io.NBTUtil;
import net.querz.nbt.io.NamedTag;
import net.querz.nbt.io.SNBTUtil;
import net.querz.nbt.tag.CompoundTag;
import pl.glmc.api.common.packet.listener.PacketListener;
import pl.glmc.serverlinker.bukkit.GlmcServerLinkerBukkit;
import pl.glmc.serverlinker.bukkit.api.transfer.ApiTransferService;
import pl.glmc.serverlinker.common.LocalPacketRegistry;
import pl.glmc.serverlinker.common.transfer.OfflineDataRequest;
import pl.glmc.serverlinker.common.transfer.OfflineDataResponse;

import java.io.File;
import java.io.IOException;

public class OfflineDataListener extends PacketListener<OfflineDataRequest> {

    private final GlmcServerLinkerBukkit plugin;
    private final ApiTransferService transferService;

    public OfflineDataListener(GlmcServerLinkerBukkit plugin, ApiTransferService transferService) {
        super(LocalPacketRegistry.Transfer.OFFLINE_DATA_REQUEST, OfflineDataRequest.class);

        this.plugin = plugin;
        this.transferService = transferService;

        this.plugin.getGlmcApiBukkit().getPacketService().registerListener(this, this.plugin);
    }

    @Override
    public void received(OfflineDataRequest offlineDataRequest) {
        File playerFile = new File(this.plugin.getRootDirectory() + "/world/playerdata/", offlineDataRequest.getPlayerUniqueId() + ".dat");

        String playerData = null;
        boolean success;

        if (playerFile.exists()) {
            try {
                NamedTag namedTag = NBTUtil.read(playerFile);
                CompoundTag playerTag = (CompoundTag) namedTag.getTag();

                for (String blacklistedTransferTag : this.plugin.getConfigData().getBlacklistedTransferTags()) {
                    playerTag.remove(blacklistedTransferTag);
                }

                playerData = SNBTUtil.toSNBT(playerTag);
                success = true;
            } catch (IOException e) {
                e.printStackTrace();

                success = false;
            }
        } else {
            success = false;
        }

        OfflineDataResponse offlineDataResponse = new OfflineDataResponse(success, offlineDataRequest.getUniqueId(), playerData);

        this.plugin.getGlmcApiBukkit().getPacketService().sendPacket(offlineDataResponse, offlineDataRequest.getSender());
    }
}
