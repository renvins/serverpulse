package it.renvins.serverpulse.commands;

import lombok.Getter;
import org.bukkit.command.CommandSender;

@Getter
public abstract class GeneralCommand {

    private final String permission;
    private final boolean isPlayerOnly;

    public GeneralCommand(String permission, boolean isPlayerOnly) {
        this.permission = permission;
        this.isPlayerOnly = isPlayerOnly;
    }

    public abstract void run(CommandSender sender, String[] args);
}
