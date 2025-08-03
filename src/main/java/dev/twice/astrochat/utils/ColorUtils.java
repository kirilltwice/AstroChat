package dev.twice.astrochat.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public class ColorUtils {

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    private static final Pattern COLOR_PATTERN = Pattern.compile("&[0-9a-fk-or]", Pattern.CASE_INSENSITIVE);
    private static final Pattern HEX_PATTERN = Pattern.compile("&#[a-fA-F0-9]{6}");

    private static final ConcurrentHashMap<String, Component> COMPONENT_CACHE = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, String> COLOR_TRANSLATION_CACHE = new ConcurrentHashMap<>();

    private static final String[] LEGACY_COLORS = {
            "&0", "<black>", "&1", "<dark_blue>", "&2", "<dark_green>", "&3", "<dark_aqua>",
            "&4", "<dark_red>", "&5", "<dark_purple>", "&6", "<gold>", "&7", "<gray>",
            "&8", "<dark_gray>", "&9", "<blue>", "&a", "<green>", "&b", "<aqua>",
            "&c", "<red>", "&d", "<light_purple>", "&e", "<yellow>", "&f", "<white>",
            "&k", "<obfuscated>", "&l", "<bold>", "&m", "<strikethrough>",
            "&n", "<underlined>", "&o", "<italic>", "&r", "<reset>"
    };

    public static Component deserialize(String message) {
        if (message == null || message.isEmpty()) return Component.empty();

        Component cached = COMPONENT_CACHE.get(message);
        if (cached != null) return cached;

        String processed = translateColors(message);
        Component component = MINI_MESSAGE.deserialize(processed);

        if (COMPONENT_CACHE.size() < 1000) {
            COMPONENT_CACHE.put(message, component);
        }

        return component;
    }

    public static Component deserializeWithPermission(Player player, String message) {
        if (message == null || message.isEmpty()) return Component.empty();

        if (player.hasPermission("astrochat.use.color")) {
            return deserialize(message);
        }

        return Component.text(stripColors(message));
    }

    public static String translateColors(String message) {
        if (message == null || message.isEmpty()) return "";

        String cached = COLOR_TRANSLATION_CACHE.get(message);
        if (cached != null) return cached;

        String processed = translateLegacyColors(message);
        processed = translateHexColors(processed);

        if (COLOR_TRANSLATION_CACHE.size() < 1000) {
            COLOR_TRANSLATION_CACHE.put(message, processed);
        }

        return processed;
    }

    private static String translateLegacyColors(String message) {
        for (int i = 0; i < LEGACY_COLORS.length; i += 2) {
            if (message.contains(LEGACY_COLORS[i])) {
                message = message.replace(LEGACY_COLORS[i], LEGACY_COLORS[i + 1]);
            }
        }
        return message;
    }

    private static String translateHexColors(String message) {
        return HEX_PATTERN.matcher(message).replaceAll(match -> {
            String hex = match.group().substring(2);
            return "<color:#" + hex + ">";
        });
    }

    public static String stripColors(String message) {
        if (message == null || message.isEmpty()) return "";

        message = COLOR_PATTERN.matcher(message).replaceAll("");
        message = HEX_PATTERN.matcher(message).replaceAll("");

        return message;
    }

    public static boolean hasOnlyColorCodes(String message) {
        if (message == null || message.isEmpty()) return false;

        String stripped = stripColors(message).trim();
        return stripped.isEmpty() && !message.trim().isEmpty();
    }

    public static void clearCache() {
        COMPONENT_CACHE.clear();
        COLOR_TRANSLATION_CACHE.clear();
    }
}