package it.renvins.serverpulse.fabric.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import it.renvins.serverpulse.api.ServerPulseProvider;
import it.renvins.serverpulse.common.config.GeneralConfiguration;
import it.renvins.serverpulse.common.utils.ChatUtils;
import lombok.RequiredArgsConstructor;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

@RequiredArgsConstructor
public class ServerPulseCommand {

    private final GeneralConfiguration config;

    public LiteralArgumentBuilder<ServerCommandSource> createCommand() {
        // Create the main command
        LiteralArgumentBuilder<ServerCommandSource> command =
                CommandManager.literal("serverpulse")
                              .executes(context -> {
                                    ServerCommandSource source = context.getSource();
                                    source.sendMessage(Text.of(ChatUtils.format(config.getConfig().getString("messages.usage"))));

                                    return Command.SINGLE_SUCCESS;
                                });

        command.then(CommandManager.literal("reload")
                                   .requires(source -> Permissions.check(source, "serverpulse.reload", 2))
                                   .executes(this::executeReload));
        command.then(CommandManager.literal("status")
                                     .requires(source -> Permissions.check(source, "serverpulse.status", 2))
                                     .executes(this::executeStatus));

        return command;
    }

    private int executeReload(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();

        Text message;
        if (config.load()) {
            message = Text.of(ChatUtils.format(config.getConfig().getString("messages.reloadConfig")));
        } else {
            message = Text.of(ChatUtils.format(config.getConfig().getString("messages.reloadConfigError")));
        }

        source.sendMessage(message);
        return Command.SINGLE_SUCCESS;
    }

    private int executeStatus(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();

        Text message = Text.of(
                ChatUtils.format(ServerPulseProvider.get().getDatabaseService().isConnected() ?
                        config.getConfig().getString("messages.statusConnected") :
                        config.getConfig().getString("messages.statusNotConnected")));

        source.sendMessage(message);
        return Command.SINGLE_SUCCESS;
    }
}
