package pl.glmc.serverlinker.api.common.sector;

public class SectorType {

    private final String id;

    private String worldName;
    private boolean hasSpawn, hasWeatherAndTimeSync;

    //nullable
    private int spawnX, spawnY, spawnZ;
    private String weatherAndTimeProvider;

    public SectorType(String id, String worldName, boolean hasSpawn, boolean hasWeatherAndTimeSync) {
        this.id = id;

        this.worldName = worldName;
        this.hasSpawn = hasSpawn;
        this.hasWeatherAndTimeSync = hasWeatherAndTimeSync;
    }

    public String getId() {
        return id;
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

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof SectorType sectorType)) return false;

        return this.id.equals(sectorType.getId());
    }

    @Override
    public String toString() {
        return "SectorType{" +
                "id='" + id + '\'' +
                ", worldName='" + worldName + '\'' +
                ", hasSpawn=" + hasSpawn +
                ", hasWeatherAndTimeSync=" + hasWeatherAndTimeSync +
                ", spawnX=" + spawnX +
                ", spawnY=" + spawnY +
                ", spawnZ=" + spawnZ +
                ", weatherAndTimeProvider='" + weatherAndTimeProvider + '\'' +
                '}';
    }
}
