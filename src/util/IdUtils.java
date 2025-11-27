package util;

import java.util.UUID;

/**
 * Utility methods related to UUID handling. Provides convenience methods for
 * generation and parsing of UUIDs.
 */
public final class IdUtils {
    private IdUtils() {
        // utility class - prevent instantiation
    }

    public static UUID newId() { return UUID.randomUUID(); }

    public static UUID parse(String value) {
        try {
            return UUID.fromString(value.trim());
        } catch (Exception e) {
            return null;
        }
    }
}
