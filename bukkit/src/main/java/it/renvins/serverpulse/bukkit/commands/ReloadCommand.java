package it.renvins.serverpulse.bukkit.commands;

import it.renvins.serverpulse.common.utils.ChatUtils;
import it.renvins.serverpulse.bukkit.config.BukkitConfiguration;
import org.bukkit.command.CommandSender;

public class ReloadCommand extends GeneralCommand {

    private final BukkitConfiguration config;

    public ReloadCommand(String permission, boolean isPlayerOnly, BukkitConfiguration config) {
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
