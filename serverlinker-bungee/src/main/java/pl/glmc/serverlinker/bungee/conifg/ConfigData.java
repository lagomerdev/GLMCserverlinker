package pl.glmc.serverlinker.bungee.conifg;

import java.util.List;

public class ConfigData {

    private final List<String> blacklistedTransferTags, optionalTransferTags, backupIgnoredTags;

    public ConfigData(List<String> blacklistedTransferTags, List<String> optionalTransferTags, List<String> backupIgnoredTags) {
        this.blacklistedTransferTags = blacklistedTransferTags;
        this.optionalTransferTags = optionalTransferTags;
        this.backupIgnoredTags = backupIgnoredTags;
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
