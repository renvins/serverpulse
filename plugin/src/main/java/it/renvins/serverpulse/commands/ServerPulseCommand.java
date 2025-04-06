package it.renvins.serverpulse.commands;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import it.renvins.serverpulse.config.CustomConfig;
import it.renvins.serverpulse.utils.ChatUtils;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.HelpCommand;
import org.bukkit.command.defaults.VersionCommand;
import org.jetbrains.annotations.NotNull;

public class ServerPulseCommand implements CommandExecutor {

    private final CustomConfig config;
    private final Map<String, ReloadCommand> commands = new HashMap<>();

    public ServerPulseCommand(CustomConfig config) {
        this.config = config;
        registerCommands();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatUtils.format(config.getConfig().getString("messages.noArgs")));
            return true;
        }
        String cmdName = args[0].toLowerCase();
        if (!commands.containsKey(cmdName)) {
            sender.sendMessage(ChatUtils.format(config.getConfig().getString("messages.noCommand")));
            return true;
        }
        GeneralCommand cmd = commands.get(cmdName);
        if (!sender.hasPermission(cmd.getPermission())) {
            sender.sendMessage(ChatUtils.format(config.getConfig().getString("messages.noPerms")));
            return true;
        }
        if (cmd.isPlayerOnly() && !(sender instanceof org.bukkit.entity.Player)) {
            sender.sendMessage(ChatUtils.format(config.getConfig().getString("messages.playerOnly")));
            return true;
        }
        if (args.length > 1) {
            String[] newArgs = new String[args.length - 1];
            System.arraycopy(args, 1, newArgs, 0, args.length - 1);
            cmd.run(sender, newArgs);
        } else {
            cmd.run(sender, new String[0]);
        }
        return true;
    }

    private void registerCommands() {
        commands.put("reload", new ReloadCommand("serverpulse.reload", false));
    }
}
