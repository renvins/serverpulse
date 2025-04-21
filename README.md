# ServerPulse

ServerPulse is an **open-source**, real-time performance monitoring tool for Paper Minecraft servers. It collects key server metrics (TPS, disk usage, memory, player count, entities, chunks, ping) and visualizes them through an integrated Grafana dashboard.

<details>
<summary>📊 View Dashboard Examples</summary>

![ServerPulse Grafana Dashboard Example1](img/dashboard.png)
*Example dashboard view 1: General Server Overview*

![ServerPulse Grafana Dashboard Example2](img/dashboard2.png)
*Example dashboard view 2: Per-World Details*

</details>

## 📖 What This Project Is

- **Goal:** Provide an extensible, lightweight plugin to gather server metrics and store them in InfluxDB for visualization with Grafana.
- **Tech stack:**
    - Java (Paper plugin) → InfluxDB
    - Grafana dashboard (preconfigured via provisioning)
    - Discord alerts for key server metrics
    - Docker Compose (for InfluxDB & Grafana setup)

## Why Choose ServerPulse?

ServerPulse isn't just another metrics exporter - it offers several unique advantages:

* **Complete Monitoring Stack**: Fully integrated solution with InfluxDB (optimized for time-series data) and pre-configured Grafana dashboards
* **Per-World Analytics**: Track entity counts, chunk loading, and performance metrics separately for each world
* **Flexible Tagging System**: Group and filter metrics by server, network, region, or any custom dimension through simple configuration
* **Zero-Configuration Dashboards**: Auto-provisioned Grafana dashboards - no manual setup required
* **Alert Notifications**: Integrated Discord alerts for critical server metrics (TPS drops, memory issues, etc.)
* **Production-Ready Infrastructure**: Built-in health checks, connection retry mechanisms, and proper error handling
* **Docker-First Deployment**: Single command deployment with Docker Compose for the entire monitoring stack

## 📚 Documentation

**For setup guides, configuration instructions, API examples, and developer information, please visit our [Wiki](https://github.com/renvins/serverpulse/wiki).**

The wiki contains comprehensive documentation on:
- Installation and setup instructions
- Configuration options and customization
- Discord alerts configuration
- Custom dashboard creation
- Developer API examples
- Contributing guidelines

## 📊 Comparison with Alternative Solutions

| Feature | ServerPulse | Generic Prometheus Exporters |
|---------|------------|--------------------------|
| Setup Time | ~5 minutes with Docker Compose | Manual metrics + Prometheus + Grafana setup |
| Dashboard Configuration | Pre-configured, auto-provisioned | Manual dashboard creation |
| Data Storage | InfluxDB (optimized for time-series) | Prometheus (general-purpose) |
| Per-World Metrics | Built-in | Usually not available |
| Custom Tagging | Flexible tag system | Limited labeling |
| Alert System | Discord integration | Requires manual setup |
| Infrastructure | Complete stack included | Manual integration required |
| Health Monitoring | Automated health checks | Varies by implementation |

## 🤝 Contributing

We welcome all contributions — bug reports, feature proposals, pull requests, or simply feedback. Read [Contributing](https://github.com/renvins/serverpulse/wiki/7.-Contributing-guidelines)

## 📄 License

ServerPulse is licensed under the GNU General Public License v3.0.
