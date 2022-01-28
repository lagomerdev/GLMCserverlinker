package pl.glmc.serverlinker.api.bungee;

public class GlmcTransferBungeeProvider {
    private static GlmcTransferBungee instance = null;

    public static GlmcTransferBungee get() {
        if (instance == null) {
            throw new NullPointerException("GLMCserverlinker is not loaded!");
        }

        return instance;
    }

    public static void register(GlmcTransferBungee instance) {
        GlmcTransferBungeeProvider.instance = instance;
    }

    public static void unregister() {
        GlmcTransferBungeeProvider.instance = null;
    }
}
