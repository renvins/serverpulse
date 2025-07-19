package it.renvins.serverpulse.common.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class DatabaseConfiguration {

    private final String host;
    private final String org;
    private final String token;
    private final String bucket;
}
