package pl.glmc.serverlinker.api.bukkit;

public class GlmcTransferBukkitProvider {
    private static GlmcTransferBukkit instance = null;

    public static GlmcTransferBukkit get() {
        if (instance == null) {
            throw new NullPointerException("GLMCserverlinker is not loaded!");
        }

        return instance;
    }

    public static void register(GlmcTransferBukkit instance) {
        GlmcTransferBukkitProvider.instance = instance;
    }

    public static void unregister() {
        GlmcTransferBukkitProvider.instance = null;
    }
}
