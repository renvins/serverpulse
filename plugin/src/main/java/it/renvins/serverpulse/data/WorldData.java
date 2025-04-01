package it.renvins.serverpulse.data;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class WorldData {

    private final int entities;
    private final int loadedChunks;
}
