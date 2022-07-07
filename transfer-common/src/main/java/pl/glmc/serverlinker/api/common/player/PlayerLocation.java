package pl.glmc.serverlinker.api.common.player;

public class PlayerLocation {

    private final boolean present;
    private int x, y, z;

    public PlayerLocation() {
        this.present = false;
    }

    public PlayerLocation(int x, int y, int z) {
        this.present = true;

        this.x = x;
        this.y = y;
        this.z = z;
    }

    public boolean isPresent() {
        return present;
    }

    public double getX() {
        return x + 0.5;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z + 0.5;
    }

    @Override
    public String toString() {
        return "PlayerLocation{" +
                "present=" + present +
                ", x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }
}
