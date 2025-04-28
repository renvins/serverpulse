package it.renvins.serverpulse.common.config;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class DatabaseConfiguration {

    private String host;
    private String bucket;
    private String org;
    private String token;
}
