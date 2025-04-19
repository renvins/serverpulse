package it.renvins.serverpulse.api;

public class ServerPulseProvider {

    private static ServerPulseAPI api;

    /**
     * Registers the ServerPulseAPI instance.
     *
     * @param api The ServerPulseAPI instance to register.
     * @throws IllegalStateException if the API is already registered.
     */
    public static void register(ServerPulseAPI api) {
        if (ServerPulseProvider.api != null) {
            throw new IllegalStateException("ServerPulseAPI already registered!");
        }
        ServerPulseProvider.api = api;
    }

    /**
     * Unregisters the ServerPulseAPI instance.
     *
     * @throws IllegalStateException if the API is not registered.
     */
    public static void unregister() {
        if (api == null) {
            throw new IllegalStateException("ServerPulseAPI not registered!");
        }
        ServerPulseProvider.api = null;
    }

    /**
     * Retrieves the registered ServerPulseAPI instance.
     *
     * @return The registered ServerPulseAPI instance.
     * @throws IllegalStateException if the API is not registered.
     */
    public static ServerPulseAPI get() {
        if (api == null) {
            throw new IllegalStateException("ServerPulseAPI not registered!");
        }
        return api;
    }
}
