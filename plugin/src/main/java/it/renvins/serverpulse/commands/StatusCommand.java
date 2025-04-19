package it.renvins.serverpulse.commands;

import it.renvins.serverpulse.api.ServerPulseProvider;
import it.renvins.serverpulse.config.CustomConfig;
import it.renvins.serverpulse.utils.ChatUtils;
import org.bukkit.command.CommandSender;

public class StatusCommand extends GeneralCommand {

    private final CustomConfig config;

    public StatusCommand(String permission, boolean isPlayerOnly, CustomConfig config) {
        super(permission, isPlayerOnly);

        this.config = config;
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        if (args.length > 0) {
            sender.sendMessage(ChatUtils.format(config.getConfig().getString("messages.statusUsage")));
            return;
        }
        sender.sendMessage(ChatUtils.format(ServerPulseProvider.get().getDatabaseService().isConnected() ?
                config.getConfig().getString("messages.statusConnected") :
                config.getConfig().getString("messages.statusNotConnected")));
    }
}
