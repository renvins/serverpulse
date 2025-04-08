package it.renvins.serverpulse.commands;

import it.renvins.serverpulse.config.CustomConfig;
import it.renvins.serverpulse.service.IDatabaseService;
import it.renvins.serverpulse.utils.ChatUtils;
import org.bukkit.command.CommandSender;

public class StatusCommand extends GeneralCommand {

    private final CustomConfig config;
    private final IDatabaseService databaseService;

    public StatusCommand(String permission, boolean isPlayerOnly, CustomConfig config, IDatabaseService databaseService) {
        super(permission, isPlayerOnly);

        this.config = config;
        this.databaseService = databaseService;
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        if (args.length > 0) {
            sender.sendMessage(ChatUtils.format(config.getConfig().getString("messages.statusUsage")));
            return;
        }
        sender.sendMessage(ChatUtils.format(databaseService.isConnected() ?
                config.getConfig().getString("messages.statusConnected") :
                config.getConfig().getString("messages.statusNotConnected")));
    }
}
