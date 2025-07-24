package it.renvins.serverpulse.e2e;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

@Testcontainers
public class ServerE2ETest {

    private static final Logger logger = LoggerFactory.getLogger(ServerE2ETest.class);
    private static final Path CONFIG_PATH = Paths.get("src/test/resources/config/ServerPulse/config.yml");
    private static final Path CONFIG_TEMPLATE_PATH = Paths.get("src/test/resources/config/ServerPulse/config.template.yml");

    private static Stream<TestConfig> testConfigStream() {
        return Stream.of(
                new TestConfig("fabric", "fabric-server", 25566, ".*Done \\(.*\\)! For help, type \"help\".*", "fabric1"),
                new TestConfig("bukkit", "bukkit-server", 25565, ".*Done \\(.*\\)! For help, type \"help\".*", "bed1"),
                new TestConfig("velocity", "velocity-server", 25577, ".*Done \\(.*\\)! For help, type \"help\".*", "velocity1"),
                new TestConfig("bungeecord", "bungeecord-server", 25578, ".*Listening on /0.0.0.0:25577.*", "bungeecord")
        );
    }

    @ParameterizedTest(name = "E2E Test for {0}")
    @MethodSource("testConfigStream")
    void testServerMetrics(TestConfig config) throws Exception {
        String templateContent = Files.readString(CONFIG_TEMPLATE_PATH);
        String actualContent = templateContent.replace("%%SERVER_NAME%%", config.getServerTag());
        Files.writeString(CONFIG_PATH, actualContent);

        try (DockerComposeContainer<?> environment = new DockerComposeContainer<>(new File("src/test/resources/compose/" + config.getModuleName() + ".yml"))
                .withExposedService("influxdb", 8086, Wait.forHttp("/ping").forStatusCode(204).withStartupTimeout(Duration.ofMinutes(2)))
                .withExposedService("grafana", 3000, Wait.forHttp("/api/health").forStatusCode(200).withStartupTimeout(Duration.ofMinutes(2)))
                .withExposedService(config.getServiceName(), config.getPort(), Wait.forLogMessage(config.getLogMessage(), 1).withStartupTimeout(Duration.ofMinutes(5)))
                .withLogConsumer(config.getServiceName(), new Slf4jLogConsumer(logger).withPrefix(config.getModuleName().toUpperCase()))) {

            environment.start();

            System.out.println("Environment for " + config.getModuleName() + " is running. Waiting for 2 minutes for metrics collection...");
            TimeUnit.MINUTES.sleep(2);

            System.out.println("Test for " + config.getModuleName() + " completed, shutting down.");
        } finally {
            Files.deleteIfExists(CONFIG_PATH);
        }
    }

    @RequiredArgsConstructor
    @Getter
    private static class TestConfig {
        private final String moduleName;
        private final String serviceName;

        private final int port;

        private final String logMessage;
        private final String serverTag;
    }
}