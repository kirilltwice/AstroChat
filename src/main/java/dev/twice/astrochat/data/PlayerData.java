package dev.twice.astrochat.data;

import lombok.Data;

@Data
public class PlayerData {
    private final String group;
    private final String prefix;
    private final String suffix;
    private final long cacheTime;

    public PlayerData(String group, String prefix, String suffix) {
        this.group = group != null ? group : "default";
        this.prefix = prefix != null ? prefix : "";
        this.suffix = suffix != null ? suffix : "";
        this.cacheTime = System.currentTimeMillis();
    }

    public boolean isExpired(long ttl) {
        return System.currentTimeMillis() - cacheTime > ttl;
    }
}