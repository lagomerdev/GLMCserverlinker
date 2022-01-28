package pl.glmc.serverlinker.bukkit.config;

import net.md_5.bungee.api.ChatColor;
import pl.glmc.api.common.config.DatabaseConfig;
import pl.glmc.api.common.config.RedisConfig;
import pl.glmc.serverlinker.bukkit.GlmcServerLinkerBukkit;

import java.io.File;
import java.util.List;

public class ConfigProvider {

    private final GlmcServerLinkerBukkit plugin;

    private ConfigData configData;
    private DatabaseConfig databaseConfig;
    private RedisConfig redisConfig;

    public ConfigProvider(GlmcServerLinkerBukkit plugin) {
        this.plugin = plugin;

        this.loadFiles();
        
        try {
            this.loadConfig();
            this.loadDatabaseConfig();
            this.loadRedisConfig();
        } catch (NullPointerException exception) {
            exception.printStackTrace();

            this.plugin.getLogger().warning(ChatColor.RED + "Failed to load config!");
        }
    }

    private void loadFiles() {
        File configFile = new File(this.plugin.getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            this.plugin.saveDefaultConfig();
        }
    }

    private void loadDatabaseConfig() {
        final String host = this.plugin.getConfig().getString("connections.mysql.host");
        final String database = this.plugin.getConfig().getString("connections.mysql.database");
        final String username = this.plugin.getConfig().getString("connections.mysql.user");
        final String password = this.plugin.getConfig().getString("connections.mysql.password");
        final String poolName = this.plugin.getConfig().getString("connections.mysql.pool_name");
        final int port = this.plugin.getConfig().getInt("connections.mysql.port");
        final int maxPoolSize = this.plugin.getConfig().getInt("connections.mysql.max_pool_size");

        this.databaseConfig = new DatabaseConfig(host, database, username, password, poolName, port, maxPoolSize);
    }

    private void loadRedisConfig() {
        final String host = this.plugin.getConfig().getString("connections.redis.host");
        final String password = this.plugin.getConfig().getString("connections.redis.password");
        final int port = this.plugin.getConfig().getInt("connections.redis.port");
        final int timeout = this.plugin.getConfig().getInt("connections.redis.timeout");

        this.redisConfig = new RedisConfig(host, password, port, timeout);
    }

    private void loadConfig() {
        final List<String> blacklistedTransferTags = this.plugin.getConfig().getStringList("blacklisted_transfer_tags");
        final List<String> optionalTransferTags = this.plugin.getConfig().getStringList("optional_transfer_tags");
        final List<String> backupIgnoredTags = this.plugin.getConfig().getStringList("backup_ignored_tags");

        this.configData = new ConfigData(blacklistedTransferTags, optionalTransferTags, backupIgnoredTags);
    }

    public ConfigData getConfigData() {
        return configData;
    }

    public DatabaseConfig getDatabaseConfig() {
        return databaseConfig;
    }

    public RedisConfig getRedisConfig() {
        return redisConfig;
    }
}
