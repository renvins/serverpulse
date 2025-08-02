### ServerPulse Installation Guide 📊

This guide provides a comprehensive walkthrough for setting up **ServerPulse**, a real-time performance monitoring tool for Minecraft servers. You'll learn how to set up the monitoring infrastructure with **InfluxDB** and **Grafana** using Docker, and how to install and configure the ServerPulse plugin on your Minecraft server.

-----

### Prerequisites

Before you begin, make sure you have the following installed on your system:

* **Docker and Docker Compose**: These tools are essential for running the monitoring infrastructure. If you don't have them, you can download them from the official Docker website.
* **Java 21 or newer**: This is required to run the ServerPulse plugin on your Minecraft server.
* A Minecraft server:
    * Bukkit/Spigot/Paper
    * BungeeCord
    * Velocity
    * Fabric

-----

### Step 1: Setting Up the Monitoring Infrastructure 🏗️

The monitoring infrastructure consists of **InfluxDB** for data storage and **Grafana** for data visualization. ServerPulse provides a Docker Compose setup to get you up and running quickly.

1.  **Download ServerPulse**:

    * You can either download the latest release from the [ServerPulse GitHub repository](https://github.com/renvins/serverpulse) or clone it using Git.

2.  **Navigate to the `infra` Directory**:

    * Open a terminal or command prompt and navigate to the `infra` directory within the downloaded ServerPulse files.

3.  **Start the Docker Containers**:

    * Run the following command to start the InfluxDB and Grafana containers in the background:
      ```
      docker compose up -d
      ```
    * This command will download the necessary Docker images and start the services. You can check the status of the containers with `docker compose ps`.

-----

### Step 2: Configuring InfluxDB ⚙️

Once the InfluxDB container is running, you need to perform a one-time setup.

1.  **Access the InfluxDB UI**:

    * Open your web browser and go to `http://localhost:8086`.

2.  **Initial Setup**:

    * You will be prompted to set up your initial user. Create a **username** and **password**.
    * For the **Initial Organization Name**, enter `my-org`.
    * For the **Initial Bucket Name**, enter `metrics_db`.

3.  **Create an API Token**:

    * In the InfluxDB UI, navigate to **Load Data** \> **API Tokens**.
    * Click on **Generate API Token** and select **Custom API Token**.
    * Configure the token with the following permissions:
        * **Read** access to the `metrics_db` bucket.
        * **Write** access to the `metrics_db` bucket.
    * Give your token a descriptive name, like `serverpulse-token`.
    * **Important**: Copy the generated token and save it somewhere safe. You will need it in the next steps.

-----

### Step 3: Configuring Grafana 🎨

Grafana needs to be configured to connect to InfluxDB as a data source.

1.  **Update the Grafana Datasource Configuration**:

    * Open the file `infra/grafana/provisioning/datasources/influx.yml` in a text editor.
    * Find the line `token: my-token` and replace `my-token` with the InfluxDB API token you generated in the previous step.
    * Save the file.

2.  **Restart the Docker Containers**:

    * To apply the changes, restart the Docker containers:
      ```
      docker compose down
      docker compose up -d
      ```

3.  **Configure Alert Notifications (Optional)**:

    * ServerPulse comes with pre-configured alerts for low TPS. You can set up notifications for these alerts via Discord or Telegram.
    * **For Discord**:
        * Open `infra/grafana/provisioning/alerting/discord_contact.yml`.
        * Replace `https://discord.com/api/webhooks/your-webhook` with your Discord webhook URL.
    * **For Telegram**:
        * Open `infra/grafana/provisioning/alerting/telegram_contact.yml`.
        * Replace `your_bot_token` with your Telegram bot's token and `your_chat_id` with your Telegram chat ID.
    * After making changes, restart the Docker containers again.

-----

### Step 4: Installing the ServerPulse Plugin 🔌

Now it's time to install the ServerPulse plugin on your Minecraft server.

1.  **Download the Correct Plugin Version**:

    * Go to the [ServerPulse releases page](https://github.com/renvins/serverpulse/releases) and download the JAR file that corresponds to your server platform:
        * **Bukkit/Paper**: `serverpulse-bukkit-x.x.x.jar`
        * **BungeeCord**: `serverpulse-bungeecord-x.x.x.jar`
        * **Velocity**: `serverpulse-velocity-x.x.x.jar`
        * **Fabric**: `serverpulse-fabric-x.x.x.jar`

2.  **Install the Plugin**:

    * Copy the downloaded JAR file to the appropriate directory on your server:
        * **Bukkit/Paper/BungeeCord/Velocity**: `plugins/`
        * **Fabric**: `mods/`

3.  **First Run and Configuration**:

    * Start your Minecraft server once to generate the default configuration file.
    * The configuration file will be created in the following location:
        * **Bukkit/Paper**: `plugins/ServerPulse/config.yml`
        * **BungeeCord**: `plugins/ServerPulse/config.yml`
        * **Velocity**: `plugins/serverpulse/config.yml`
        * **Fabric**: `config/serverpulse/config.yml`
    * Stop your server.
    * Open the generated `config.yml` file and configure it as follows:

    <!-- end list -->

    * Replace `YOUR_INFLUXDB_TOKEN` with the token you saved earlier.
    * Set a unique `server` tag to identify this server in Grafana.
    * You can also customize the in-game messages in the `messages` section of the configuration file.

-----

### Step 5: Verifying the Installation ✅

1.  **Start Your Server**:

    * Start your Minecraft server again.

2.  **Check Plugin Status**:

    * In your server console or as an in-game operator, run the status command for your platform:
        * **Bukkit/Paper/Fabric**: `/serverpulse status`
        * **BungeeCord**: `/serverpulsebungeecord status`
        * **Velocity**: `/serverpulsevelocity status`
    * You should see a message indicating a successful connection to InfluxDB.

3.  **Check the Grafana Dashboard**:

    * Open Grafana in your browser at `http://localhost:3000`.
    * Log in with the default credentials (`admin`/`admin`) and change the password when prompted.
    * Navigate to **Dashboards**. You should find pre-configured dashboard.
    * Set the correct server-name to filter the dashboard to one server.
    * It may take a few minutes for data to start appearing.

-----

### Platform-Specific Features 🌟

* **Bukkit/Paper**:
    * Includes detailed TPS (Ticks Per Second) monitoring. It automatically uses Paper's native TPS retriever if available, otherwise it falls back to a custom implementation for Bukkit.
    * Provides per-world statistics for entities and loaded chunks.
    * Paper and its forks have also support for MSPT
* **BungeeCord/Velocity**:
    * Monitors player count and ping across your entire proxy network.
    * Tracks memory and disk usage of the proxy server itself.
* **Fabric**:
    * A native implementation for Fabric servers using the Fabric API.
    * Includes TPS monitoring and per-world metrics for entities and loaded chunks, similar to the Bukkit version.
    * Also fabric has support for MSPT
-----

### Troubleshooting Common Issues 🛠️

* **InfluxDB Connection Failed**:
    * Double-check that the InfluxDB token in your plugin's `config.yml` is correct.
    * Ensure that your Minecraft server can reach the InfluxDB URL specified in the config. If they are on different machines, make sure there are no firewall rules blocking the connection.
* **No Data in Grafana**:
    * Verify that the `server` tag in your `config.yml` matches the server tag used in the Grafana dashboard queries.
    * Check your server console for any error messages from the ServerPulse plugin.
* **Grafana Can't Connect to InfluxDB**:
    * Ensure that the InfluxDB container is running (`docker compose ps`).
    * Verify that the InfluxDB token in the Grafana datasource configuration is correct.
    * Try restarting the Docker containers: `docker compose restart`.

### Getting Help 🆘

If you run into any issues, you can:

* Check the [GitHub issue tracker](https://github.com/renvins/serverpulse/issues) for existing bug reports and feature requests.
* Open a new issue if you find a bug, providing as much detail as possible.
* Join the community on [Discord](https://discord.gg/jZUqcemc4G)
* For contributing to the project, please see the [contributing guidelines](https://github.com/renvins/serverpulse/blob/master/CONTRIBUTING.md).

Enjoy using ServerPulse to monitor your Minecraft server's performance\! 🎉