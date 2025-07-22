package it.renvins.serverpulse.e2e;

import org.geysermc.mcprotocollib.network.ClientSession;
import org.geysermc.mcprotocollib.network.event.session.ConnectedEvent;
import org.geysermc.mcprotocollib.network.event.session.DisconnectedEvent;
import org.geysermc.mcprotocollib.network.event.session.SessionAdapter; // FIX: Importa SessionAdapter
import org.geysermc.mcprotocollib.network.factory.ClientNetworkSessionFactory;
import org.geysermc.mcprotocollib.protocol.MinecraftProtocol;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class MinecraftBot implements Runnable {

    private final String host;
    private final int port;
    private final String username;
    private final int durationMinutes;

    public MinecraftBot(String host, int port, String username, int durationMinutes) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.durationMinutes = durationMinutes;
    }

    /**
     * Metodo principale che esegue la logica del bot.
     */
    @Override
    public void run() {
        // 1. Crea il client
        ClientSession client = ClientNetworkSessionFactory.factory()
                .setAddress(host, port)
                .setProtocol(new MinecraftProtocol(username))
                .create();

        // 2. Connettiti e attendi che la connessione sia stabilita
        if (!connectAndWait(client, 30)) {
            System.err.println("❌ [Bot] Connessione fallita. Il test del bot termina.");
            return;
        }

        // 3. Mantieni il bot online per la durata del test
        System.out.println("📊 [Bot] " + username + " rimarrà connesso per " + durationMinutes + " minuti.");
        try {
            TimeUnit.MINUTES.sleep(durationMinutes);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            // 4. Disconnetti il bot alla fine
            if (client.isConnected()) {
                client.disconnect("Test completato.");
                System.out.println("🚪 [Bot] " + username + " si è disconnesso.");
            }
        }
    }

    /**
     * Tenta di connettere il client e attende in modo sincrono fino al successo
     * o al timeout.
     *
     * @param client  La sessione del client da connettere.
     * @param timeoutSeconds Il tempo massimo di attesa in secondi.
     * @return true se la connessione ha avuto successo, altrimenti false.
     */
    private boolean connectAndWait(ClientSession client, int timeoutSeconds) {
        CountDownLatch connectionLatch = new CountDownLatch(1);

        // FIX: Usa SessionAdapter invece di SessionListener per evitare di implementare tutti i metodi
        client.addListener(new SessionAdapter() {
            @Override
            public void connected(ConnectedEvent event) {
                System.out.println("✅ [Bot] Connesso con successo!");
                connectionLatch.countDown();
            }

            @Override
            public void disconnected(DisconnectedEvent event) {
                System.err.println("🔌 [Bot] Disconnesso inaspettatamente: " + event.getReason());
                connectionLatch.countDown();
            }
        });

        System.out.println("🤖 [Bot] Tentativo di connessione a " + host + ":" + port);
        client.connect();

        try {
            if (!connectionLatch.await(timeoutSeconds, TimeUnit.SECONDS)) {
                System.err.println("❌ [Bot] Timeout: connessione non riuscita entro " + timeoutSeconds + " secondi.");
                return false;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }

        return client.isConnected();
    }
}
