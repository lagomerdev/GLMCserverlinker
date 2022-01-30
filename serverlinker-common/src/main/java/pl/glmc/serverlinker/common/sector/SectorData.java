package pl.glmc.serverlinker.common.sector;

import java.util.Arrays;
import java.util.List;

public class SectorData {

    private final String id;

    private String serverNorth, serverEast, serverSouth, serverWest;
    private double minX, minZ, maxX, maxZ;
    private int viewDistance, simulationDistance;

    private SectorType sectorType;

    public SectorData(String id, String serverNorth, String serverEast, String serverSouth, String serverWest, double minX, double minZ, double maxX, double maxZ, int viewDistance, int simulationDistance, SectorType sectorType) {
        this.id = id;
        this.serverNorth = serverNorth;
        this.serverEast = serverEast;
        this.serverSouth = serverSouth;
        this.serverWest = serverWest;
        this.minX = minX;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxZ = maxZ;
        this.viewDistance = viewDistance;
        this.simulationDistance = simulationDistance;
        this.sectorType = sectorType;
    }

    public void calibrateBorderDistance() {
        if (maxX > minX) {
            this.minX -= 0.5;
            this.maxX += 1.5;
        } else {
            this.maxX -= 0.5;
            this.minX += 1.5;
        }

        if (maxZ > minZ) {
            this.minZ -= 0.5;
            this.maxZ += 1.5;
        } else {
            this.minZ += 1.5;
            this.maxZ -= 0.5;
        }

        System.out.println(Arrays.toString(List.of(minX, minZ, maxX, maxZ).toArray()));
    }

    public String getId() {
        return id;
    }

    public String getServerNorth() {
        return serverNorth;
    }

    public String getServerEast() {
        return serverEast;
    }

    public String getServerSouth() {
        return serverSouth;
    }

    public String getServerWest() {
        return serverWest;
    }

    public double getMinX() {
        return minX;
    }

    public double getMinZ() {
        return minZ;
    }

    public double getMaxX() {
        return maxX;
    }

    public double getMaxZ() {
        return maxZ;
    }

    public int getViewDistance() {
        return viewDistance;
    }

    public int getSimulationDistance() {
        return simulationDistance;
    }

    public SectorType getSectorType() {
        return sectorType;
    }
}
