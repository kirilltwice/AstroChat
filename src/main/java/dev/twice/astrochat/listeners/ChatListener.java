package dev.twice.astrochat.listeners;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import dev.twice.astrochat.ChatPlugin;
import dev.twice.astrochat.utils.ColorUtils;

public class ChatListener implements Listener {

    private final ChatPlugin plugin;

    public ChatListener(ChatPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onAsyncChat(AsyncChatEvent event) {
        event.setCancelled(true);

        Player player = event.getPlayer();
        String originalMessage = PlainTextComponentSerializer.plainText().serialize(event.originalMessage());

        if (ColorUtils.hasOnlyColorCodes(originalMessage)) {
            plugin.getMessageManager().sendEmptyColorMessage(player);
            return;
        }

        if (originalMessage.trim().equals(plugin.getConfigManager().getRangeSymbol())) {
            plugin.getMessageManager().sendEmptyGlobalMessage(player);
            return;
        }

        if (plugin.getChatFilterManager().shouldBlockCooldown(player)) {
            long remaining = plugin.getCooldownManager().getRemainingCooldown(player);
            plugin.getMessageManager().sendCooldownMessage(player, remaining);
            return;
        }

        if (plugin.getChatFilterManager().shouldBlockRepeat(player, originalMessage)) {
            plugin.getMessageManager().sendRepeatMessage(player);
            return;
        }

        String processedMessage = plugin.getChatFilterManager().processAntiCaps(player, originalMessage);
        if (processedMessage == null) {
            return;
        }

        String formattedMessage = plugin.getChatManager().formatMessage(player, processedMessage);
        Component finalMessage = ColorUtils.deserialize(formattedMessage);

        if (plugin.getChatManager().isGlobalMessage(originalMessage)) {
            plugin.getChatManager().sendGlobalMessage(finalMessage);
        } else if (plugin.getConfigManager().isRangeEnabled()) {
            plugin.getChatManager().sendLocalMessage(finalMessage, player);
        } else {
            plugin.getChatManager().sendGlobalMessage(finalMessage);
        }

        plugin.getChatFilterManager().applyCooldown(player);
    }
}