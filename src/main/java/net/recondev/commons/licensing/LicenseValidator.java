package net.recondev.commons.licensing;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@SuppressWarnings("all")
public class LicenseValidator {

    public static boolean validateLicense(final String licenseKey, final String product) {
        final String apiUrl = "http://103.195.102.32:49304/api/client";
        final String apiKey = "G1gje4OBpsAr5gpkqvEgAiRHdumxIGUo";

        try {
            final URL url = new URL(apiUrl);
            final HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", apiKey);
            connection.setDoOutput(true);

            final String jsonInputString = String.format("{\"licensekey\": \"%s\", \"product\": \"%s\"}", licenseKey, product);

            try(final OutputStream outputStream = connection.getOutputStream()) {
                final byte[] input = jsonInputString.getBytes("utf-8");
                outputStream.write(input, 0, input.length);
            }

            try(final BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
                final StringBuilder response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }


                return response.toString().contains("\"status_id\":\"SUCCESS\"");

            }
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }

    }
}
/*
String key = getConfig().getString("license-key");
String product = "ReconDevCommons";
getLogger().info("Validating license...");
if (LicenseValidator.validateLicense(key, product)) {
    getLogger().info("License is valid!");
} else {
    getLogger().log(Level.SEVERE, "License is invalid!");
    getServer().getPluginManager().disablePlugin(this);
}

This is a basic implementation of the license system
 */
