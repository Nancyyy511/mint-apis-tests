package utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Centralized configuration:
 * - baseUrl: -DbaseUrl > env MINT_BASE_URL > config.properties (baseUrl)
 * - headers: config.properties keys starting with "headers."
 * - secrets: env first, fallback to config.properties for local testing
 */
public final class Config {
    private static final String CONFIG_RESOURCE = "/config.properties";
    private static final Properties PROPS = loadProps();

    private Config() {}

    private static Properties loadProps() {
        Properties p = new Properties();
        try (InputStream in = Config.class.getResourceAsStream(CONFIG_RESOURCE)) {
            if (in == null) {
                // Allow running without the file; callers will fail with a clearer message where needed.
                return p;
            }
            p.load(in);
            return p;
        } catch (IOException e) {
            throw new RuntimeException("Failed to load " + CONFIG_RESOURCE, e);
        }
    }

    public static String baseUrl() {
        String sys = System.getProperty("baseUrl");
        if (isNonBlank(sys)) return sys.trim();

        String env = System.getenv("MINT_BASE_URL");
        if (isNonBlank(env)) return env.trim();

        String fromFile = PROPS.getProperty("baseUrl");
        if (isNonBlank(fromFile)) return fromFile.trim();

        throw new IllegalStateException("Missing baseUrl. Provide -DbaseUrl, MINT_BASE_URL, or config.properties baseUrl.");
    }

    public static Map<String, String> defaultHeaders() {
        Map<String, String> headers = new LinkedHashMap<>();
        for (String key : PROPS.stringPropertyNames()) {
            if (!key.startsWith("headers.")) continue;
            String headerName = key.substring("headers.".length());
            String value = PROPS.getProperty(key);
            if (isNonBlank(headerName) && value != null) {
                headers.put(headerName, value);
            }
        }
        return headers;
    }

    public static String username() {
        return secret("MINT_USERNAME", "mint.username");
    }

    public static String password() {
        return secret("MINT_PASSWORD", "mint.password");
    }

    public static String pin() {
        return secret("MINT_PIN", "mint.pin");
    }

    public static String securityAnswer() {
        return secret("MINT_SECURITY_ANSWER", "mint.securityAnswer");
    }

    public static int useBiometric() {
        String s = PROPS.getProperty("auth.useBiometric", "1");
        try {
            return Integer.parseInt(s.trim());
        } catch (NumberFormatException e) {
            return 1;
        }
    }

    private static String secret(String envKey, String propKey) {
        String env = System.getenv(envKey);
        if (isNonBlank(env)) return env.trim();

        String fromFile = PROPS.getProperty(propKey);
        if (isNonBlank(fromFile)) return fromFile.trim();

        return null;
    }

    public static void requireNonBlank(String value, String name, String hint) {
        if (isNonBlank(value)) return;
        throw new IllegalStateException("Missing required config: " + name + ". " + hint);
    }

    private static boolean isNonBlank(String s) {
        return s != null && !s.trim().isEmpty();
    }
}

