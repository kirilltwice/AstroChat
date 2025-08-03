package dev.twice.astrochat;

import dev.rollczi.litecommands.LiteCommands;
import dev.rollczi.litecommands.bukkit.LiteBukkitFactory;
import dev.twice.astrochat.managers.*;
import lombok.Getter;
import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import dev.twice.astrochat.commands.AstroChatCommand;
import dev.twice.astrochat.config.ConfigManager;
import dev.twice.astrochat.listeners.ChatListener;

@Getter
public final class ChatPlugin extends JavaPlugin {

    private static ChatPlugin instance;
    private ConfigManager configManager;
    private MessageManager messageManager;
    private CooldownManager cooldownManager;
    private PermissionManager permissionManager;
    private ChatManager chatManager;
    private ChatFilterManager chatFilterManager;
    private CacheManager cacheManager;
    private LuckPerms luckPerms;
    private LiteCommands<org.bukkit.command.CommandSender> liteCommands;

    @Override
    public void onEnable() {
        instance = this;

        if (!setupLuckPerms()) {
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        initializeManagers();
        registerListeners();
        setupCommands();
    }

    @Override
    public void onDisable() {
        if (liteCommands != null) {
            liteCommands.unregister();
        }
        if (cacheManager != null) {
            cacheManager.shutdown();
        }
    }

    private boolean setupLuckPerms() {
        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) {
            luckPerms = provider.getProvider();
            return luckPerms != null;
        }
        return false;
    }

    private void initializeManagers() {
        configManager = new ConfigManager(this);
        messageManager = new MessageManager(this);
        cooldownManager = new CooldownManager();
        permissionManager = new PermissionManager(this);
        chatManager = new ChatManager(this);
        chatFilterManager = new ChatFilterManager(this);
        cacheManager = new CacheManager(this);
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new ChatListener(this), this);
    }

    private void setupCommands() {
        liteCommands = LiteBukkitFactory.builder()
                .commands(new AstroChatCommand(this))
                .build();
    }

    public static ChatPlugin getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public MessageManager getMessageManager() {
        return messageManager;
    }

    public CooldownManager getCooldownManager() {
        return cooldownManager;
    }

    public PermissionManager getPermissionManager() {
        return permissionManager;
    }

    public ChatManager getChatManager() {
        return chatManager;
    }

    public ChatFilterManager getChatFilterManager() {
        return chatFilterManager;
    }
}