package pl.glmc.serverlinker.api.bungee.sector;

import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import pl.glmc.serverlinker.api.common.sector.SectorData;
import pl.glmc.serverlinker.api.common.sector.SectorType;

import java.util.Map;
import java.util.Optional;

public interface SectorManager {

    /**
     *
     * @return
     */
    public Map<String, SectorData> getSectors();

    /**
     *
     * @return
     */
    public Map<String, SectorType> getSectorTypes();

    /**
     *
     * @param sector
     * @param x
     * @param z
     * @return
     */
    public boolean isInSector(SectorData sector, double x, double z);

    /**
     *
     * @param sector
     * @param x
     * @param z
     * @return
     */
    public boolean isInSectorType(SectorType sectorType, double x, double z);

    /**
     *
     * @param sectorType
     * @param x
     * @param z
     * @return
     */
    public Optional<SectorData> getSectorFromLocation(SectorType sectorType, double x, double z);

    /**
     *
     * @param serverId
     * @return
     */
    public boolean isServerSector(String serverId);

    /**
     *
     * @param player
     * @return
     */
    public SectorData getSectorData(ProxiedPlayer player);

    /**
     *
     * @param serverInfo
     * @return
     */
    public SectorData getSectorData(ServerInfo serverInfo);

    /**
     *
     * @param server
     * @return
     */
    public SectorData getSectorData(String server);
}
