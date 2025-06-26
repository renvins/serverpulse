package it.renvins.serverpulse.bungeecord.commands;

import java.util.HashMap;
import java.util.Map;

import it.renvins.serverpulse.common.utils.ChatUtils;
import it.renvins.serverpulse.bungeecord.config.BungeeCordConfiguration;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class ServerPulseCommand extends Command {

    private final BungeeCordConfiguration config;
    private final Map<String, GeneralCommand> commands = new HashMap<>();

    public ServerPulseCommand(BungeeCordConfiguration config) {
        super("serverpulsebungeecord", "serverpulse.use", "sp", "spb");
        this.config = config;
        registerCommands();
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(new TextComponent(ChatUtils.format(config.getConfig().getString("messages.noArgs"))));
            return;
        }
        String cmdName = args[0].toLowerCase();
        if (!commands.containsKey(cmdName)) {
            sender.sendMessage(new TextComponent(ChatUtils.format(config.getConfig().getString("messages.noCommand"))));
            return;
        }
        GeneralCommand cmd = commands.get(cmdName);
        if (!sender.hasPermission(cmd.getPermission())) {
            sender.sendMessage(new TextComponent(ChatUtils.format(config.getConfig().getString("messages.noPerms"))));
            return;
        }
        if (cmd.isPlayerOnly() && !(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(new TextComponent(ChatUtils.format(config.getConfig().getString("messages.playerOnly"))));
            return;
        }
        if (args.length > 1) {
            String[] newArgs = new String[args.length - 1];
            System.arraycopy(args, 1, newArgs, 0, args.length - 1);
            cmd.run(sender, newArgs);
        } else {
            cmd.run(sender, new String[0]);
        }
    }

    private void registerCommands() {
        commands.put("reload", new ReloadCommand("serverpulse.reload", false, config));
        commands.put("status", new StatusCommand("serverpulse.status", false, config));
    }
}
