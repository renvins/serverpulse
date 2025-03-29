# ServerPulse

ServerPulse is an **open‑source**, real‑time performance monitoring tool for Paper Minecraft servers. It will collect key server metrics **(TPS, CPU usage, heap memory, online player count, entities, chunks)** and store them in InfluxDB for visualization in Grafana.

![ServerPulse Grafana Dashboard Example](img/dashboard.png)  ---

## 📖 What This Project Is

- **Goal:** Provide an extensible, lightweight plugin to gather server metrics and expose them via a time‑series database + dashboard
- **Tech stack:**
    - Java (Paper plugin) → InfluxDB
    - Grafana dashboard (preconfigured via provisioning)
    - Docker Compose (for InfluxDB & Grafana setup)

---

## 🚀 Getting Started

Follow these steps to set up and run the ServerPulse monitoring environment:

### Prerequisites

* Docker and Docker Compose installed on your machine.
* A Minecraft server running Paper (or compatible forks) where you can install the plugin.
* Git for cloning the repository (optional if you download the ZIP).

### 1. InfluxDB Setup and Token Configuration

The system uses InfluxDB to store metrics and Grafana to visualize them. Configuring an access token for InfluxDB is essential.

1.  **Start InfluxDB and Grafana Services:**
    In the `infra` directory of the project, run the command:
    ```bash
    docker compose up -d
    ```
    This will start the InfluxDB and Grafana containers in the background.

2.  **Access InfluxDB UI:**
    Open your browser and navigate to `http://localhost:8086`. Complete the initial InfluxDB setup if it's your first time launching it (create a user, password, and initial organization - you can use `my-org` as the organization name for consistency with the configuration files).

3.  **Create the Bucket:**
    Within the InfluxDB user interface, create a new **Bucket** named `metrics_db`. All metrics collected by the plugin will be stored here.

4.  **Generate an API Token:**
    Still in the InfluxDB UI, go to the API Tokens section and generate a new token. Ensure this token has **Read** and **Write** permissions for the `metrics_db` bucket. Copy this token; you'll need it in the next step. **Treat this token like a password; do not share it publicly.**

5.  **Update Configuration Files:**
    You need to replace the placeholder `"my-token"` with the token you just generated in **two** files:
    * **Grafana Datasource Configuration:** Edit the file `infra/grafana/provisioning/datasources/influx.yml` at the line `token: my-token`.
    * **Plugin Configuration:** Edit the file `plugin/src/main/resources/config.yml` **before building**, OR (recommended method for end-users) start the Minecraft server with the plugin installed once, then edit the `config.yml` file that will be created in your server's `plugins/ServerPulse/` folder. Replace `my-token` with your actual InfluxDB token.

    *Note:* You might need to restart the Docker containers after modifying Grafana's provisioning file for the changes to take effect (`docker compose down && docker compose up -d` in the `infra` directory). If you modify the plugin's `config.yml` while the Minecraft server is running, you may need to restart the server or reload the plugin (if supported).

### 2. Build and Install the Plugin

1.  **Build the Plugin:** (If you don't already have the JAR file)
    Run the Gradle command to build the plugin JAR. From the project root directory:
    ```bash
    ./gradlew shadowJar
    ```
    You will find the resulting JAR file in `plugin/build/libs/`.

2.  **Installation:**
    Copy the ServerPulse plugin JAR file (e.g., `serverpulse-plugin-x.y.z.jar`) into the `plugins` folder of your Paper Minecraft server.

### 3. Start and Access

1.  **Start your Minecraft server.** The ServerPulse plugin will load automatically. If it's the first time, it will create the `config.yml` file in its data folder (`plugins/ServerPulse/`). Ensure you have correctly configured the InfluxDB token in that file (see step 1.5). Once configured, the plugin will start sending metrics to InfluxDB at the configured interval. Check the server console for any error messages from the plugin.

2.  **Access Grafana:**
    Open your browser and navigate to `http://localhost:3000`.
    * The default credentials for Grafana are usually `admin` / `admin` (you will be prompted to change the password on first login).
    * The InfluxDB datasource and a preconfigured dashboard ("Bed1's metrics" or similar) should already be available thanks to the provisioning files. You might need to wait a few minutes for the first metrics to appear after the server starts and the token is correctly configured.

---

## 🎨 Custom Dashboards & Visualization

While ServerPulse provides a preconfigured dashboard as a starting point, the real power comes from creating your own visualizations in Grafana!

* **Explore Grafana:** Log in to your Grafana instance (`http://localhost:3000`) and explore its interface. You can edit the existing dashboard or create entirely new ones.
* **Create Panels:** Add new panels to your dashboards to visualize specific metrics. Grafana offers various panel types (graphs, gauges, tables, etc.).
* **Query Your Data:** When configuring a panel, you'll use the **InfluxDB datasource** and write **Flux queries** to retrieve the data you want to display. All metrics sent by the ServerPulse plugin are available in the `metrics_db` bucket within the `minecraft_stats` measurement. You can filter by metric (`_field`) and tags (like `server`).

Feel free to experiment and build dashboards tailored to the specific metrics you care about most!

---

## 🤝 Contributing

We welcome all contributions — bug reports, feature proposals, pull requests, or simply feedback.

1.  Fork this repository
2.  Create a feature branch (`git checkout -b feature/awesome-idea`)
3.  Commit your changes with clear, descriptive messages
4.  Open a Pull Request against `main`

Please maintain the same design of authors' code.
