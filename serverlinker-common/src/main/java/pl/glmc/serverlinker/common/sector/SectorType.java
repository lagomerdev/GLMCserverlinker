package pl.glmc.serverlinker.common.sector;

public class SectorType {

    private String worldName;
    private boolean hasSpawn, hasWeatherAndTimeSync;

    //nullable
    private int spawnX, spawnY, spawnZ;
    private String weatherAndTimeProvider;

    public SectorType(String worldName, boolean hasSpawn, boolean hasWeatherAndTimeSync) {
        this.worldName = worldName;
        this.hasSpawn = hasSpawn;
        this.hasWeatherAndTimeSync = hasWeatherAndTimeSync;
    }

    public void setSpawn(int spawnX, int spawnY, int spawnZ) {
        this.spawnX = spawnX;
        this.spawnY = spawnY;
        this.spawnZ = spawnZ;
    }

    public void setWeatherAndTimeProvider(String weatherAndTimeProvider) {
        this.weatherAndTimeProvider = weatherAndTimeProvider;
    }

    public String getWorldName() {
        return worldName;
    }

    public boolean hasSpawn() {
        return hasSpawn;
    }

    public boolean hasWeatherAndTimeSync() {
        return hasWeatherAndTimeSync;
    }

    public int getSpawnX() {
        return spawnX;
    }

    public int getSpawnY() {
        return spawnY;
    }

    public int getSpawnZ() {
        return spawnZ;
    }

    public String getWeatherAndTimeProvider() {
        return weatherAndTimeProvider;
    }
}
