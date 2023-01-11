package celtab.swge.util;

import java.util.UUID;

public interface UUIDUtils {

    default String getRandomUUIDString() {
        return getRandomUUID().toString();
    }

    default UUID getRandomUUID() {
        return UUID.randomUUID();
    }
}
