package pl.glmc.serverlinker.api.bukkit.transfer;

import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.Tag;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

public class TransferDataProcessEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();

    private final UUID playerUniqueId;
    private final CompoundTag optionalTag;

    private CompoundTag applyTag;

    public TransferDataProcessEvent(UUID playerUniqueId, CompoundTag optionalTag) {
        super(true);

        this.playerUniqueId = playerUniqueId;
        this.optionalTag = optionalTag;

        this.applyTag = new CompoundTag();
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

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
