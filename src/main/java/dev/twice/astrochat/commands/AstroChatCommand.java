package dev.twice.astrochat.commands;

import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import org.bukkit.entity.Player;
import dev.twice.astrochat.ChatPlugin;

import java.util.List;

@Command(name = "astrochat")
public class AstroChatCommand {

    private final ChatPlugin plugin;

    public AstroChatCommand(ChatPlugin plugin) {
        this.plugin = plugin;
    }

    @Execute
    public void execute(@Context Player player) {
        List<String> helpMessages = plugin.getConfigManager().getHelpMessages();

        for (String message : helpMessages) {
            plugin.getMessageManager().sendMessage(player, message);
        }
    }

    @Execute(name = "reload")
    @Permission("astrochat.admin")
    public void reload(@Context Player player) {
        plugin.getConfigManager().reloadConfig();
        plugin.getMessageManager().sendReloaded(player);
    }
}