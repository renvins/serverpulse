package it.renvins.serverpulse.e2e;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.File;
import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Testcontainers
public class ServerE2ETest {

    private static final Logger logger = LoggerFactory.getLogger(ServerE2ETest.class);

    @Container
    public static DockerComposeContainer<?> environment =
            new DockerComposeContainer<>(new File("src/test/resources/docker-compose.test.yml"))
                    .withExposedService("influxdb", 8086,
                            Wait.forHttp("/ping").forStatusCode(204)
                                    .withStartupTimeout(Duration.ofMinutes(2)))
                    .withExposedService("grafana", 3000,
                            Wait.forHttp("/api/health").forStatusCode(200)
                                    .withStartupTimeout(Duration.ofMinutes(2)))
                    .withExposedService("minecraft-server", 25565,
                            Wait.forLogMessage(".*Done \\(.*\\)! For help, type \"help\".*", 1)
                                    .withStartupTimeout(Duration.ofMinutes(5)))
                    .withLogConsumer("grafana", new Slf4jLogConsumer(logger).withPrefix("GRAFANA"))
                    .withLogConsumer("influxdb", new Slf4jLogConsumer(logger).withPrefix("INFLUXDB"))
                    .withLogConsumer("minecraft-server", new Slf4jLogConsumer(logger).withPrefix("MINECRAFT"));

    @Test
    void testServerMetrics() throws Exception {
        System.out.println("Starting E2E test for Minecraft server metrics...");
        System.out.println("Starting the bot to connect to the Minecraft server...");

        ExecutorService executor = Executors.newSingleThreadExecutor();
        MinecraftBot bot = new MinecraftBot("localhost", 25565, "renvins-bot", 10);
        Future<?> botTask = executor.submit(bot);

        System.out.println("Environment ready, metrics should be collected now.");
        System.out.println("Look at Grafana at http://localhost:3000 to see the metrics.");

        botTask.get(3, TimeUnit.MINUTES);

        executor.shutdownNow();
        System.out.println("Test completed, shutting down.");
    }
}
