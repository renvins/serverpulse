# Developer API

ServerPulse provides a comprehensive API that allows other plugins to interact with and extend its functionality. This guide covers how to integrate with ServerPulse and use its features in your plugins.

---

## Adding ServerPulse as a Dependency

To use the ServerPulse API, you need to add it to your project's dependencies.

### Plugin Dependency
First, declare a dependency on ServerPulse in your `plugin.yml` (for Bukkit/BungeeCord) or equivalent metadata file. This ensures your plugin loads after ServerPulse.

```yaml
# For Bukkit/BungeeCord plugin.yml
depend: [ServerPulse]          # Hard dependency
# OR
softdepend: [ServerPulse]      # Soft dependency
````

### Maven Dependency

Add the JitPack repository and the API dependency to your `pom.xml`.

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>[https://jitpack.io](https://jitpack.io)</url>
    </repository>
</repositories>

<dependency>
    <groupId>com.github.renvins.serverpulse</groupId>
    <artifactId>api</artifactId>
    <version>[VERSION]</version> <scope>provided</scope>
</dependency>
```

### Gradle Dependency

Add the JitPack repository and the API dependency to your `build.gradle.kts`.

```kotlin
repositories {
    mavenCentral()
    maven { url = uri("[https://jitpack.io](https://jitpack.io)") }
}

dependencies {
    // Replace with the desired release version
    compileOnly("com.github.renvins.serverpulse:api:[VERSION]")
}
```

-----

## Getting Started

### Accessing the API

The `ServerPulseAPI` is accessible via the static `ServerPulseProvider` class. It's good practice to check if the ServerPulse plugin is enabled before accessing the API.

```java
import it.renvins.serverpulse.api.ServerPulseAPI;
import it.renvins.serverpulse.api.ServerPulseProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class YourPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        // Check if ServerPulse is available on the server
        if (getServer().getPluginManager().getPlugin("ServerPulse") != null) {
            try {
                ServerPulseAPI api = ServerPulseProvider.get();
                // You can now use the API
                getLogger().info("Successfully hooked into ServerPulse API!");
            } catch (IllegalStateException e) {
                // This will be thrown if the API is not registered
                getLogger().warning("ServerPulse API is not available: " + e.getMessage());
            }
        } else {
            getLogger().warning("ServerPulse plugin not found!");
        }
    }
}
```

-----

## Core API Components

The `ServerPulseAPI` interface provides access to all public-facing services of the plugin.

### Database Service

The `IDatabaseService` interface provides methods for interacting with InfluxDB.

```java
IDatabaseService dbService = api.getDatabaseService();

// Check the current connection status to InfluxDB
boolean isConnected = dbService.isConnected();
getLogger().info("Connection status: " + isConnected);

// Send a custom data point using InfluxDB's Line Protocol format
LineProtocolPoint point = new LineProtocolPoint("custom_stats")
    .addTag("plugin", "YourPluginName")
    .addField("custom_value", 123)
    .setTimestamp(System.currentTimeMillis() * 1_000_000); // Timestamp in nanoseconds

// The write operation is asynchronous and returns a CompletableFuture
dbService.writeLineProtocol(point.toLineProtocol()).thenAccept(success -> {
    if (success) {
        getLogger().info("Successfully wrote custom metric to InfluxDB.");
    } else {
        getLogger().warning("Failed to write custom metric to InfluxDB.");
    }
});
```

### Metrics Service

The `IMetricsService` interface allows you to interact with the metric collection process.

```java
IMetricsService metricsService = api.getMetricsService();

// Manually trigger a metric collection and sending cycle
// This will gather all standard ServerPulse metrics and send them to InfluxDB
metricsService.collectAndSendMetrics();
```

### Retrievers

ServerPulse provides several retrievers to get specific metrics on demand.

#### Disk Metrics

The `IDiskRetriever` interface provides information about the server's disk space.

```java
IDiskRetriever diskRetriever = api.getDiskRetriever();

// Get total and usable disk space in bytes
long totalSpace = diskRetriever.getTotalSpace();
long usableSpace = diskRetriever.getUsableSpace();

getLogger().info("Disk space: " + usableSpace / (1024*1024) + " MB free out of " + totalSpace / (1024*1024) + " MB");
```

#### Player Ping

The `IPingRetriever` interface provides statistics about player latency.

```java
IPingRetriever pingRetriever = api.getPingRetriever();

// Get min, max, and average ping for all online players
int minPing = pingRetriever.getMinPing();
int maxPing = pingRetriever.getMaxPing();
int avgPing = pingRetriever.getAveragePing();

getLogger().info("Player ping (ms): Min=" + minPing + ", Avg=" + avgPing + ", Max=" + maxPing);
```

-----

## Example: Custom Metrics Plugin

Here's a complete example of a simple Bukkit plugin that hooks into ServerPulse to send custom data to InfluxDB.

```java
import it.renvins.serverpulse.api.ServerPulseAPI;
import it.renvins.serverpulse.api.ServerPulseProvider;
import it.renvins.serverpulse.api.data.LineProtocolPoint;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Random;

public class CustomMetricsPlugin extends JavaPlugin {

    private ServerPulseAPI api;
    private final Random random = new Random();

    @Override
    public void onEnable() {
        if (!setupServerPulse()) {
            getLogger().severe("ServerPulse not found. This plugin requires ServerPulse to function.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        startMetricsTask();
    }

    private boolean setupServerPulse() {
        Plugin serverPulsePlugin = getServer().getPluginManager().getPlugin("ServerPulse");
        if (serverPulsePlugin == null) {
            return false;
        }

        try {
            api = ServerPulseProvider.get();
            return true;
        } catch (IllegalStateException e) {
            getLogger().severe("Failed to get ServerPulse API: " + e.getMessage());
            return false;
        }
    }

    private void startMetricsTask() {
        // Run a task every minute (1200 ticks) to send custom metrics
        getServer().getScheduler().runTaskTimerAsynchronously(this, () -> {
            if (api == null || !api.getDatabaseService().isConnected()) {
                return; // Don't try to send metrics if not connected
            }

            // Create a custom data point
            LineProtocolPoint point = new LineProtocolPoint("my_custom_metrics")
                .addTag("plugin", "CustomMetricsPlugin")
                .addField("random_value", random.nextInt(100))
                .addField("some_other_data", getSomeData())
                .setTimestamp(System.currentTimeMillis() * 1_000_000);

            // Send the data using the API
            api.getDatabaseService().writeLineProtocol(point.toLineProtocol());

        }, 0L, 1200L);
    }

    private String getSomeData() {
        // Your logic to get custom data
        return "example_data";
    }
}
```