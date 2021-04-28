package pl.glmc.serverlinker.api.bungee.event;

import net.md_5.bungee.api.plugin.Event;

public class SpecifyJoinServerEvent extends Event {

    private final String lastServer, lastServerType; //nullable
    private final boolean firstJoin;

    private String joinServer;

    public SpecifyJoinServerEvent(String lastServer, String lastServerType, boolean firstJoin) {
        this.lastServer = lastServer;
        this.lastServerType = lastServerType;
        this.firstJoin = firstJoin;

        this.joinServer = lastServer;
    }

    public String getLastServer() {
        return lastServer;
    }

    public String getLastServerType() {
        return lastServerType;
    }

    public String getJoinServer() {
        return joinServer;
    }

    public boolean isFirstJoin() {
        return firstJoin;
    }

    public void setJoinServer(String joinServer) {
        this.joinServer = joinServer;
    }
}
