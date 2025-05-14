package com.example.LAT2025.service.impl;

import com.example.LAT2025.model.Currency;
import com.example.LAT2025.model.api.FrankfurterResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.math.BigDecimal;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Client for interacting with the Frankfurter currency exchange API
 */
@Component
public class FrankfurterApiClient {
    
    private static final Logger logger = LoggerFactory.getLogger(FrankfurterApiClient.class);
    private static final String BASE_URL = "https://api.frankfurter.app";
    
    private final WebClient webClient;
    
    public FrankfurterApiClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl(BASE_URL).build();
    }
    

    public Map<Currency, BigDecimal> fetchRates(Currency baseCurrency) {
        try {
            String symbols = Stream.of(Currency.values())
                    .filter(currency -> currency != baseCurrency)
                    .map(Enum::name)
                    .collect(Collectors.joining(","));
            
            FrankfurterResponse response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/latest")
                            .queryParam("from", baseCurrency.name())
                            .queryParam("to", symbols)
                            .build())
                    .retrieve()
                    .bodyToMono(FrankfurterResponse.class)
                    .block();
            
            if (response != null && response.getRates() != null) {
                logger.info("Successfully fetched exchange rates from Frankfurter API");
                
                return response.getRates().entrySet().stream()
                        .filter(entry -> {
                            try {
                                Currency.valueOf(entry.getKey());
                                return true;
                            } catch (IllegalArgumentException e) {
                                logger.warn("Ignoring unknown currency: {}", entry.getKey());
                                return false;
                            }
                        })
                        .collect(Collectors.toMap(
                                entry -> Currency.valueOf(entry.getKey()),
                                Map.Entry::getValue
                        ));
            }
            
            logger.warn("Received empty or null response from Frankfurter API");
            return null;
            
        } catch (WebClientResponseException e) {
            logger.error("API error: {} - {}", e.getStatusCode(), e.getMessage());
            return null;
        } catch (Exception e) {
            logger.error("Error fetching rates: {}", e.getMessage());
            return null;
        }
    }
} 