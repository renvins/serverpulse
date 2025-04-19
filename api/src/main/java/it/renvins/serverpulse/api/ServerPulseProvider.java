package it.renvins.serverpulse.api;

public class ServerPulseProvider {

    private static ServerPulseAPI api;

    public static void register(ServerPulseAPI api) {
        if (ServerPulseProvider.api != null) {
            throw new IllegalStateException("ServerPulseAPI already registered!");
        }
        ServerPulseProvider.api = api;
    }

    public static void unregister() {
        if (api == null) {
            throw new IllegalStateException("ServerPulseAPI not registered!");
        }
        ServerPulseProvider.api = null;
    }

    public static ServerPulseAPI get() {
        if (api == null) {
            throw new IllegalStateException("ServerPulseAPI not registered!");
        }
        return api;
    }
}
