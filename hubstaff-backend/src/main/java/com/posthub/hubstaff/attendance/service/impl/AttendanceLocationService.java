package com.posthub.hubstaff.attendance.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import lombok.extern.slf4j.Slf4j;

/** Resolves a concise location label without exposing a street number to the UI. */
@Service
@Slf4j
public class AttendanceLocationService {

    private final RestClient restClient;
    private final boolean enabled;
    private final Map<String, String> displayNameCache = new ConcurrentHashMap<>();
    private final long minimumIntervalMs;
    private long nextLookupAt;

    public AttendanceLocationService(
            @Value("${geocoding.enabled:true}") boolean enabled,
            @Value("${geocoding.provider-url}") String providerUrl,
            @Value("${geocoding.user-agent}") String userAgent,
            @Value("${geocoding.minimum-interval-ms:1100}") long minimumIntervalMs) {
        this.enabled = enabled;
        this.minimumIntervalMs = minimumIntervalMs;
        this.restClient = RestClient.builder()
                .baseUrl(providerUrl)
                .defaultHeader("User-Agent", userAgent)
                .build();
    }

    public String resolveDisplayName(BigDecimal latitude, BigDecimal longitude) {
        if (!enabled || latitude == null || longitude == null) {
            return "Location captured";
        }

        String cacheKey = coordinateKey(latitude, longitude);
        return displayNameCache.computeIfAbsent(cacheKey, ignored -> lookup(latitude, longitude));
    }

    private synchronized String lookup(BigDecimal latitude, BigDecimal longitude) {
        try {
            long delayMs = nextLookupAt - System.currentTimeMillis();
            if (delayMs > 0) {
                Thread.sleep(delayMs);
            }
            nextLookupAt = System.currentTimeMillis() + minimumIntervalMs;
            NominatimResponse response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .queryParam("format", "jsonv2")
                            .queryParam("addressdetails", 1)
                            .queryParam("lat", latitude)
                            .queryParam("lon", longitude)
                            .build())
                    .retrieve()
                    .body(NominatimResponse.class);
            return response == null ? "Location captured" : conciseDisplayName(response.address());
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            return "Location captured";
        } catch (RuntimeException exception) {
            log.warn("Reverse geocoding failed; attendance will retain coordinates only", exception);
            return "Location captured";
        }
    }

    private String conciseDisplayName(Map<String, String> address) {
        if (address == null || address.isEmpty()) {
            return "Location captured";
        }

        Set<String> parts = new LinkedHashSet<>();
        add(parts, address, "amenity", "building", "road", "suburb", "neighbourhood");
        add(parts, address, "city", "town", "village", "municipality", "state");
        return parts.isEmpty() ? "Location captured" : String.join(", ", parts.stream().limit(3).toList());
    }

    private void add(Set<String> parts, Map<String, String> address, String... keys) {
        for (String key : keys) {
            String value = address.get(key);
            if (value != null && !value.isBlank()) {
                parts.add(value);
                return;
            }
        }
    }

    private String coordinateKey(BigDecimal latitude, BigDecimal longitude) {
        return latitude.setScale(4, RoundingMode.HALF_UP) + "," + longitude.setScale(4, RoundingMode.HALF_UP);
    }

    private record NominatimResponse(Map<String, String> address) {
    }
}
