package it.renvins.serverpulse.velocity.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import it.renvins.serverpulse.api.ServerPulseProvider;
import it.renvins.serverpulse.common.config.GeneralConfiguration;
import it.renvins.serverpulse.common.utils.ChatUtils;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

@RequiredArgsConstructor
public class ServerPulseCommand {

    private final GeneralConfiguration config;

    public BrigadierCommand createCommand() {
        // Create the main command
        LiteralArgumentBuilder<CommandSource> command =
                BrigadierCommand.literalArgumentBuilder("serverpulsevelocity")
                                .executes(context -> {
                                    CommandSource source = context.getSource();

                                    Component message = LegacyComponentSerializer.legacySection().deserialize(
                                            ChatUtils.format(config.getConfig().getString("messages.usage")));
                                    source.sendMessage(message);

                                    return Command.SINGLE_SUCCESS;
                                });

        command.then(BrigadierCommand.literalArgumentBuilder("reload")
                                .requires(source -> source.hasPermission("serverpulse.reload"))
                                .executes(this::executeReload)
        );

        command.then(BrigadierCommand.literalArgumentBuilder("status")
                                .requires(source -> source.hasPermission("serverpulse.status"))
                                .executes(this::executeStatus)
        );

        return new BrigadierCommand(command.build());
    }

    private int executeReload(CommandContext<CommandSource> context) {
        CommandSource source = context.getSource();

        Component message;
        if (config.load()) {
            message = LegacyComponentSerializer.legacySection().deserialize(
                    ChatUtils.format(config.getConfig().getString("messages.reloadConfig")));
        } else {
            message = LegacyComponentSerializer.legacySection().deserialize(
                    ChatUtils.format(config.getConfig().getString("messages.reloadConfigError")));
        }

        source.sendMessage(message);
        return Command.SINGLE_SUCCESS;
    }

    private int executeStatus(CommandContext<CommandSource> context) {
        CommandSource source = context.getSource();

        Component message = LegacyComponentSerializer.legacySection().deserialize(
                ChatUtils.format(ServerPulseProvider.get().getDatabaseService().isConnected() ?
                        config.getConfig().getString("messages.statusConnected") :
                        config.getConfig().getString("messages.statusNotConnected")));

        source.sendMessage(message);
        return Command.SINGLE_SUCCESS;
    }
}
