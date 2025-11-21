package it.renvins.serverpulse.common.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ChatUtils {

    public String format(String message) {
        if (message == null) return "";
        return message.replace("&", "§");
    }
}
