package pl.glmc.serverlinker.bungee.api.sector;

import pl.glmc.serverlinker.api.bungee.sector.SectorManager;
import pl.glmc.serverlinker.bungee.GlmcServerLinkerBungee;
import pl.glmc.serverlinker.common.sector.SectorData;
import pl.glmc.serverlinker.common.sector.SectorType;

import java.sql.SQLException;
import java.util.*;

public class ApiSectorManager implements SectorManager {
    private static final String GET_SECTORS = "SELECT * FROM `sector_info`";
    private static final String GET_SECTOR_TYPES = "SELECT * FROM `sector_types`";

    private final GlmcServerLinkerBungee plugin;

    private final HashMap<String, SectorData> sectors = new HashMap<>();
    private final HashMap<String, SectorType> sectorTypes = new HashMap<>();

    public ApiSectorManager(GlmcServerLinkerBungee plugin) {
        this.plugin = plugin;

        this.init();
        this.loadSectorTypes();
        this.loadSectors();
    }

    private void init() {
        final String sectorInfoStatement = "CREATE TABLE `sector_info` (" +
                "  `id` varchar(30) NOT NULL, " +
                "  `server_north` varchar(30) NOT NULL, " +
                "  `server_east` varchar(30) NOT NULL, " +
                "  `server_south` varchar(30) NOT NULL, " +
                "  `server_west` varchar(30) NOT NULL, " +
                "  `min_x` int NOT NULL, " +
                "  `min_z` int NOT NULL, " +
                "  `max_x` int NOT NULL, " +
                "  `max_z` int NOT NULL, " +
                "  `type` varchar(30) NOT NULL, " +
                "  `view_distance` int NOT NULL, " +
                "  `simulation_distance` int NOT NULL, " +
                "  PRIMARY KEY (`id`) " +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";

        final String sectorTypesStatement = "CREATE TABLE `sector_types` ( " +
                "  `id` varchar(30) NOT NULL, " +
                "  `world_name` varchar(30) NOT NULL, " +
                "  `has_spawn` tinyint(1) NOT NULL, " +
                "  `spawn_x` int DEFAULT NULL, " +
                "  `spawn_y` int DEFAULT NULL, " +
                "  `spawn_z` int DEFAULT NULL, " +
                "  `has_weather_and_time_sync` tinyint(1) NOT NULL, " +
                "  `weather_and_time_provider` varchar(30) DEFAULT NULL, " +
                "  PRIMARY KEY (`id`) " +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";

        this.plugin.getDatabaseProvider().updateSync(sectorInfoStatement);
        this.plugin.getDatabaseProvider().updateSync(sectorTypesStatement);
    }

    private void loadSectorTypes() {
        var resultSet = this.plugin.getDatabaseProvider().getSync(GET_SECTOR_TYPES);

        try {
            while (resultSet.next()) {
                String sectorTypeId = resultSet.getString("id");
                String worldName = resultSet.getString("world_name");
                boolean hasSpawn = resultSet.getBoolean("has_spawn");
                boolean hasWeatherAndTimeSync = resultSet.getBoolean("has_weather_and_time_sync");

                SectorType sectorType = new SectorType(sectorTypeId, worldName, hasSpawn, hasWeatherAndTimeSync);

                if (hasSpawn) {
                    int x = resultSet.getInt("spawn_x");
                    int y = resultSet.getInt("spawn_y");
                    int z = resultSet.getInt("spawn_z");

                    sectorType.setSpawn(x, y, z);
                }

                if (hasWeatherAndTimeSync) {
                    String weatherAndTimeSyncProvider = resultSet.getString("weather_and_time_provider");

                    sectorType.setWeatherAndTimeProvider(weatherAndTimeSyncProvider);
                }

                this.sectorTypes.put(sectorTypeId, sectorType);
            }
        } catch (SQLException | NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void loadSectors() {
        var resultSet = this.plugin.getDatabaseProvider().getSync(GET_SECTORS);

        try {
            while (resultSet.next()) {
                String sectorId = resultSet.getString("id");
                String serverNorth = resultSet.getString("server_north");
                String serverEast = resultSet.getString("server_east");
                String serverSouth = resultSet.getString("server_south");
                String serverWest = resultSet.getString("server_west");
                //int distanceCheck = resultSet.getInt("distance_check");
                double minX = resultSet.getDouble("min_x");
                double minZ = resultSet.getDouble("min_z");
                double maxX = resultSet.getDouble("max_x");
                double maxZ = resultSet.getDouble("max_z");
                int viewDistance = resultSet.getInt("view_distance");
                int simulationDistance = resultSet.getInt("simulation_distance");

                String sectorTypeId = resultSet.getString("type");
                SectorType sectorType = this.sectorTypes.get(sectorTypeId);

                var sectorData = new SectorData(sectorId, serverNorth, serverEast, serverSouth, serverWest, minX, minZ, maxX, maxZ, viewDistance, simulationDistance, sectorType);

                this.sectors.put(sectorId, sectorData);
            }
        } catch (SQLException | NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Map<String, SectorData> getSectors() {
        return Collections.unmodifiableMap(sectors);
    }

    @Override
    public Map<String, SectorType> getSectorTypes() {
        return Collections.unmodifiableMap(sectorTypes);
    }

    public boolean isIn(SectorData sector, double x, double z) {
        return x > sector.getMinX()
                && x < sector.getMaxX()
                && z > sector.getMinZ()
                && z < sector.getMaxZ();
    }

    public Optional<SectorData> getSectorFromLocation(SectorType sectorType, double x, double z) {
        return this.sectors.values()
                .stream()
                .filter(sectorData -> sectorData.getSectorType().getId().equals(sectorType.getId()) && isIn(sectorData, x, z))
                .findFirst();
    }

    public boolean isServerSector(String serverId) {
        return this.sectors.values()
                .stream()
                .anyMatch(sectorData -> sectorData.getId().equals(serverId));
    }
}
