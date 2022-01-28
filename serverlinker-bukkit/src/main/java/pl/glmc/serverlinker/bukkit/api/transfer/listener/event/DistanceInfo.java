package pl.glmc.serverlinker.bukkit.api.transfer.listener.event;

public class DistanceInfo {

    private String server, direction;
    private double distance;
    private boolean isMapBorder = false;

    public DistanceInfo(String server, String direction, double distance) {
        this.server = server;
        this.direction = direction;
        this.distance = distance;

        if (server.equals("null")) isMapBorder = true;
    }

    public String getServer() {
        return server;
    }

    public String getDirection() {
        return direction;
    }

    public double getDistance() {
        return distance;
    }

    public boolean isMapBorder() {
        return isMapBorder;
    }
}
