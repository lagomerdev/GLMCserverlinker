package pl.glmc.serverlinker.bungee.conifg;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import pl.glmc.api.common.config.DatabaseConfig;
import pl.glmc.api.common.config.RedisConfig;
import pl.glmc.serverlinker.bungee.GlmcServerLinkerBungee;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;

public class ConfigProvider {

    private final GlmcServerLinkerBungee plugin;

    private Configuration configuration;
    private ConfigData configData;
    private DatabaseConfig databaseConfig;
    private RedisConfig redisConfig;

    public ConfigProvider(final GlmcServerLinkerBungee plugin) {
        this.plugin = plugin;

        try {
            this.init();

            this.loadConfig();
            this.loadDatabaseConfig();
            this.loadRedisConfig();
        } catch (NullPointerException exception) {
            exception.printStackTrace();

            this.plugin.getLogger().warning(ChatColor.RED + "Failed to load config!");
        }
    }

    private void init() {
        try {
            if (!this.plugin.getDataFolder().exists()) {
                this.plugin.getDataFolder().mkdir();
            }

            final File configFile = new File(this.plugin.getDataFolder(), "config.yml");
            if (!configFile.exists()) {
                this.saveDefaultConfig(configFile);
            }

            this.configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(this.plugin.getDataFolder(), "config.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadDatabaseConfig() {
        final String host = this.configuration.getString("connections.mysql.host");
        final String database = this.configuration.getString("connections.mysql.database");
        final String username = this.configuration.getString("connections.mysql.user");
        final String password = this.configuration.getString("connections.mysql.password");
        final int port = this.configuration.getInt("connections.mysql.port");
        final int maxPoolSize = this.configuration.getInt("connections.mysql.max_pool_size");

        this.databaseConfig = new DatabaseConfig(host, database, username, password, port, maxPoolSize);
    }

    private void loadRedisConfig() {
        final String host = this.configuration.getString("connections.redis.host");
        final String password = this.configuration.getString("connections.redis.password");
        final int port = this.configuration.getInt("connections.redis.port");
        final int timeout = this.configuration.getInt("connections.redis.timeout");

        this.redisConfig = new RedisConfig(host, password, port, timeout);
    }

    private void loadConfig() {
        final List<String> blacklistedTransferTags = this.configuration.getStringList("blacklisted_transfer_tags");
        final List<String> optionalTransferTags = this.configuration.getStringList("optional_transfer_tags");
        final List<String> backupIgnoredTags = this.configuration.getStringList("backup_ignored_tags");

        this.configData = new ConfigData(blacklistedTransferTags, optionalTransferTags, backupIgnoredTags);
    }

    private void saveDefaultConfig(File configFile) {
        try (InputStream inputStream = this.plugin.getResourceAsStream("config.yml")) {
            Files.copy(inputStream, configFile.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveConfig() {
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(this.configuration, new File(this.plugin.getDataFolder(), "config.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Configuration getConfig() {
        return this.configuration;
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
