package pl.glmc.serverlinker.api.bukkit.transfer;

import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.Tag;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import pl.glmc.serverlinker.api.common.TransferMetaData;

import java.util.UUID;

public class TransferProcessEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();

    private final UUID playerUniqueId;
    private final TransferMetaData transferMetaData;
    private final CompoundTag optionalTag;

    private boolean cancelProcessing;

    private CompoundTag applyTag;

    public TransferProcessEvent(UUID playerUniqueId, TransferMetaData transferMetaData, CompoundTag optionalTag) {
        super(true);

        this.playerUniqueId = playerUniqueId;
        this.transferMetaData = transferMetaData;
        this.optionalTag = optionalTag;

        this.cancelProcessing = false;

        this.applyTag = new CompoundTag();
    }

    public TransferMetaData getTransferMetaData() {
        return transferMetaData;
    }

    public UUID getPlayerUniqueId() {
        return playerUniqueId;
    }

    public CompoundTag getOptionalTag() {
        return optionalTag;
    }

    public CompoundTag getApplyTag() {
        return applyTag;
    }

    public void putTag(String key, Tag<?> tag) {
        this.applyTag.put(key, tag);
    }

    public void setCancelProcessing(boolean cancelProcessing) {
        this.cancelProcessing = cancelProcessing;
    }

    public boolean isCanceledProcessing() {
        return cancelProcessing;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
