# Alert System

ServerPulse includes a robust alert system that can notify you about important server events through Discord or Telegram. This guide covers setting up and customizing alerts.

## Contact Point Setup

ServerPulse supports multiple notification channels. You can choose either Discord or Telegram (or both) for your alerts.

### Discord Integration Setup

1.  **Creating a Discord Webhook**

    1.  Open your Discord server settings.
    2.  Navigate to "Integrations" → "Webhooks".
    3.  Click "Create Webhook".
    4.  Choose a name and channel for alerts.
    5.  Copy the webhook URL.

2.  **Configuring Discord Integration**

    1.  Open `infra/grafana/provisioning/alerting/discord_contact.yml`.

    2.  Replace the example webhook URL with yours:

        ```yaml
        apiVersion: 1
        contactPoints:
          - orgId: 1
            name: Discord contact point
            receivers:
              - uid: deiz0m4w2afpcb
                type: discord
                settings:
                  url: https://discord.com/api/webhooks/your-webhook  # Replace this
                  message: '{{ template "discord.default.message" . }}'
                  title: '{{ template "default.title" . }}'
        ```

### Telegram Integration Setup

1.  **Creating a Telegram Bot**

    1.  Open Telegram and search for "@BotFather".
    2.  Start a chat and send the command `/newbot`.
    3.  Follow the instructions to create your bot.
    4.  Copy the bot token provided by BotFather.

2.  **Getting Your Chat ID**

    1.  Add your bot to a group or start a private conversation with it.
    2.  Send any message to the bot.
    3.  Visit `https://api.telegram.org/bot<YourBOTToken>/getUpdates`.
    4.  Look for the "chat" object and copy the "id" value.

3.  **Configuring Telegram Integration**

    1.  Open `infra/grafana/provisioning/alerting/telegram_contact.yml`.
    2.  Replace the example values with yours:
        ```yaml
        apiVersion: 1
        contactPoints:
            - orgId: 1
              name: Telegram contact point
              receivers:
                - uid: eejlr7re61og0e
                  type: telegram
                  settings:
                    bottoken: your_bot_token      # Replace with your bot token
                    chatid: "your_chat_id"       # Replace with your chat ID
                    disable_notification: false
                    disable_web_page_preview: false
                    protect_content: false
                  disableResolveMessage: false
        ```

## Choosing Your Contact Point for Alerts

After setting up your desired contact points (Discord, Telegram, or both), you need to select which one to use for your alerts:

### Setting the Default Contact Point

1.  **Edit the Contact Policy**

    1.  Open `infra/grafana/provisioning/alerting/contact_policy.yml`.
    2.  Change the `receiver` value to your preferred contact point:
        ```yaml
        apiVersion: 1
        policies:
          - orgId: 1
            receiver: Discord contact point  # Change to "Telegram contact point" if preferred
            group_wait: 0s
            group_interval: 30s
            repeat_interval: 3m
        ```

2.  **Update Alert Rules**

    1.  Open `infra/grafana/provisioning/alerting/metrics.yml`.
    2.  For each alert rule, update the `receiver` in the notification settings:
        ```yaml
        notification_settings:
          receiver: Discord contact point  # Change to "Telegram contact point" if preferred
        ```

3.  **Apply Changes**

    ```bash
    docker compose down
    docker compose up -d
    ```

## Creating Multiple Contact Policies

If you want to use different contact points for different alerts, you can create multiple policies:

1.  **Edit the Contact Policy**

    ```yaml
    apiVersion: 1
    policies:
      - orgId: 1
        name: Critical Alerts     # Add a name for clarity
        receiver: Discord contact point
        group_wait: 0s
        group_interval: 30s
        repeat_interval: 3m
        matcher:
          - name: severity
            value: critical
      - orgId: 1
        name: Warning Alerts      # Add a name for clarity
        receiver: Telegram contact point
        group_wait: 0s
        group_interval: 1m
        repeat_interval: 5m
        matcher:
          - name: severity
            value: warning
    ```

2.  **Set Alert Severity Labels**
    When creating alerts, add a label "severity" with value "critical" or "warning" to route them to the correct contact point.

## Default Alert Rules

ServerPulse comes with pre-configured alerts:

### TPS Monitoring

The default alert triggers when TPS drops below 18, evaluating every 10 seconds with 5-minute historical context.

## Creating Alerts via Grafana UI

Most server administrators will find it easier to create and manage alerts directly through the Grafana user interface.

### Accessing Alert Management

1.  Log in to your Grafana instance (typically http://localhost:3000).
2.  In the left sidebar, click on the bell icon (Alerting).
3.  This opens the Alerting page where you can manage all your alerts.

### Creating a New Alert Rule

1.  From the Alerting page, click on "Alert rules" in the sidebar.
2.  Click the "New alert rule" button.
3.  Configure your alert in the three sections:

#### 1\. Define query and alert condition

1.  Select your InfluxDB data source.
2.  Write your Flux query or use the query builder:
    ```
    from(bucket: "metrics_db")
      |> range(start: v.timeRangeStart, stop: v.timeRangeStop)
      |> filter(fn: (r) => r._measurement == "minecraft_stats")
      |> filter(fn: (r) => r._field == "used_memory")
      |> filter(fn: (r) => r.server == "your-server-name")
    ```
3.  In the "Threshold" section, set the alert trigger condition:
    * Select "Is above" for memory alerts or "Is below" for TPS alerts.
    * Enter your threshold value (e.g., 18 for TPS, 80% for memory usage).

#### 2\. Add alert rule details

1.  Give your alert a descriptive name (e.g., "High Memory Usage").
2.  Set an appropriate evaluation interval (e.g., 10s for critical metrics, 1m for less critical ones).
3.  Optionally add a summary and description to provide more context.
4.  Add any labels needed for routing (e.g., `severity=critical`).

#### 3\. Add notifications

1.  Select your preferred contact point (Discord or Telegram).

2.  Configure the message template (or use the default).

3.  Set notification timing:

    * **Group interval**: How long to wait before sending an updated notification (e.g., 30s).
    * **Auto resolve**: Toggle if alerts should automatically resolve.
    * **Resolve timeout**: How long before considering an alert resolved if no longer triggering.

4.  Click "Save and exit" to activate your alert.

## Example Alerts to Create

Here are some useful alerts you might want to set up:

### Low TPS Alert

* **Query**:
  ```
  from(bucket: "metrics_db")
    |> range(start: v.timeRangeStart, stop: v.timeRangeStop)
    |> filter(fn: (r) => r._measurement == "minecraft_stats")
    |> filter(fn: (r) => r._field == "tps_1m")
    |> filter(fn: (r) => r.server == "your-server-name")
  ```
* **Condition**: Is below 18
* **Name**: "Low TPS Alert"
* **Evaluation**: Every 10s
* **Label**: severity=critical

### High Memory Usage Alert

* **Query**:
  ```
  from(bucket: "metrics_db")
    |> range(start: v.timeRangeStart, stop: v.timeRangeStop)
    |> filter(fn: (r) => r._measurement == "minecraft_stats")
    |> filter(fn: (r) => r._field == "used_memory")
    |> filter(fn: (r) => r.server == "your-server-name")
  ```
* **Condition**: Is above your threshold (e.g., 80% of your server's allocated memory).
* **Name**: "High Memory Usage"
* **Evaluation**: Every 30s
* **Label**: severity=critical

### Low Disk Space Alert

* **Query**:
  ```
  from(bucket: "metrics_db")
    |> range(start: v.timeRangeStart, stop: v.timeRangeStop)
    |> filter(fn: (r) => r._measurement == "minecraft_stats")
    |> filter(fn: (r) => r._field == "usable_disk_space")
    |> filter(fn: (r) => r.server == "your-server-name")
  ```
* **Condition**: Is below your threshold (e.g., 10GB).
* **Name**: "Low Disk Space"
* **Evaluation**: Every 5m
* **Label**: severity=warning

## Testing Alerts

1.  Simulate trigger conditions:

    * For TPS: Use a plugin or command to stress test the server.
    * For memory: Load a lot of chunks or spawn many entities.
    * For disk space: Create large temporary files in your server directory.

2.  Verify Integration:

    * Check Discord channel or Telegram chat for alert messages.
    * Confirm formatting and content.

## Troubleshooting

If alerts aren't working:

1.  Check webhook URL or bot token for typos.
2.  Verify Grafana can reach the Discord/Telegram API.
3.  Confirm your alert conditions are correctly configured.
4.  Look for error messages in Grafana's alert history.
5.  Test the contact point by sending a test notification.
6.  Ensure the correct contact point is selected in your alert rules and policies.