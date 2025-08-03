package dev.twice.astrochat.config;

import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import dev.twice.astrochat.ChatPlugin;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Getter
public class ConfigManager {

    private final ChatPlugin plugin;
    private FileConfiguration config;
    private File configFile;

    public ConfigManager(ChatPlugin plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    public void loadConfig() {
        configFile = new File(plugin.getDataFolder(), "config.yml");

        if (!configFile.exists()) {
            plugin.getDataFolder().mkdirs();
            plugin.saveResource("config.yml", false);
        }

        config = YamlConfiguration.loadConfiguration(configFile);
    }

    public void saveConfig() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reloadConfig() {
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    public boolean isRangeEnabled() {
        return config.getBoolean("Settings.Range.enabled", true);
    }

    public double getRangeRadius() {
        return config.getDouble("Settings.Range.radius", 100.0);
    }

    public String getRangeSymbol() {
        return config.getString("Settings.Range.symbol", "!");
    }

    public boolean isCooldownEnabled() {
        return config.getBoolean("Settings.Cooldown.enabled", true);
    }

    public int getCooldownTime() {
        return config.getInt("Settings.Cooldown.time", 3);
    }

    public String getLocalFormat() {
        return getLocalFormat("default");
    }

    public String getGlobalFormat() {
        return getGlobalFormat("default");
    }

    public String getLocalFormat(String group) {
        String path = "Settings.Formats.Chat." + group + ".local";
        String defaultPath = "Settings.Formats.Chat.default.local";

        if (config.contains(path)) {
            return config.getString(path);
        }
        return config.getString(defaultPath, "<gray>⌞<c:#c9ff45><b>L</b></c>⌝</gray> <dark_gray>|</dark_gray> <prefix><player><suffix> <c:#969aff>▹</c> <gray><message></gray>");
    }

    public String getGlobalFormat(String group) {
        String path = "Settings.Formats.Chat." + group + ".global";
        String defaultPath = "Settings.Formats.Chat.default.global";

        if (config.contains(path)) {
            return config.getString(path);
        }
        return config.getString(defaultPath, "<gray>⌞<c:#c9ff45><b>L</b></c>⌝</gray> <dark_gray>|</dark_gray> <prefix><player><suffix> <c:#969aff>▹</c> <gray><message></gray>");
    }

    public String getPluginPrefix() {
        return config.getString("Messages.Plugin.prefix", "<gray>⌞<gradient:#fc5c5c:#ffa500><b>AstroChat</b></gradient>⌝</gray> <yellow><b>▸</b></yellow>");
    }

    public String getReloadedMessage() {
        return config.getString("Messages.Plugin.reloaded", "<prefix> <gray>⌞<c:#35f7a6><b>✔</b></c>⌝</gray> Плагин перезагружен.");
    }

    public String getNoPermissionMessage() {
        return config.getString("Messages.Plugin.no-permission", "<prefix> &7⌞&c&l✖&7⌝ &fУ вас недостаточно прав!");
    }

    public String getInvalidCommandMessage() {
        return config.getString("Messages.Plugin.invalid-command", "<prefix> <gray>⌞<c:#ff7c7c><b>✖</b></c>⌝</gray> Неизвестная команда.");
    }

    public List<String> getHelpMessages() {
        return config.getStringList("Messages.Plugin.help");
    }

    public String getCooldownFormats() {
        return config.getString("Chat.Cooldown.formats", "секунду/секунды/секунд");
    }

    public String getCooldownMessage() {
        return config.getString("Chat.Cooldown.message", "<prefix> <gray>⌞<c:#ffbf6e><b>!</b></c>⌝</gray> Вы сможете писать в чат через <c:#35f7a6><time></c> <time-format>.");
    }

    public String getRepeatMessage() {
        return config.getString("Chat.Repeat.message", "<prefix> <gray>⌞<c:#ffbf6e><b>!</b></c>⌝</gray> Не пишите одно и тоже сообщение.");
    }

    public String getAntiCapsMessage() {
        return config.getString("Chat.Anti-caps.message", "<prefix> <gray>⌞<c:#ffbf6e><b>!</b></c>⌝</gray> Не капси.");
    }

    public int getAntiCapsPercent() {
        return config.getInt("Chat.Anti-caps.percent", 50);
    }

    public String getAntiCapsType() {
        return config.getString("Chat.Anti-caps.type", "REPLACE");
    }

    public String getEmptyColorMessage() {
        return config.getString("Chat.Empty-color.message", "<prefix> <gray>⌞<c:#ffbf6e><b>!</b></c>⌝</gray> Нельзя отправлять пустые сообщения с цветовыми кодами.");
    }

    public String getEmptyGlobalMessage() {
        return config.getString("Chat.Empty-global.message", "<prefix> <gray>⌞<c:#ffbf6e><b>!</b></c>⌝</gray> Нельзя отправлять пустые глобальные сообщения.");
    }
}