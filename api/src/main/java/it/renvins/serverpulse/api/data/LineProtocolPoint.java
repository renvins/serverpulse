package it.renvins.serverpulse.api.data;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a single point in the InfluxDB line protocol format.
 * This class is used to construct a line protocol point with measurement, tags, fields, and timestamp.
 */
public class LineProtocolPoint {

    private final String measurement;
    private final Map<String, String> tags;
    private final Map<String, Object> fields;
    private long timestamp;

    public LineProtocolPoint(String measurement) {
        this.measurement = measurement;
        this.tags = new HashMap<>();
        this.fields = new HashMap<>();
    }

    public LineProtocolPoint addTag(String key, String value) {
        tags.put(key, value);
        return this;
    }

    public LineProtocolPoint addField(String key, Object value) {
        fields.put(key, value);
        return this;
    }

    public LineProtocolPoint setTimestamp(long timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public String toLineProtocol() {
        StringBuilder sb = new StringBuilder();
        sb.append(measurement);

        if (!tags.isEmpty()) {
            sb.append(",");
            tags.forEach((key, value) -> sb.append(key).append("=").append(value).append(","));
            sb.setLength(sb.length() - 1); // Remove the last comma
        }

        sb.append(" ");

        if (!fields.isEmpty()) {
            fields.forEach((key, value) -> {
                sb.append(key).append("=");
                if (value instanceof String) {
                    sb.append("\"").append(value).append("\"");
                } else if (value instanceof Integer || value instanceof Long) {
                    sb.append(value).append("i");
                } else {
                    sb.append(value);
                }
                sb.append(",");
            });
            sb.setLength(sb.length() - 1); // Remove the last comma
        }

        sb.append(" ").append(timestamp);

        return sb.toString();
    }
}
