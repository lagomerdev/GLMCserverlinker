package pl.glmc.serverlinker.bukkit.api.sector;

import org.bukkit.World;
import pl.glmc.serverlinker.api.bukkit.sector.SectorManager;
import pl.glmc.serverlinker.api.common.sector.SectorType;
import pl.glmc.serverlinker.bukkit.GlmcServerLinkerBukkit;
import pl.glmc.serverlinker.api.common.sector.SectorData;

import java.sql.SQLException;
import java.util.Objects;

public class ApiSectorManager implements SectorManager {
    private static final String GET_SECTOR_INFO = "SELECT * FROM `sector_info` WHERE id = ?";
    private static final String GET_SECTOR_TYPE = "SELECT * FROM `sector_types` WHERE id = ?";

    private final GlmcServerLinkerBukkit plugin;

    private World world;

    public static SectorData sectorData;

    public ApiSectorManager(GlmcServerLinkerBukkit plugin) {
        this.plugin = plugin;

        sectorData = this.loadSectorData();
    }

    private SectorData loadSectorData() {
        var resultSet = this.plugin.getDatabaseProvider().getSync(GET_SECTOR_INFO, this.plugin.getGlmcApiBukkit().getServerId());

        try {
            if (resultSet.next()) {
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
                SectorType sectorType = this.loadSectorType(sectorTypeId);

                return new SectorData(this.plugin.getGlmcApiBukkit().getServerId(), serverNorth, serverEast, serverSouth, serverWest, minX, minZ, maxX, maxZ, viewDistance, simulationDistance, sectorType);
            }
        } catch (SQLException | NullPointerException e) {
            e.printStackTrace();
        }

        throw new NullPointerException("Could not load SectorData");
    }

    private SectorType loadSectorType(String sectorTypeId) throws SQLException {
        var resultSet = this.plugin.getDatabaseProvider().getSync(GET_SECTOR_TYPE, sectorTypeId);

        if (resultSet.next()) {
            String worldName = resultSet.getString("world_name");

            this.world = Objects.requireNonNull(this.plugin.getServer().getWorld(worldName));

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

            return sectorType;
        } else {
            throw new NullPointerException("Sector type does not exists");
        }
    }

    public SectorData getSectorData() {
        return sectorData;
    }

    @Override
    public World getWorld() {
        return this.world;
    }
}
