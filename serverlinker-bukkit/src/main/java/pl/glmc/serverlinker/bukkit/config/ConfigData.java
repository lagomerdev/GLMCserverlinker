package pl.glmc.serverlinker.bukkit.config;

import java.util.List;

public class ConfigData {

    private final String serverId;
    private final List<String> blacklistedTransferTags, optionalTransferTags, backupIgnoredTags;

    public ConfigData(String serverId, List<String> blacklistedTransferTags, List<String> optionalTransferTags, List<String> backupIgnoredTags) {
        this.serverId = serverId;
        this.blacklistedTransferTags = blacklistedTransferTags;
        this.optionalTransferTags = optionalTransferTags;
        this.backupIgnoredTags = backupIgnoredTags;
    }

    public String getServerId() {
        return serverId;
    }

    public List<String> getBlacklistedTransferTags() {
        return blacklistedTransferTags;
    }

    public List<String> getOptionalTransferTags() {
        return optionalTransferTags;
    }

    public List<String> getBackupIgnoredTags() {
        return backupIgnoredTags;
    }
}
