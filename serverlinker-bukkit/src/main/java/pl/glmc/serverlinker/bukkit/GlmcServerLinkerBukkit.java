package pl.glmc.serverlinker.bukkit;

import com.google.gson.Gson;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import pl.glmc.api.bukkit.GlmcApiBukkit;
import pl.glmc.api.bukkit.GlmcApiBukkitProvider;
import pl.glmc.api.bukkit.database.DatabaseProvider;
import pl.glmc.serverlinker.bukkit.api.GlmcTransferProvider;
import pl.glmc.serverlinker.bukkit.config.ConfigData;
import pl.glmc.serverlinker.bukkit.config.ConfigProvider;

public class GlmcServerLinkerBukkit extends JavaPlugin {

    private GlmcTransferProvider glmcTransferProvider;
    private GlmcApiBukkit glmcApiBukkit;

    private String rootDirectory;
    private Gson gson;

    private ConfigProvider configProvider;
    private DatabaseProvider databaseProvider;

    @Override
    public void onLoad() {
        this.configProvider = new ConfigProvider(this);
        this.databaseProvider = new DatabaseProvider(this, this.configProvider.getDatabaseConfig());

        this.rootDirectory = this.getServer().getWorldContainer().getAbsolutePath();

        this.gson = new Gson();
    }

    @Override
    public void onEnable() {
        try {
            this.glmcApiBukkit = GlmcApiBukkitProvider.get();
        } catch (NullPointerException e) {
            this.getLogger().warning(ChatColor.RED + "Failed to load GLMCapi!");
        }

        this.glmcTransferProvider = new GlmcTransferProvider(this);
    }

    @Override
    public void onDisable() {
        this.glmcApiBukkit.unload(this);

        this.databaseProvider.unload();
    }

    public ConfigProvider getConfigProvider() {
        return configProvider;
    }

    public ConfigData getConfigData() {
        return this.configProvider.getConfigData();
    }

    public DatabaseProvider getDatabaseProvider() {
        return databaseProvider;
    }

    public String getRootDirectory() {
        return rootDirectory;
    }

    public GlmcTransferProvider getGlmcTransferProvider() {
        return glmcTransferProvider;
    }

    public GlmcApiBukkit getGlmcApiBukkit() {
        return glmcApiBukkit;
    }

    public Gson getGson() {
        return gson;
    }
}
