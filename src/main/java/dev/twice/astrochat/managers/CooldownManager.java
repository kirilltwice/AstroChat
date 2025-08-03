package dev.twice.astrochat.managers;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CooldownManager {

    private final Map<UUID, Long> cooldowns = new HashMap<>();
    private final Map<UUID, String> lastMessages = new HashMap<>();

    public boolean hasCooldown(Player player) {
        UUID uuid = player.getUniqueId();
        if (!cooldowns.containsKey(uuid)) {
            return false;
        }

        long currentTime = System.currentTimeMillis();
        long cooldownTime = cooldowns.get(uuid);

        if (currentTime >= cooldownTime) {
            cooldowns.remove(uuid);
            return false;
        }

        return true;
    }

    public long getRemainingCooldown(Player player) {
        UUID uuid = player.getUniqueId();
        if (!cooldowns.containsKey(uuid)) {
            return 0;
        }

        long currentTime = System.currentTimeMillis();
        long cooldownTime = cooldowns.get(uuid);

        return (cooldownTime - currentTime) / 1000;
    }

    public void setCooldown(Player player, int seconds) {
        UUID uuid = player.getUniqueId();
        long cooldownTime = System.currentTimeMillis() + (seconds * 1000L);
        cooldowns.put(uuid, cooldownTime);
    }

    public boolean isRepeatedMessage(Player player, String message) {
        UUID uuid = player.getUniqueId();
        String lastMessage = lastMessages.get(uuid);

        if (lastMessage != null && lastMessage.equals(message)) {
            return true;
        }

        lastMessages.put(uuid, message);
        return false;
    }

    public void clearCooldown(Player player) {
        cooldowns.remove(player.getUniqueId());
    }

    public void clearLastMessage(Player player) {
        lastMessages.remove(player.getUniqueId());
    }
}