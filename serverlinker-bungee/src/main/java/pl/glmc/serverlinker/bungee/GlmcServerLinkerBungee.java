package pl.glmc.serverlinker.bungee;

import com.google.gson.Gson;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.plugin.Plugin;
import pl.glmc.api.bungee.GlmcApiBungee;
import pl.glmc.api.bungee.GlmcApiBungeeProvider;
import pl.glmc.api.bungee.database.DatabaseProvider;
import pl.glmc.serverlinker.bungee.api.GlmcTransferProvider;
import pl.glmc.serverlinker.bungee.cmd.RTPCmd;
import pl.glmc.serverlinker.bungee.cmd.ServerCmd;
import pl.glmc.serverlinker.bungee.cmd.TestCmd;
import pl.glmc.serverlinker.bungee.conifg.ConfigProvider;

public class GlmcServerLinkerBungee extends Plugin {

    private GlmcTransferProvider glmcTransferProvider;
    private GlmcApiBungee glmcApiBungee;

    private ConfigProvider configProvider;
    private DatabaseProvider databaseProvider;

    private Gson gson;

    @Override
    public void onLoad() {
        this.configProvider = new ConfigProvider(this);
        this.databaseProvider = new DatabaseProvider(this, this.configProvider.getDatabaseConfig());

        this.gson = new Gson();
    }

    @Override
    public void onEnable() {
        try {
            this.glmcApiBungee = GlmcApiBungeeProvider.get();
        } catch (NullPointerException e) {
            this.getLogger().warning(ChatColor.RED + "Failed to load GLMCapi!");
        }

        this.glmcTransferProvider = new GlmcTransferProvider(this);

        ServerCmd serverCommand = new ServerCmd(this);
        RTPCmd rtpCommand = new RTPCmd(this);
        TestCmd testCmd = new TestCmd(this);
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

    public Gson getGson() {
        return gson;
    }
}
