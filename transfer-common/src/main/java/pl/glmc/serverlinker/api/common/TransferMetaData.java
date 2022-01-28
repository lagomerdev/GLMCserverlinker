package pl.glmc.serverlinker.api.common;

import java.util.HashMap;

public class TransferMetaData {

    private final HashMap<String, String> transferMetaData = new HashMap<>();

    public void setItem(String key, String value) {
        this.transferMetaData.put(key, value);
    }

    public String getItem(String key) {
        return this.transferMetaData.get(key);
    }

    public boolean hasItem(String key) {
        return this.transferMetaData.containsKey(key);
    }

    public HashMap<String, String> getTransferMetaData() {
        return transferMetaData;
    }
}
