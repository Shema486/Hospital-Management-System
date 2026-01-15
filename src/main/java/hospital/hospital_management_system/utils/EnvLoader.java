package hospital.hospital_management_system.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class EnvLoader {
    
    private static final Map<String, String> envVars = new HashMap<>();
    
    static {
        loadEnv();
    }
    
    private static void loadEnv() {
        String envPath = ".env";
        try (BufferedReader reader = new BufferedReader(new FileReader(envPath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                
                String[] parts = line.split("=", 2);
                if (parts.length == 2) {
                    envVars.put(parts[0].trim(), parts[1].trim());
                }
            }
        } catch (IOException e) {
            System.err.println("Warning: .env file not found, using default values");
        }
    }
    
    public static String get(String key, String defaultValue) {
        return envVars.getOrDefault(key, defaultValue);
    }
}
