package it.renvins.serverpulse.paper.commands;

import it.renvins.serverpulse.api.ServerPulseProvider;
import it.renvins.serverpulse.common.utils.ChatUtils;
import it.renvins.serverpulse.paper.config.PaperConfiguration;
import org.bukkit.command.CommandSender;

public class StatusCommand extends GeneralCommand {

    private final PaperConfiguration config;

    public StatusCommand(String permission, boolean isPlayerOnly, PaperConfiguration config) {
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
