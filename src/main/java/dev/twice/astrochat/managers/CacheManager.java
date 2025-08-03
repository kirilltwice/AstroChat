package dev.twice.astrochat.managers;

import org.bukkit.scheduler.BukkitRunnable;
import dev.twice.astrochat.ChatPlugin;
import dev.twice.astrochat.utils.ColorUtils;

public class CacheManager {

    private final ChatPlugin plugin;
    private BukkitRunnable cacheCleanupTask;

    public CacheManager(ChatPlugin plugin) {
        this.plugin = plugin;
        startCacheCleanup();
    }

    private void startCacheCleanup() {
        cacheCleanupTask = new BukkitRunnable() {
            @Override
            public void run() {
                clearAllCaches();
            }
        };

        cacheCleanupTask.runTaskTimerAsynchronously(plugin, 6000L, 6000L);
    }

    public void clearAllCaches() {
        ColorUtils.clearCache();
        plugin.getChatManager().clearCache();
    }

    public void shutdown() {
        if (cacheCleanupTask != null) {
            cacheCleanupTask.cancel();
        }
        clearAllCaches();
    }
}