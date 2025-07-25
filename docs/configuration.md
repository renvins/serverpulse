### ServerPulse Configuration Guide

This guide covers all configuration options available in ServerPulse across all supported platforms: Bukkit/Paper, BungeeCord, Velocity, and Fabric.

### Configuration Files Locations

Each platform stores its configuration in a different location:

* **Bukkit/Paper**: `plugins/ServerPulse/config.yml`
* **BungeeCord**: `plugins/ServerPulse/config.yml`
* **Velocity**: `plugins/serverpulse/config.yml`
* **Fabric**: `config/serverpulse/config.yml`

### Common Configuration Structure

All platforms share the same core configuration structure with minor variations in the messages section:

```yaml
metrics:
  interval: 5              # Collection interval in seconds
  influxdb:
    url: http://localhost:8086  # InfluxDB API URL
    org: my-org              # Organization name
    bucket: metrics_db       # Bucket for storing metrics
    token: my-token          # API token (replace this!)
    table: minecraft_stats   # Measurement name in InfluxDB
  tags:
    server: "server-id"      # Server identifier (change this for each server)
messages:
  # Message configurations vary by platform (see below)
```

### Platform-Specific Configurations

#### Bukkit/Paper Configuration

Default configuration with explanations:

```yaml
metrics:
  interval: 5
  influxdb:
    url: http://localhost:8086
    org: my-org
    bucket: metrics_db
    token: my-token
    table: minecraft_stats
  tags:
    server: "bed1"      # Change to a unique identifier for your server
messages:
  noPerms: "&7[&bServer&7Pulse] &7You don't have &bpermission &7to use this &bcommand&7."
  reloadConfig: "&7[&bServer&7Pulse] &7Configuration &breloaded&7."
  reloadConfigError: "&7[&bServer&7Pulse] &7Error &breloading &7configuration..."
  noArgs: "&7[&bServer&7Pulse] &7You need to specify a &bcommand&7: &breload&7, &bstatus&7."
  playerOnly: "&7[&bServer&7Pulse] &7This command can only be used by &bplayers&7."
  noCommand: "&7[&bServer&7Pulse] &7This command is not &bavailable&7."
  reloadConfigUsage: "&7[&bServer&7Pulse] &7Usage: &b/serverpulse reload&7."
  statusConfigUsage: "&7[&bServer&7Pulse] &7Usage: &b/serverpulse status&7."
  statusConnected: "&7[&bServer&7Pulse] &7Connected to &bInfluxDB&7."
  statusNotConnected: "&7[&bServer&7Pulse] &7Not connected to &bInfluxDB&7."
```

#### BungeeCord Configuration

```yaml
metrics:
  interval: 5
  influxdb:
    url: http://localhost:8086
    org: my-org
    bucket: metrics_db
    token: my-token
    table: minecraft_stats
  tags:
    server: "bungeecord"      # Change to a unique identifier for your proxy
messages:
  noPerms: "&7[&bServer&7Pulse] &7You don't have &bpermission &7to use this &bcommand&7."
  reloadConfig: "&7[&bServer&7Pulse] &7Configuration &breloaded&7."
  reloadConfigError: "&7[&bServer&7Pulse] &7Error &breloading &7configuration..."
  noArgs: "&7[&bServer&7Pulse] &7You need to specify a &bcommand&7: &breload&7, &bstatus&7."
  playerOnly: "&7[&bServer&7Pulse] &7This command can only be used by &bplayers&7."
  noCommand: "&7[&bServer&7Pulse] &7This command is not &bavailable&7."
  reloadConfigUsage: "&7[&bServer&7Pulse] &7Usage: &b/serverpulse reload&7."
  statusConfigUsage: "&7[&bServer&7Pulse] &7Usage: &b/serverpulse status&7."
  statusConnected: "&7[&bServer&7Pulse] &7Connected to &bInfluxDB&7."
  statusNotConnected: "&7[&bServer&7Pulse] &7Not connected to &bInfluxDB&7."
```

#### Velocity Configuration

```yaml
metrics:
  interval: 5
  influxdb:
    url: http://localhost:8086
    org: my-org
    bucket: metrics_db
    token: my-token
    table: minecraft_stats
  tags:
    server: "velocity1"   # Change to a unique identifier for your proxy
messages:
  usage: "&7[&bServer&7Pulse] &7Please use &b/serverpulsevelocity [status|reload]&7."
  reloadConfig: "&7[&bServer&7Pulse] &7Configuration &breloaded&7."
  reloadConfigError: "&7[&bServer&7Pulse] &7Error &breloading &7configuration..."
  statusConnected: "&7[&bServer&7Pulse] &7Connected to &bInfluxDB&7."
  statusNotConnected: "&7[&bServer&7Pulse] &7Not connected to &bInfluxDB&7."
```

#### Fabric Configuration

```yaml
metrics:
  interval: 5
  influxdb:
    url: http://localhost:8086
    org: my-org
    bucket: metrics_db
    token: my-token
    table: minecraft_stats
  tags:
    server: "fabric1"     # Change to a unique identifier for your Fabric server
messages:
  usage: "&7[&bServer&7Pulse] &7Please use &b/serverpulsevelocity [status|reload]&7."
  reloadConfig: "&7[&bServer&7Pulse] &7Configuration &breloaded&7."
  reloadConfigError: "&7[&bServer&7Pulse] &7Error &breloading &7configuration..."
  statusConnected: "&7[&bServer&7Pulse] &7Connected to &bInfluxDB&7."
  statusNotConnected: "&7[&bServer&7Pulse] &7Not connected to &bInfluxDB&7."
```

### Metrics Settings

#### Main Options

* `metrics.interval`: How often metrics are collected and sent.
    * Value is in seconds.
    * Recommended: 5-15 seconds.
    * A lower value provides more granular data but may increase server impact.

### InfluxDB Connection

#### Required Settings

* `metrics.influxdb.url`: The full URL of your InfluxDB v2 instance, including the port.

    * Format: `http://hostname:port`
    * The default port for InfluxDB is `8086`.
    * Use `localhost` if ServerPulse is running on the same machine as InfluxDB.

* `metrics.influxdb.org`: The organization name you created during the InfluxDB setup.

    * Default used in documentation: `my-org`.

* `metrics.influxdb.bucket`: The name of the bucket where metrics will be stored.

    * This bucket must exist in your InfluxDB organization.
    * Default used in documentation: `metrics_db`.

* `metrics.influxdb.token`: The API token for authentication.

    * This token must have **write** and **read** permissions for the specified bucket.
    * **Important**: Treat this token like a password. Do not share it publicly.

* `metrics.influxdb.table`: The name for the measurement where your server data is stored in InfluxDB.

    * Default: `minecraft_stats`.
    * It is recommended to keep the default value unless you have a specific reason to change it.

### Tags System

Tags are used to label and organize your data, making it easy to filter and group in Grafana.

#### Server Tag

The `metrics.tags.server` setting is crucial for identifying metrics from different servers. You must use a unique identifier for each server instance you monitor.

* For Bukkit/Paper servers:

  ```yaml
  metrics:
    tags:
      server: "survival-1"
  ```

* For BungeeCord proxies:

  ```yaml
  metrics:
    tags:
      server: "bungeecord1"
  ```

* For Velocity proxies:

  ```yaml
  metrics:
    tags:
      server: "velocity1"
  ```

* For Fabric servers:

  ```yaml
  metrics:
    tags:
      server: "fabric1"
  ```

#### Custom Tags

You can add any other custom tags under the `metrics.tags` section to add more dimensions to your data.

```yaml
metrics:
  tags:
    server: "survival-1"
    region: "eu-west"      # e.g., Geographic location
    type: "survival"       # e.g., Server gamemode
    network: "main"        # e.g., Network identifier
```

These tags are automatically applied to all metrics sent from the server and can be used for advanced filtering in your Grafana dashboards.

### Messages Configuration

All platforms support customizing the in-game messages sent by the plugin's commands. Messages support standard Minecraft color codes using the `&` symbol. These messages are reloaded when you use the reload command for your platform.

### Platform-Specific Considerations

#### Bukkit/Paper Specific Settings

The Bukkit/Paper implementation automatically detects if you're running Paper (or a fork) and uses the native `Bukkit.getTPS()` method for higher accuracy. If you're running standard Bukkit or Spigot, it falls back to a custom tick-monitoring implementation to calculate TPS.

#### BungeeCord/Velocity Specific Settings

Proxy implementations like BungeeCord and Velocity focus on network-level metrics. They do not report TPS or world-related data (entities, chunks), as these metrics are not applicable to a proxy. Instead, they provide accurate player counts and ping statistics for the entire network.

#### Fabric Specific Settings

The Fabric implementation includes a custom TPS retriever that monitors server tick durations to calculate 1-minute, 5-minute, and 15-minute averages. It also reports per-world data, including entity counts and loaded chunks, using the Fabric API.

### Multi-Server Setup

When monitoring a network of servers, a consistent tagging strategy is key.

1.  Assign a unique `server` tag to each server, proxy, and mod instance.
2.  Use custom tags like `network` or `type` to group related servers.
3.  Point all ServerPulse configurations to the same InfluxDB instance.
4.  In Grafana, the pre-built dashboards are designed to filter by the `server` tag, allowing you to view metrics for each server individually.

#### Sample Multi-Server Config

Here's an example of how to configure ServerPulse in a network with multiple server types:

* **Main Velocity Proxy** (`velocity/plugins/serverpulse/config.yml`):

  ```yaml
  metrics:
    tags:
      server: "proxy-main"
      type: "proxy"
      network: "main-network"
  ```

* **Bukkit Survival Server** (`bukkit/plugins/ServerPulse/config.yml`):

  ```yaml
  metrics:
    tags:
      server: "survival-1"
      type: "survival"
      network: "main-network"
  ```

* **Fabric Creative Server** (`fabric/config/serverpulse/config.yml`):

  ```yaml
  metrics:
    tags:
      server: "creative-fabric"
      type: "creative"
      network: "main-network"
  ```

This structure allows you to filter metrics in Grafana by server type, specific server, or the entire network.

### Verifying Configuration

After applying your configuration changes, restart your server and use the status command to verify the connection to InfluxDB.

* For Bukkit/Paper: `/serverpulse status`
* For BungeeCord: `/serverpulsebungeecord status` (aliases: `/sp`, `/spb`)
* For Velocity: `/serverpulsevelocity status` (alias: `/spv`)
* For Fabric: `/serverpulse status`

All commands require the `serverpulse.status` permission and will confirm whether the connection to InfluxDB is active.