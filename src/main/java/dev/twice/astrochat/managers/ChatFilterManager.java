package dev.twice.astrochat.managers;

import org.bukkit.entity.Player;
import dev.twice.astrochat.ChatPlugin;

public class ChatFilterManager {

    private final ChatPlugin plugin;

    public ChatFilterManager(ChatPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean shouldBlockCooldown(Player player) {
        if (plugin.getPermissionManager().hasCooldownBypass(player)) {
            return false;
        }

        if (!plugin.getConfigManager().isCooldownEnabled()) {
            return false;
        }

        return plugin.getCooldownManager().hasCooldown(player);
    }

    public boolean shouldBlockRepeat(Player player, String message) {
        return plugin.getCooldownManager().isRepeatedMessage(player, message);
    }

    public String processAntiCaps(Player player, String message) {
        if (!isAntiCapsTriggered(message)) {
            return message;
        }

        String type = plugin.getConfigManager().getAntiCapsType();

        switch (type.toUpperCase()) {
            case "REPLACE":
                return message.toLowerCase();
            case "CANCEL":
                plugin.getMessageManager().sendAntiCapsMessage(player);
                return null;
            case "NULL":
            default:
                return message;
        }
    }

    public void applyCooldown(Player player) {
        if (!plugin.getPermissionManager().hasCooldownBypass(player) &&
                plugin.getConfigManager().isCooldownEnabled()) {
            plugin.getCooldownManager().setCooldown(player, plugin.getConfigManager().getCooldownTime());
        }
    }

    private boolean isAntiCapsTriggered(String message) {
        if (message.length() < 3) return false;

        int upperCount = 0;
        int letterCount = 0;

        for (char c : message.toCharArray()) {
            if (Character.isLetter(c)) {
                letterCount++;
                if (Character.isUpperCase(c)) {
                    upperCount++;
                }
            }
        }

        if (letterCount == 0) return false;

        double percentage = (double) upperCount / letterCount * 100;
        return percentage >= plugin.getConfigManager().getAntiCapsPercent();
    }
}