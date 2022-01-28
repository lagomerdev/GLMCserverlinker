package pl.glmc.serverlinker.bungee.api.sector;

import pl.glmc.serverlinker.api.bungee.sector.SectorManager;
import pl.glmc.serverlinker.bungee.GlmcServerLinkerBungee;

public class ApiSectorManager implements SectorManager {

    private final GlmcServerLinkerBungee plugin;

    public ApiSectorManager(GlmcServerLinkerBungee plugin) {
        this.plugin = plugin;

        this.init();
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
}
