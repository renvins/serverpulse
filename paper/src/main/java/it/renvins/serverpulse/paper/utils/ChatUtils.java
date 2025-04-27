package it.renvins.serverpulse.paper.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ChatUtils {

    public String format(String message) {
        return message.replace("&", "§");
    }
}
