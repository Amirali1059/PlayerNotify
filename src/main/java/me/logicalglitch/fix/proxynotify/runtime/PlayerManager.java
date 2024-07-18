package me.logicalglitch.fix.proxynotify.runtime;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerManager {
    private final Set<UUID> messageToggles;
    private final Map<UUID, String> playerLastServer;

    public PlayerManager() {
        this.messageToggles = new HashSet<>();
        this.playerLastServer = new ConcurrentHashMap<>();
    }

    public void cachePlayerServer(UUID uuid, String server) {
        playerLastServer.put(uuid, server);
    }

    public String popPlayer(UUID uuid) {
        return playerLastServer.remove(uuid);
    }

    public boolean toggleMessagesIsOn(UUID uuid) {
        if (messageToggles.contains(uuid)) {
            messageToggles.remove(uuid);
            return true;
        } else {
            messageToggles.add(uuid);
            return false;
        }
    }

    public boolean isToggledOff(UUID uuid) {
        return messageToggles.contains(uuid);
    }
}
