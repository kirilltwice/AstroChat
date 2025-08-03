package dev.twice.astrochat.managers;

import net.luckperms.api.model.user.User;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import dev.twice.astrochat.ChatPlugin;
import dev.twice.astrochat.data.PlayerData;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PermissionManager implements Listener {

    private final ChatPlugin plugin;
    private final ConcurrentHashMap<UUID, PlayerData> playerCache = new ConcurrentHashMap<>();
    private static final long CACHE_TTL = 300000;

    public PermissionManager(ChatPlugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public boolean hasColorPermission(Player player) {
        return player.hasPermission("astrochat.use.color");
    }

    public boolean hasCooldownBypass(Player player) {
        return player.hasPermission("astrochat.bypass.cooldown");
    }

    public boolean hasAdminPermission(Player player) {
        return player.hasPermission("astrochat.admin");
    }

    public PlayerData getPlayerData(Player player) {
        UUID uuid = player.getUniqueId();
        PlayerData cached = playerCache.get(uuid);

        if (cached != null && !cached.isExpired(CACHE_TTL)) {
            return cached;
        }

        PlayerData data = loadPlayerData(player);
        playerCache.put(uuid, data);
        return data;
    }

    public String getPlayerGroup(Player player) {
        return getPlayerData(player).getGroup();
    }

    public String getPlayerPrefix(Player player) {
        return getPlayerData(player).getPrefix();
    }

    public String getPlayerSuffix(Player player) {
        return getPlayerData(player).getSuffix();
    }

    private PlayerData loadPlayerData(Player player) {
        if (plugin.getLuckPerms() == null) {
            return new PlayerData("default", "", "");
        }

        User user = plugin.getLuckPerms().getUserManager().getUser(player.getUniqueId());
        if (user == null) {
            return new PlayerData("default", "", "");
        }

        String group = user.getPrimaryGroup();
        String prefix = user.getCachedData().getMetaData().getPrefix();
        String suffix = user.getCachedData().getMetaData().getSuffix();

        return new PlayerData(group, prefix, suffix);
    }

    public void invalidatePlayer(UUID uuid) {
        playerCache.remove(uuid);
    }

    public void clearCache() {
        playerCache.clear();
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        playerCache.remove(event.getPlayer().getUniqueId());
    }
}