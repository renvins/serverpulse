package it.renvins.serverpulse.bungeecord.commands;

import it.renvins.serverpulse.common.config.GeneralConfiguration;
import it.renvins.serverpulse.common.utils.ChatUtils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;

public class ReloadCommand extends GeneralCommand {

    private final GeneralConfiguration config;

    public ReloadCommand(String permission, boolean isPlayerOnly, GeneralConfiguration config) {
        super(permission, isPlayerOnly);
        this.config = config;
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        if (args.length > 0) {
            sender.sendMessage(new TextComponent(ChatUtils.format(config.getConfig().getString("messages.reloadConfigUsage"))));
            return;
        }
        if (!config.load()) {
            sender.sendMessage(new TextComponent(ChatUtils.format(config.getConfig().getString("messages.reloadConfigError"))));
        } else {
            sender.sendMessage(new TextComponent(ChatUtils.format(config.getConfig().getString("messages.reloadConfig"))));
        }
    }
}
