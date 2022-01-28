package pl.glmc.serverlinker.bukkit.api.transfer.listener.event;

import com.google.gson.Gson;
import net.querz.nbt.tag.DoubleTag;
import net.querz.nbt.tag.ListTag;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import pl.glmc.serverlinker.api.bukkit.transfer.TransferProcessEvent;
import pl.glmc.serverlinker.api.common.TransferLocation;
import pl.glmc.serverlinker.api.common.TransferMetaKey;
import pl.glmc.serverlinker.bukkit.GlmcServerLinkerBukkit;

import java.util.UUID;

public class TransferProcessListener implements Listener {
    private final GlmcServerLinkerBukkit plugin;

    public TransferProcessListener(GlmcServerLinkerBukkit plugin) {
        this.plugin = plugin;

        this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);

        this.plugin.getLogger().warning("1");
    }

    @EventHandler
    public void onTransferProcess(TransferProcessEvent transferProcess) {
         if (transferProcess.getTransferMetaData().hasItem(TransferMetaKey.TELEPORT_ON_COORDS)) {
            var transferLocation = this.plugin.getGson().fromJson(transferProcess.getTransferMetaData().getItem(TransferMetaKey.TELEPORT_ON_COORDS), TransferLocation.class);
            var optionalTag = transferProcess.getOptionalTag();
            var newPosTag = optionalTag.getListTag("Pos");
            newPosTag.clear();

            newPosTag.addDouble(transferLocation.getX() + 0.5);
            newPosTag.addDouble(transferLocation.getY());
            newPosTag.addDouble(transferLocation.getZ() + 0.5);

            transferProcess.getApplyTag().put("Pos", newPosTag);
            transferProcess.getApplyTag().put("Rotation", optionalTag.get("Rotation"));

            this.plugin.getLogger().warning(newPosTag.getClass().getName());
        } else if (transferProcess.getTransferMetaData().hasItem(TransferMetaKey.TELEPORT_TO_PLAYER)) {
            UUID playerTargetUniqueId = UUID.fromString(transferProcess.getTransferMetaData().getItem(TransferMetaKey.TELEPORT_TO_PLAYER));

            Player playerTarget = this.plugin.getServer().getPlayer(playerTargetUniqueId);

            if (playerTarget == null || !playerTarget.isOnline()) {
                transferProcess.setCancelProcessing(true);

                return;
            }
            var locationTarget = playerTarget.getLocation();

            var optionalTag = transferProcess.getOptionalTag();
            var newPosTag = optionalTag.getListTag("Pos");
            var newRotationTag = optionalTag.getListTag("Rotation");
            newPosTag.clear();
            newRotationTag.clear();

            newPosTag.addDouble(locationTarget.getBlock().getX() + 0.5);
            newPosTag.addDouble(locationTarget.getBlockY()); //todo: make it use World#highestBlockAt
            newPosTag.addDouble(locationTarget.getZ() + 0.5);

            newRotationTag.addFloat(locationTarget.getBlockY());
            newRotationTag.addFloat(locationTarget.getPitch());

            this.plugin.getLogger().warning(newPosTag.toString());
            this.plugin.getLogger().warning(optionalTag.get("Rotation").toString());
            transferProcess.getApplyTag().put("Pos", newPosTag);
            transferProcess.getApplyTag().put("Rotation", optionalTag.get("Rotation"));
        } else {
             var optionalTag = transferProcess.getOptionalTag();
             if (optionalTag.containsKey("Pos") && optionalTag.containsKey("Rotation")) {
                 transferProcess.putTag("Pos", optionalTag.get("Pos"));
                 transferProcess.putTag("Rotation", optionalTag.get("Rotation"));
             }
         }
    }
}
