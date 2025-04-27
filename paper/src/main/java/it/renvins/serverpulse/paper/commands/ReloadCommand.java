package it.renvins.serverpulse.paper.commands;

import it.renvins.serverpulse.paper.config.CustomConfig;
import it.renvins.serverpulse.paper.utils.ChatUtils;
import org.bukkit.command.CommandSender;

public class ReloadCommand extends GeneralCommand {

    private final CustomConfig config;

    public ReloadCommand(String permission, boolean isPlayerOnly, CustomConfig config) {
        super(permission, isPlayerOnly);
        this.config = config;
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        if (args.length > 0) {
            sender.sendMessage(ChatUtils.format(config.getConfig().getString("messages.reloadConfigUsage")));
            return;
        }
        if (!config.reload()) {
            sender.sendMessage(ChatUtils.format(config.getConfig().getString("messages.reloadConfigError")));
        } else {
            sender.sendMessage(ChatUtils.format(config.getConfig().getString("messages.reloadConfig")));
        }
    }
}
