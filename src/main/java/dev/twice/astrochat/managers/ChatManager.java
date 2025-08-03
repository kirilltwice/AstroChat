package dev.twice.astrochat.managers;

import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import dev.twice.astrochat.ChatPlugin;
import dev.twice.astrochat.data.PlayerData;
import dev.twice.astrochat.utils.ColorUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChatManager {

    private final ChatPlugin plugin;
    private final ConcurrentHashMap<String, String> formatCache = new ConcurrentHashMap<>();
    private final Map<World, Map<Long, List<Player>>> spatialIndex = new HashMap<>();

    public ChatManager(ChatPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean isGlobalMessage(String message) {
        return message.startsWith(plugin.getConfigManager().getRangeSymbol());
    }

    public String removeGlobalSymbol(String message) {
        if (isGlobalMessage(message)) {
            return message.substring(1).trim();
        }
        return message;
    }

    public String formatMessage(Player player, String message) {
        PlayerData playerData = plugin.getPermissionManager().getPlayerData(player);

        boolean isGlobal = isGlobalMessage(message);
        String processedMessage = removeGlobalSymbol(message);

        if (plugin.getPermissionManager().hasColorPermission(player)) {
            processedMessage = ColorUtils.translateColors(processedMessage);
        } else {
            processedMessage = ColorUtils.stripColors(processedMessage);
        }

        String cacheKey = playerData.getGroup() + ":" + isGlobal;
        String format = formatCache.get(cacheKey);

        if (format == null) {
            format = isGlobal ?
                    plugin.getConfigManager().getGlobalFormat(playerData.getGroup()) :
                    plugin.getConfigManager().getLocalFormat(playerData.getGroup());

            if (formatCache.size() < 100) {
                formatCache.put(cacheKey, format);
            }
        }

        return buildFormattedMessage(format, player.getName(), playerData, processedMessage);
    }

    private String buildFormattedMessage(String format, String playerName, PlayerData playerData, String message) {
        StringBuilder result = new StringBuilder(format);

        replaceInBuilder(result, "<player>", playerName);
        replaceInBuilder(result, "<prefix>", playerData.getPrefix());
        replaceInBuilder(result, "<suffix>", playerData.getSuffix());
        replaceInBuilder(result, "<message>", message);

        return result.toString();
    }

    private void replaceInBuilder(StringBuilder builder, String target, String replacement) {
        int index = builder.indexOf(target);
        if (index != -1) {
            builder.replace(index, index + target.length(), replacement);
        }
    }

    public void sendLocalMessage(Component message, Player sender) {
        double radius = plugin.getConfigManager().getRangeRadius();
        double radiusSquared = radius * radius;
        Location senderLocation = sender.getLocation();
        World world = sender.getWorld();

        List<Player> nearbyPlayers = getNearbyPlayers(world, senderLocation, radiusSquared);

        for (Player player : nearbyPlayers) {
            player.sendMessage(message);
        }

        if (!nearbyPlayers.contains(sender)) {
            sender.sendMessage(message);
        }
    }

    private List<Player> getNearbyPlayers(World world, Location center, double radiusSquared) {
        List<Player> nearby = new ArrayList<>();

        for (Player player : world.getPlayers()) {
            if (player.getLocation().distanceSquared(center) <= radiusSquared) {
                nearby.add(player);
            }
        }

        return nearby;
    }

    public void sendGlobalMessage(Component message) {
        plugin.getServer().broadcast(message);
    }

    public void clearCache() {
        formatCache.clear();
        spatialIndex.clear();
    }
}