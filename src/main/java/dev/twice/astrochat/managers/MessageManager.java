package dev.twice.astrochat.managers;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import dev.twice.astrochat.ChatPlugin;
import dev.twice.astrochat.utils.ColorUtils;

public class MessageManager {

    private final ChatPlugin plugin;

    public MessageManager(ChatPlugin plugin) {
        this.plugin = plugin;
    }

    public void sendMessage(Player player, String message) {
        Component component = ColorUtils.deserialize(message);
        player.sendMessage(component);
    }

    public void sendActionBar(Player player, String message) {
        Component component = ColorUtils.deserialize(message);
        player.sendActionBar(component);
    }

    public void sendPluginMessage(Player player, String message) {
        String prefix = plugin.getConfigManager().getPluginPrefix();
        message = message.replace("<prefix>", prefix);
        sendActionBar(player, message);
    }

    public void sendNoPermission(Player player) {
        String message = plugin.getConfigManager().getNoPermissionMessage();
        sendPluginMessage(player, message);
    }

    public void sendReloaded(Player player) {
        String message = plugin.getConfigManager().getReloadedMessage();
        sendPluginMessage(player, message);
    }

    public void sendInvalidCommand(Player player) {
        String message = plugin.getConfigManager().getInvalidCommandMessage();
        sendPluginMessage(player, message);
    }

    public void sendCooldownMessage(Player player, long remainingSeconds) {
        String message = plugin.getConfigManager().getCooldownMessage();
        String formats = plugin.getConfigManager().getCooldownFormats();
        String timeFormat = getTimeFormat(remainingSeconds, formats);

        message = message.replace("<time>", String.valueOf(remainingSeconds));
        message = message.replace("<time-format>", timeFormat);

        sendPluginMessage(player, message);
    }

    public void sendRepeatMessage(Player player) {
        String message = plugin.getConfigManager().getRepeatMessage();
        sendPluginMessage(player, message);
    }

    public void sendAntiCapsMessage(Player player) {
        String message = plugin.getConfigManager().getAntiCapsMessage();
        sendPluginMessage(player, message);
    }

    public void sendEmptyColorMessage(Player player) {
        String message = plugin.getConfigManager().getEmptyColorMessage();
        sendPluginMessage(player, message);
    }

    public void sendEmptyGlobalMessage(Player player) {
        String message = plugin.getConfigManager().getEmptyGlobalMessage();
        sendPluginMessage(player, message);
    }

    private String getTimeFormat(long seconds, String formats) {
        String[] formatArray = formats.split("/");
        if (formatArray.length != 3) return "секунд";

        if (seconds == 1) return formatArray[0];
        if (seconds >= 2 && seconds <= 4) return formatArray[1];
        return formatArray[2];
    }
}