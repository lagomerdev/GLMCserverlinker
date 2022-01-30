package pl.glmc.serverlinker.api.bungee.sector;

import pl.glmc.serverlinker.common.sector.SectorData;
import pl.glmc.serverlinker.common.sector.SectorType;

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
    public boolean isIn(SectorData sector, double x, double z);

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
}
