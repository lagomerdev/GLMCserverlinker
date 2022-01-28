package pl.glmc.serverlinker.bungee;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.plugin.Plugin;
import pl.glmc.api.bungee.GlmcApiBungee;
import pl.glmc.api.bungee.GlmcApiBungeeProvider;
import pl.glmc.api.bungee.database.DatabaseProvider;
import pl.glmc.serverlinker.bungee.api.GlmcTransferProvider;
import pl.glmc.serverlinker.bungee.cmd.ServerConfig;
import pl.glmc.serverlinker.bungee.conifg.ConfigProvider;

public class GlmcServerLinkerBungee extends Plugin {

    private GlmcTransferProvider glmcTransferProvider;
    private GlmcApiBungee glmcApiBungee;

    private ConfigProvider configProvider;
    private DatabaseProvider databaseProvider;

    @Override
    public void onLoad() {
        this.configProvider = new ConfigProvider(this);
        this.databaseProvider = new DatabaseProvider(this, this.configProvider.getDatabaseConfig());
    }

    @Override
    public void onEnable() {
        try {
            this.glmcApiBungee = GlmcApiBungeeProvider.get();
        } catch (NullPointerException e) {
            this.getLogger().warning(ChatColor.RED + "Failed to load GLMCapi!");
        }

        this.glmcTransferProvider = new GlmcTransferProvider(this);

        ServerConfig serverCommand = new ServerConfig(this);
    }

    @Override
    public void onDisable() {
        this.glmcApiBungee.unload(this);

        this.databaseProvider.unload();
    }

    public GlmcApiBungee getGlmcApiBungee() {
        return glmcApiBungee;
    }

    public ConfigProvider getConfigProvider() {
        return configProvider;
    }

    public DatabaseProvider getDatabaseProvider() {
        return databaseProvider;
    }

    public GlmcTransferProvider getGlmcTransferProvider() {
        return glmcTransferProvider;
    }
}
